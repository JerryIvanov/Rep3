package Meeting;

import AdminsPanel.AdminsManager;
import AdminsPanel.ManagerDatabase;
import Bot.Bot;
import LineAndStation.MetroStation;
import Main.Main;
import Steps.StepZero.Step_0;
import Users.UserKeyboard;
import Users.UserThread;
import Users.UsersManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class BuildMeeting extends Thread{
    private Logger buildLogger = LogManager.getLogger(BuildMeeting.class);
    private final UserThread thread;
    private boolean isActive;
    private boolean wait;
    private Bot bot = UsersManager.getBot();
    private Meeting meeting;
    private MetroStation metroStation;
    private int indexp = 0;
    private int index = 0;
    private static final int timeSession = (1000 * 60) * 5;
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");


    public BuildMeeting(UserThread thread){
        this.thread = thread;
        this.isActive = true;
        this.meeting = new Meeting(thread);
        this.meeting.setOwnerId(thread.getChatUserId());

        for (MetroStation metro: thread.getMetroLineGiveMeet().getMetroStations()
             ) {
            if(metro.getNameMetroStation().equalsIgnoreCase(thread.getMessage())) {
                this.metroStation = metro;
                break;
            }
        }
        Meetings.getBuildMeetings().put(thread.getChatUserId(), this);
    }


    @Override
    public void run() {
        while (!isInterrupted()){
            if(isActive){
                if(thread.getMessage() != null && thread.getMessage().equalsIgnoreCase("Назад")){
                    InlineKeyboardMarkup inlineKeyboardMarkup = UserKeyboard.getKeyboardButtonsStations(thread.getMetroLineGiveMeet());
                    bot.sendMessage(("Ⓜ" + thread.getMetroLineGiveMeet().getNameLine() + "Ⓜ️"), thread.getChatUserId()
                            , inlineKeyboardMarkup);
                    isActive = false;
                    thread.setStep(3.1);
                    Meetings.getBuildMeetings().remove(thread.getChatUserId());
                    this.interrupt();
                }
                else {
                    new SessionMeets(false,this, null, timeSession).start();
                    if(indexp < 1){
                        bot.sendMessage("❕Необходимо установить параметры передаваемой встречи" +
                                ", все ответы необходимо вводить в текстовое поле согласно условию❕", thread.getChatUserId());
                        bot.sendMessage(meeting.getTemplates().get(indexp), thread.getChatUserId());
                        wait = true;
                        deActive();
                        indexp = 1;
                        continue;
                    }
                    if(indexp < 6 && !wait){
                        bot.sendMessage(meeting.getTemplates().get(indexp), thread.getChatUserId());
                        indexp += 1;
                        wait = true;
                        deActive();
                        buildLogger.info("indexp " + index);
                        continue;
                    }
                    if ((index == 2 || index == 4) && thread.getMessage().equalsIgnoreCase("0")){
                        meeting.getParameters().set(index, meeting.getParameters().get(index) + "НЕ УКАЗАНО.");
                        wait = false;
                        active();
                        buildLogger.info("TEST 2 or 4 " + index);
                        index++;

                    }
                    else if(index == 3){
                        if(parseInterval(thread.getMessage()) != null) {
                            meeting.getParameters().set(index, meeting.getParameters().get(index) + parseInterval(thread.getMessage()));
                            wait = false;
                            active();
                            index++;
                        }
                        else {
                            wait = true;
                            deActive();
                            index = 3;
                        }
                    }
                    else if(index < 5){
                        meeting.getParameters().set(index, meeting.getParameters()
                                .get(index) + thread.getMessage());
                        wait = false;
                        active();
                        index++;
                    }
                    else if(index == 5){
                        buildLogger.info("TEST " + index);
                        meeting.setPhotoSizes(thread.getUpdate().getMessage().getPhoto());
                        if(meeting.getPhotoSizes() != null) {
                            active();
                            index++;
                        }
                        else  {
                            bot.sendMessage("Неверный формат фото, попробуйте другое.", thread.getChatUserId());
                            deActive();
                        }
                    }
                    else if(index == 6){
                        metroStation.getMeetings().add(meeting);
                        if(!Meetings.getMeetings().containsKey(thread.getChatUserId())){
                            List<Meeting> list = new ArrayList<>();
                            list.add(meeting);
                            Meetings.getMeetings().put(thread.getChatUserId(), list);
                        }
                        else Meetings.getMeetings().get(thread.getChatUserId()).add(meeting);

                        meeting.setMetroStation(metroStation);
                        Meetings.getBuildMeetings().remove(thread.getChatUserId());
                        meeting.setIdMeeting(Meetings.amountIncrement());
                        String description = "Предложение: " + "#" + meeting.getIdMeeting() + "\n\n";
                        for (String st: meeting.getParameters()
                             ) {
                            description += st + "\n";
                        }
                        meeting.setDescriptionMeet("В районе станции - " + metroStation.getNameMetroStation() + "\n" + description);
                        String add = description + setDurationTime(meeting);
                        Map<String, String> map = new HashMap<>();
                        map.put("Удалить", ("cancel-" + meeting.getIdMeeting() + "-" + meeting.getOwnerId()));
                        meeting.setInlineKeyboardMarkup(UserKeyboard.getInlineKeyboard(map));
                        SendPhoto sendPhoto = new SendPhoto();
                        sendPhoto.setChatId(thread.getChatUserId()).setCaption(add);
                        sendPhoto.setReplyMarkup(meeting.getInlineKeyboardMarkup());
                        sendPhoto.setPhoto(meeting.getPhotoSizes().get(0).getFileId());
                        bot.sendMessage(sendPhoto);
                        sendPhoto.setCaption(description);
                        meeting.setSendPhoto(sendPhoto);
                        buildLogger.info("OwnerID = " + meeting.getOwnerId());
                        if (meeting.getDuration() > 100) {
                            Main.getManagerDatabase().getAdminsManager().addMeetingUser(this.meeting);
                            new SessionMeets(false,null, meeting, meeting.getDuration()).start();
                            AdminsManager.statistics.set(0, + 1);
                        } else {
                            meeting.destroyThis();
                        }
                        isActive = false;
                        thread.setStep(0d);
                        this.interrupt();
                        Step_0.stepZero(thread);
                    }
                }
            }
        }
    }

    void active() {
        this.isActive = true;
    }
    private void deActive(){
        this.isActive = false;
    }

    private String parseInterval(String str){
        String [] arr = str.split(",");
        int [] arrInt = new int[arr.length];
        for (int i = 0; i < arr.length; i++
             ) {
            arrInt[i] = Integer.parseInt(arr[i].trim());
        }
        Arrays.sort(arrInt);
        meeting.setStartHour(Integer.toString(arrInt[arrInt.length-1]));
        try {
            for (int i = 0; i < arrInt.length; i++){
                arr[i] = arrInt[i] + " - " + (arrInt[i] + 2) + ", ";
            }

        } catch (NumberFormatException e) {
            bot.sendMessage("Неверно указаны интервалы, укажите начало каждого интервала через запятую" +
                    ".\uD83D\uDC4F\uD83C\uDFFB\n " +
                            "Пример - 11,13,17", thread.getChatUserId());
            e.printStackTrace();
            return null;
        }
        String res = "";
        meeting.setStartHour(arr[arr.length-1].split("-")[0].trim());
        for (String s : arr) {
            res += s;
        }
        res = res.substring(0, res.lastIndexOf(","));
        return res.trim();
    }

    public UserThread getThread() {
        return thread;
    }

    private String setDurationTime (Meeting meeting){
        int hour1 = Integer.parseInt(meeting.getStartHour());
        int hour = hour1 * 60 * 60 * 1000;;
        String currentTime = simpleDateFormat.format(new Date());
        int currentTimeMillis = (Integer.parseInt(currentTime.split(":")[0]) + 3) * 60 * 60 * 1000;
        currentTimeMillis += Integer.parseInt(currentTime.split(":")[1]) * 60 * 1000;
        currentTimeMillis += Integer.parseInt(currentTime.split(":")[2]) * 1000;
        String result;
        if(hour - currentTimeMillis < 0){
            result = "\nУказанные интервалы времени уже наступили, предложение будет удалено.\uD83D\uDE41";
            meeting.setDuration(100);
        }
        else {
            result = "\nУспешно добавлено\uD83D\uDC4D" + "\nПредложение будет действительно до "
                    + hour1 + ":00\uD83D\uDD70";
            meeting.setDuration(hour - currentTimeMillis);

            buildLogger.info("Meets delete after minutes " +
                    TimeUnit.MILLISECONDS.toMinutes(hour - currentTimeMillis));
        }
        return result;
    }
}
