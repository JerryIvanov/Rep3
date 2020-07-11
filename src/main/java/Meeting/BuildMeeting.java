package Meeting;

import Bot.Bot;
import LineAndStation.MetroStation;
import Steps.StepZero.Step_0;
import Users.UserKeyboard;
import Users.UserThread;
import Users.UsersManager;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.text.SimpleDateFormat;
import java.util.*;

public class BuildMeeting extends Thread{
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
                if(thread.getMessage().equalsIgnoreCase("Назад")){
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
                    if(indexp < 5 && !wait){
                        bot.sendMessage(meeting.getTemplates().get(indexp), thread.getChatUserId());
                        wait = true;
                        deActive();
                        indexp++;
                        continue;
                    }
                    if ((index == 2 || index == 4) && thread.getMessage().equalsIgnoreCase("0")){
                        meeting.getParameters().set(index, meeting.getParameters().get(index) + "НЕ УКАЗАНО.");
                        wait = false;
                        active();
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
                            continue;
                        }
                    }
                    else if(index < 5){
                        meeting.getParameters().set(index, meeting.getParameters().get(index) + thread.getMessage());
                        wait = false;
                        active();
                        index++;
                    }
                    else if(index == 5){
                        metroStation.getMeetings().add(meeting);
                        if(!Meetings.getMeetings().containsKey(thread.getChatUserId())){
                            List<Meeting> list = new ArrayList<>();
                            list.add(meeting);
                            Meetings.getMeetings().put(thread.getChatUserId(), list);
                        }
                        else Meetings.getMeetings().get(thread.getChatUserId()).add(meeting);

                        meeting.setMetroStation(metroStation);
                        Meetings.getBuildMeetings().remove(thread.getChatUserId());
                        this.interrupt();
                        meeting.setIdMeeting(Meetings.amountIncrement());
                        String description = "Предложение: " + "#" + meeting.getIdMeeting() + "\n\n";
                        for (String st: meeting.getParameters()
                             ) {
                            description += st + "\n";
                        }
                        meeting.setDescriptionMeet("В районе станции - " + metroStation.getNameMetroStation() + "\n" + description);
                        String add = description + setDurationTime(meeting);
                        Map<String, String> map = new HashMap<>();
                        map.put("Удалить", ("cancel-" + meeting.getIdMeeting()));
                        meeting.setInlineKeyboardMarkup(UserKeyboard.getInlineKeyboard(map));
                        bot.sendMessage(add, thread.getChatUserId(), meeting.getInlineKeyboardMarkup());

                        thread.setStep(0d);
                        new SessionMeets(false,null, meeting, meeting.getDuration()).start();
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
        try {
            for (int i = 0; i < arr.length; i++){
                arr[i] = arr[i].trim() + " - " + (Integer.parseInt(arr[i].trim()) + 2) + ", ";
            }
        } catch (NumberFormatException e) {
            bot.sendMessage("Неверно указаны интервалы, укажите начало каждого интервала через запятую.\n " +
                            "Пример - 11,13,17", thread.getChatUserId());
            e.printStackTrace();
            return null;
        }
        String res = "";
        Arrays.sort(arr);

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
        String interval = meeting.getParameters().get(3);
        int hour1 = Integer.parseInt(interval.substring(interval.length() - 7, interval.length() - 5));
        int hour = hour1 * 60 * 60 * 1000;;
        String currentTime = simpleDateFormat.format(new Date());
        int currentTimeMillis = Integer.parseInt(currentTime.split(":")[0]) * 60 * 60 * 1000;
        currentTimeMillis += Integer.parseInt(currentTime.split(":")[1]) * 60 * 1000;
        currentTimeMillis += Integer.parseInt(currentTime.split(":")[2]) * 1000;
        String result;
        if(hour - currentTimeMillis < 0){
            result = "\nУказанные интервалы времени уже наступили, предложение не будет размещено.";
            meeting.setDuration(100);
            meeting.setHour(hour1);
        }
        else {
            result = "\nУспешно добавлено\uD83D\uDC4D" + "\nПредложение будет действительно до "
                    + hour1 + ":00\uD83D\uDD70";
            meeting.setDuration(hour - currentTimeMillis);
            meeting.setHour(hour1);
        }
        return result;
    }
}
