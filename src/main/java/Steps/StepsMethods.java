package Steps;

import Bot.Bot;
import LineAndStation.LineManager;
import LineAndStation.MetroLine;
import LineAndStation.MetroStation;
import Meeting.Meeting;
import Steps.StepOne.Step_1;
import Steps.StepTwo.Step_2;
import Steps.StepTwo.Step_2_2;
import Users.UserKeyboard;
import Users.UserThread;
import Users.UsersManager;
import com.google.inject.internal.cglib.core.internal.$Function;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendDice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
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
                bot.sendMessage("Цифра перед станцией - количество предложений на этой станции.",
                        thread.getChatUserId(), Step_2.getReplyKeyboardMarkupNoNotify());
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
        if(metroStation != null && metroStation.getMeetings().size() > 0) {
            for (Meeting meet : metroStation.getMeetings()
            ) {
                String number = "№";
                map = new HashMap<>();
                if (meet.getOwnerId() == thread.getChatUserId()) {
                    number = "Это ваше предложение⚠️\n" + number;
                    SendPhoto sendPhoto = meet.getSendPhoto();
                    sendPhoto.setChatId(thread.getChatUserId());
                    sendPhoto.setCaption(number + count + "\n" + meet.getDescriptionMeet());
                    bot.sendMessage(sendPhoto);
                } else {
                    map.put("Выбрать", "takeMeet?" + meet.getIdMeeting() + ":" + meet.getOwnerId());
                    SendPhoto sendPhoto = meet.getSendPhoto();
                    sendPhoto.setChatId(thread.getChatUserId());
                    sendPhoto.setCaption(number + count + "\n" + meet.getDescriptionMeet());
                    sendPhoto.setReplyMarkup(UserKeyboard.getInlineKeyboard(map));
                    bot.sendMessage(sendPhoto);
                }
                count++;
            }
        }
        else {
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
            answerCallbackQuery.setCallbackQueryId(thread.getUpdate().getCallbackQuery().getId());
            answerCallbackQuery.setText("На выбранной станции предложений не найдено!");
            bot.sendMessage(answerCallbackQuery);
        }
    }
    }
}
