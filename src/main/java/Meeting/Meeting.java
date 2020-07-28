package Meeting;

import AdminsPanel.UsersManagerA;
import Bot.Bot;
import LineAndStation.MetroStation;
import Users.UserThread;
import Users.UsersManager;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVenue;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.ArrayList;
import java.util.List;

public class Meeting {

    private SendPhoto sendPhoto;
    private String startHour;
    private List<PhotoSize> photoSizes;
    private UserThread thread;
    private String descriptionMeet;
    private long idMeeting;
    private MetroStation metroStation;
    private long ownerId;
    private String firstName;
    private String lastName;
    private int duration = 0;
    private static final List<String> templates = new ArrayList<>();
    private List<String> parameters = new ArrayList<>();
    private InlineKeyboardMarkup inlineKeyboardMarkup;


    private static final String countMeetings = "Количество встреч - Укажите сколько встреч вы отдаете(цифра).\uD83D\uDC47";
    private static final String clientsArePhoned = "Клиент(ы) на связи - да/нет/частично.\uD83D\uDC47";
    private static final String products = "Продукты - (указать по желанию) либо отправьте 0.\uD83D\uDC47";
    private static final String intervals = "Интервалы (только начало каждого интервала, через запятую) - (Пример) 11,17,21\uD83D\uDC47";
    private static final String description = "Комментарий - добавьте комментарий от себя по желанию" +
            "(например - \"Можно выдать НК\"), либо отправьте 0.\uD83D\uDC47";
    private static final String getPhoto = "Прикрепите в ответном сообщении скриншот из МП " +
            "со встречами котороые вы отдаете. Если на скриншоте видны встречи которые вы НЕ отдаете - рекомендуется" +
            " выделить графически встречи которые вы намерены отдать.\n" +
            "Сделать это можно стандартными средствами телефона.";

    private String count = "Количество встреч - ";
    private String arePhoned = "Клиент(ы) на связи - ";
    private String product = "Продукты - ";
    private String interval = "Интервалы - ";
    private String comment = "Комментарий - ";

    public Meeting(UserThread thread) {
        this.ownerId = thread.getChatUserId();
        this.firstName = thread.getFirstName();
        this.lastName = thread.getLastName();
        this.thread = thread;
        if (templates.size() == 0) {
            templates.add(countMeetings);
            templates.add(clientsArePhoned);
            templates.add(products);
            templates.add(intervals);
            templates.add(description);
            templates.add(getPhoto);
        }
        this.parameters.add(count);
        this.parameters.add(arePhoned);
        this.parameters.add(product);
        this.parameters.add(interval);
        this.parameters.add(comment);
    }

    public synchronized void destroyThis (){
        String out = "";
        if(Meetings.getMeetings().containsKey(ownerId)) Meetings.getMeetings().get(ownerId).remove(this);
        if(Meetings.getMeetings().get(ownerId).size() == 0) {
            Meetings.getMeetings().remove(ownerId);
            out = "\nУ вас не осталось активных предложений.";
        }
        metroStation.getMeetings().remove(this);
        UsersManagerA.getUsersMap().get((int)ownerId).getMeetingMap().remove(idMeeting);
        UsersManager.getBot().sendMessage(("Предложение - " + "#" + idMeeting + " удалено!" + out), ownerId);
    }

    public void destroyThisSoftly(){
        Meetings.getMeetings().remove(ownerId);
        metroStation.getMeetings().remove(this);
    }

    public long getOwnerId() {
        return ownerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public List<String> getTemplates() {
        return templates;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public long getIdMeeting() {
        return idMeeting;
    }

    public void setIdMeeting(long idMeeting) {
        this.idMeeting = idMeeting;
    }

    public InlineKeyboardMarkup getInlineKeyboardMarkup() {
        return inlineKeyboardMarkup;
    }

    public void setInlineKeyboardMarkup(InlineKeyboardMarkup inlineKeyboardMarkup) {
        this.inlineKeyboardMarkup = inlineKeyboardMarkup;
    }

    public void setMetroStation(MetroStation metroStation) {
        this.metroStation = metroStation;
    }

    public String getDescriptionMeet() {
        return descriptionMeet;
    }

    public void setDescriptionMeet(String descriptionMeet) {
        this.descriptionMeet = descriptionMeet;
    }

    public UserThread getThread() {
        return thread;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<PhotoSize> getPhotoSizes() {
        return photoSizes;
    }

    public void setPhotoSizes(List<PhotoSize> photoSizes) {
        this.photoSizes = photoSizes;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public SendPhoto getSendPhoto() {
        return sendPhoto;
    }

    public void setSendPhoto(SendPhoto sendPhoto) {
        this.sendPhoto = sendPhoto;
    }

    public String getStartHour() {
        return startHour;
    }

    public void setStartHour(String startHour) {
        this.startHour = startHour;
    }

    public MetroStation getMetroStation() {
        return metroStation;
    }
}
