package Meeting;

import AdminsPanel.AdminsManager;
import AdminsPanel.UsersClasses.Member;
import AdminsPanel.UsersManagerA;
import LineAndStation.LineManager;
import LineAndStation.MetroStation;
import Steps.StepOne.Step_1;
import Users.UserKeyboard;
import Users.UserThread;
import Users.UsersManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.*;

public class Meetings {
    private static final Map<Long, List<Meeting>> meetings = new HashMap<>();
    private static final Map<Long, BuildMeeting> buildMeetings = new HashMap<Long, BuildMeeting>();
    private static final Map<Long, AcceptTakeMeet> decisionMeetings = new HashMap<>();
    private static final Meetings meetingsIns = new Meetings();

    private static long amountMeetings = 0;
    private static final Logger meetingsLogger = LogManager.getLogger(Meetings.class);

    private Meetings(){}


    public synchronized static Meetings getMeetingsIns() {
        return meetingsIns;
    }

    synchronized static Map<Long, BuildMeeting> getBuildMeetings() {
        return buildMeetings;
    }

    public synchronized static Map<Long, List<Meeting>> getMeetings() {
        return meetings;
    }

    static synchronized long amountIncrement(){
        amountMeetings++;
        return amountMeetings;
    }

    public static synchronized void decisionsMeets(Update update){
        String data = update.getCallbackQuery().getData();
        long idThisUser = Long.parseLong(data.split(":")[2]);
        long adminId = update.getCallbackQuery().getFrom().getId();
        if(decisionMeetings.containsKey(idThisUser)){
            decisionMeetings.get(idThisUser).activation(update);
            UsersManager.getBot().sendMessage("Решение отправлено.\n", adminId);
        }
        else UsersManager.getBot().sendMessage("Решение было принято ранее.\n" +
                "Либо этот запрос был удален по истечении 5 минут.", adminId);
    }

    public static synchronized void addMeeting(UserThread thread){
        if(!buildMeetings.containsKey(thread.getChatUserId())) new BuildMeeting(thread).start();
        else buildMeetings.get(thread.getChatUserId()).active();
    }

    public synchronized static List<String> getSuggestionUser (UserThread thread){
        List<String> list = new ArrayList<>();
        if (meetings.containsKey(thread.getChatUserId())) {
            for (Meeting meet: meetings.get(thread.getChatUserId())
                 ) {
                UsersManager.getBot().sendMessage(meet.getDescriptionMeet(), thread.getChatUserId(), meet.getInlineKeyboardMarkup());
                list.add(meet.getDescriptionMeet());
            }
        }
        else {
            UsersManager.getBot().sendMessage("Предложений не найдено\uD83D\uDE1E\n" +
                    "Вы можете создать новое предложение выбрав ветку метро☝️\uD83D\uDE0C",thread.getChatUserId(),
                    Step_1.getInlineKeyboardMarkupListLines().get(0));
        }


        return list;
    }

    public synchronized static void deleteMeeting(Update update){
        meetingsLogger.info("Вошли в delete");
        long idMeeting = Long.parseLong(update.getCallbackQuery().getData().split("-")[1]);
        meetingsLogger.info("Установили id встречи");
        long id = Long.parseLong(update.getCallbackQuery().getData().split("-")[2]);
        boolean found = false;
        meetingsLogger.info("meetings содержит id - " + meetings.containsKey(id));
        if(meetings.containsKey(id)){
            for (Meeting meet: meetings.get(id)
                 ) {
                if(meet.getIdMeeting() == idMeeting) {
                    meet.destroyThis();
                    found = true;
                    break;
                }
            }
            if(AdminsManager.getAdministrators().containsKey(update.getCallbackQuery().getFrom()
            .getId())){
                if(!found) UsersManager.getBot().sendMessage(("Предложение - " + "#" + idMeeting +
                        " было удалено ранее"), (long)update.getCallbackQuery().getFrom().getId());
                else {
                    UsersManager.getBot().sendMessage(("Предложение - " + "#" + idMeeting +
                            " было удалено."), (long)update.getCallbackQuery().getFrom().getId());
                    UsersManager.getBot().sendMessage("Предложение - " + "#" + idMeeting +
                            " было удалено вашим РГ или дежурным.", id);
                }
            }
            else if(!found) UsersManager.getBot().sendMessage(("Предложение - " + "#" + idMeeting +
                    " было удалено ранее."), id);
        }
        else {
            if(update.getCallbackQuery().getFrom().getId() == id)
            UsersManager.getBot().sendMessage("У вас нет активных предложений.", id);
            else {
                UsersManager.getBot().sendMessage("Это предложение было удалено ранее.", (long)update.getCallbackQuery()
                .getFrom().getId());
            }
        }
    }

    public synchronized static void takeMeeting (Update update){
        meetingsLogger.info("Callback = " + update.getCallbackQuery().getData());

        long idMeeting = Long.parseLong(update.getCallbackQuery().getData().split(":")[0].split("\\?")[1]);
        meetingsLogger.info("idMeeting = " + idMeeting);
        long idTargetUser = Long.parseLong(update.getCallbackQuery().getData().split(":")[1]);
        meetingsLogger.info("idTargetUser = " + idTargetUser);
        long idThisUser = update.getCallbackQuery().getFrom().getId();
        meetingsLogger.info("idThisUser = " + idThisUser);
        int idReplyMessage = UsersManager.getUsersThreads().get(idThisUser).getMessageId();
        meetingsLogger.info("ReplyMessageId = " + idReplyMessage);

        Meeting meeting = null;
        for (Meeting meet: meetings.get(idTargetUser)
             ) {
            if(meet.getIdMeeting() == idMeeting) {
                meeting = meet;
                break;
            }
        }
        if(meeting != null) {
            new AcceptTakeMeet(idMeeting, idTargetUser, idThisUser, idReplyMessage, meeting).start();
        }
        else UsersManager.getBot().sendMessage("Предложение было удалено ранее.", idThisUser);
    }

    public synchronized static int addNotifyForUser(UserThread thread){

        if(LineManager.getMetroStationMap().containsKey(thread.getMessage())){
           return LineManager.getMetroStationMap().get(thread.getMessage()).addNotify(thread.getChatUserId());

        }
        return 0;
    }
    private static class AcceptTakeMeet extends Thread {

        private long idMeeting;
        private long idTargetUser;
        private long idThisUser;
        private int idReplyMessage;
        private Meeting meeting;
        private long adminId;
        private boolean isActive = false;
        private boolean accept = false;


        public AcceptTakeMeet(long idMeeting, long idTargetUser, long idThisUser
                , int idReplyMessage, Meeting meeting){
            this.idMeeting = idMeeting;
            this.idTargetUser = idTargetUser;
            this.idThisUser = idThisUser;
            this.idReplyMessage = idReplyMessage;
            this.meeting = meeting;
        }
        public void activation (Update update){
            String data = update.getCallbackQuery().getData();
            accept = data.split(":")[1].equals("accept");
            isActive = true;
        }

        @Override
        public void run() {
            decisionMeetings.put(idThisUser, this);
            Member targetMember = UsersManagerA.getUsersMap().get((int)idTargetUser);
            Member thisMember = UsersManagerA.getUsersMap().get((int)idThisUser);
            adminId = targetMember.getGroupId();
            String description = "\n\nПередача встреч от сотрудника: " + targetMember.getLastName() + " " +
                    targetMember.getFirstName() + " сотруднику➡️" + thisMember.getLastName() + " " +
                    thisMember.getFirstName() + "\n" + "Согласовать передачу?";
            Map<String, String> map = new LinkedHashMap<>();
            map.put("Да", "decision:accept:" + idThisUser);
            map.put("Нет", "decision:deny:" + idThisUser);
            SendPhoto sendPhoto = new SendPhoto().setPhoto(meeting.getSendPhoto().getPhoto());
            sendPhoto.setChatId(adminId).setReplyMarkup(UserKeyboard.getInlineKeyboard(map));
            sendPhoto.setCaption(meeting.getDescriptionMeet() + description);
            UsersManager.getBot().sendMessage(sendPhoto);
            UsersManager.getBot().sendMessage("Ваш запрос был отправлен руководителю сотрудника " +
                            "разместившего предложение на согласование.\n" +
                            "В случае успешного согласования владелец " +
                            "предложения сможет связаться с вами.", idThisUser);
            while (!this.isInterrupted()){
                try {
                    Thread.sleep(300);
                    while (isActive){
                        if (accept) {
                            isActive = false;
                            UsersManager.getBot().sendMessage("\uD83D\uDCE8Предложение - " + idMeeting + "\nВаш запрос был согласован, теперь владелец предложения сможет связаться с вами\uD83D\uDCE8"
                                    ,idThisUser);
                            SendSticker sendSticker = new SendSticker();
                            sendSticker.setChatId(idThisUser).setSticker("CAACAgIAAxkBAAICt18YUayDRY0tlCJV-xBnN4NT8gVEAAIRAwACbbBCA9fbtKqOVG0_GgQ");
                            UsersManager.getBot().sendMessage(meeting.getSendPhoto().setChatId(idTargetUser)
                                    .setReplyMarkup(meeting.getInlineKeyboardMarkup()));
                            UsersManager.getBot().sendMessage("Заинтересовало пользователя⬇️", idTargetUser);
                            UsersManager.getBot().sendMessageToTakeMeet(idTargetUser, idThisUser, idReplyMessage);
                            UsersManager.getBot().sendMessage("Свяжитесь с ним для согласования деталей передачи встреч(и)" +
                                    "\n\n⚠️Пожалуйста удалите свое предложение если у вас получилось договориться о передаче конверта(ов)⚠️️", idTargetUser);
                            Thread.sleep(1000 * 60 * 5) ;
                            deleteThisIns();
                            deleteRequests();
                        }
                        else {
                            isActive = false;
                            UsersManager.getBot().sendMessage("\uD83D\uDCE8Предложение - " + idMeeting + "\nВаш запрос был отклонен.\uD83D\uDCE8"
                                    ,idThisUser);
                            deleteThisIns();

                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public long getIdMeeting() {
            return idMeeting;
        }

        public long getIdThisUser() {
            return idThisUser;
        }

        private void deleteThisIns (){
            decisionMeetings.remove(idThisUser);
            this.interrupt();
        }
        private void deleteRequests(){
            Iterator<Map.Entry<Long, AcceptTakeMeet>> entries = decisionMeetings.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<Long, AcceptTakeMeet> entry = entries.next();
                if(entry.getValue().getIdMeeting() == idMeeting){
                    UsersManager.getBot().sendMessage("\uD83D\uDCE8Предложение:#" +
                            entry.getValue().idMeeting + "\n Получил другой представитель.\uD83D\uDCE8",
                            entry.getValue().getIdThisUser());
                    decisionMeetings.get(entry.getKey()).interrupt();
                    decisionMeetings.remove(entry.getKey());
                }
            }
        }
    }
}
