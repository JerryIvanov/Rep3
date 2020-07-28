package LineAndStation;

import Bot.Bot;
import Meeting.Meeting;
import Users.UserKeyboard;
import Users.UsersManager;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetroStation {
    private  String NameMetroStation;
    private Bot bot = UsersManager.getBot();
    private List<Meeting> meetings;
    public static final List<Long> notifyList = new ArrayList<>();


    public MetroStation(String nameMetroStation, List<Meeting> meetings) {
        NameMetroStation = nameMetroStation;
        this.meetings = meetings;
    }
    public synchronized int addNotify(Long userId){
        if (!notifyList.contains(userId)) {
            notifyList.add(userId);
            return 1;
        }
        return 2;
    }

    public synchronized void sendNotifyUsers(Meeting meeting){
        if(notifyList.size() > 0){
            long ownerId = meeting.getOwnerId();
            SendPhoto sendPhoto1 = new SendPhoto();
            Map<String, String> map = new HashMap<>();
            map.put("Выбрать", "takeMeet?" + meeting.getIdMeeting() + ":" + meeting.getOwnerId());
            sendPhoto1.setCaption("Появилось новое предложение!\n" +
                    meeting.getDescriptionMeet()).setReplyMarkup(UserKeyboard.getInlineKeyboard(map));
            sendPhoto1.setPhoto(meeting.getSendPhoto().getPhoto());
                for (Long id: notifyList
                     ) {
                    if(id != ownerId) {
                        sendPhoto1.setChatId(id);
                        UsersManager.getBot().sendMessage(sendPhoto1);
                    }
                }
        }
    }
    public synchronized void clearNotifyList(){
        notifyList.clear();
    }

    public synchronized String getNameMetroStation() {
        return NameMetroStation;
    }

    public List<Meeting> getMeetings() {
        return meetings;
    }

    }
