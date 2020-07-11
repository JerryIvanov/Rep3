package Steps;

import Bot.Bot;
import LineAndStation.LineManager;
import LineAndStation.MetroLine;
import LineAndStation.MetroStation;
import Meeting.Meeting;
import Steps.StepOne.Step_1;
import Users.UserKeyboard;
import Users.UserThread;
import Users.UsersManager;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.HashMap;
import java.util.Map;

public interface StepsMethods {
    Bot bot = UsersManager.getBot();


    static MetroLine sendMetroStations(UserThread thread){
        synchronized (LineManager.getMeetingsList()) {
            if(LineManager.getMeetingsList().containsKey(thread.getMessage())){
                MetroLine metroLine = LineManager.getMeetingsList().get(thread.getMessage());
                InlineKeyboardMarkup inlineKeyboardMarkup = UserKeyboard.getKeyboardButtonsStations(metroLine);
                bot.sendMessage(("Ⓜ" + metroLine.getNameLine() + "Ⓜ️"), thread.getChatUserId(), inlineKeyboardMarkup);
                bot.sendMessage("Цифра перед станцией - количество предложений на этой станции.", thread.getChatUserId());
                return metroLine;
            }
            else {
                sendStationNotFound(thread);
                return null;
            }
        }
    }

    static void sendMetroLines (UserThread thread, int index){
        synchronized (bot) {
            bot.sendMessage("✌️Выберите ветку метро или введите название станции для просмотра доступных встреч✌️"
                    , thread.getChatUserId(), Step_1.getInlineKeyboardMarkupListLines().get(0));
            bot.sendMessage("☝️", thread.getChatUserId(),
                    new ReplyKeyboardMarkup().setResizeKeyboard(true).setKeyboard(UserKeyboard.getReplyKeyboards().get(index)));
            thread.setStep(2d);
        }
    }

    static void sendStationNotFound (UserThread thread){
        synchronized (StepsMethods.class) {
            if(thread.getStep() == 2){
                sendMetroLines(thread, 0);
            }
            else if(thread.getStep() == 2.1){
                sendMetroLines(thread, 1);
            }
            bot.sendMessage("\uD83D\uDD19Станция не найдена, попробуйте еще\uD83D\uDD19", thread.getChatUserId());
        }
    }

    static void sendOffersOnStation (UserThread thread){
    synchronized (LineManager.getMeetingsList()){
        MetroStation metroStation = null;
        Map<String, String> map;
        byte count = 1;
        for (MetroStation metro: thread.getMetroLineTakeMeet().getMetroStations()
             ) {
            if(metro.getNameMetroStation().equals(thread.getMessage())) metroStation = metro;
        }
        if(metroStation != null)
        for (Meeting meet: metroStation.getMeetings()
             ) {
            String number = "№";
            map = new HashMap<>();
            if(meet.getOwnerId() == thread.getChatUserId()) {
                number = "Это ваше предложение⚠️\n" + number;
                bot.sendMessage(number + count + "\n" +  meet.getDescriptionMeet(),
                        thread.getChatUserId());
                count++;
            }else {
                map.put("Выбрать", "takeMeet?" + meet.getIdMeeting() + ":" + meet.getOwnerId());
                bot.sendMessage(number + count + "\n" + meet.getDescriptionMeet(),
                        thread.getChatUserId(), UserKeyboard.getInlineKeyboard(map));
                count++;
            }
        }
    }
    }
}
