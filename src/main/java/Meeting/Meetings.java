package Meeting;

import Bot.Scheduler;
import Steps.StepOne.Step_1;
import Users.UserThread;
import Users.UsersManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Meetings {
    private static final Map<Long, List<Meeting>> meetings = new HashMap<>();
    private static final Map<Long, BuildMeeting> buildMeetings = new HashMap<Long, BuildMeeting>();
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
        long id = (long)update.getCallbackQuery().getFrom().getId();
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
            if(!found) UsersManager.getBot().sendMessage(("Предложение - " + "#" + idMeeting +
                    " было удалено ранее"), id);
        }
        else {
            UsersManager.getBot().sendMessage("У вас нет активных предложений.", id);
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

        String description;
        Meeting meeting = null;
        for (Meeting meet: meetings.get(idTargetUser)
             ) {
            if(meet.getIdMeeting() == idMeeting) {
                meeting = meet;
                break;
            }
        }
        if(meeting != null) {
            UsersManager.getBot().sendMessage(meeting.getDescriptionMeet(), idTargetUser, meeting.getInlineKeyboardMarkup());
            UsersManager.getBot().sendMessage("\uD83D\uDCE8Ваш запрос был отправлен, теперь владелец предложения сможет связаться с вами\uD83D\uDCE8"
            ,idThisUser);
            UsersManager.getBot().sendMessage("Заинтересовало пользователя⬇️", idTargetUser);
            UsersManager.getBot().sendMessage("Свяжитесь с ним для согласования деталей передачи встреч(и)" +
                "\n\n⚠️Пожалуйста удалите свое предложение если у вас получилось договориться о передаче конверта(ов)⚠️️", idTargetUser);
        }
        else UsersManager.getBot().sendMessage("Предложение было удалено ранее.", idThisUser);
    }

}
