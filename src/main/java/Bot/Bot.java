package Bot;

import Users.UserThread;
import Users.UsersManager;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Bot extends TelegramLongPollingBot  {
    private static final Logger botLogger = LogManager.getLogger(Bot.class);
    private static final Scheduler scheduler = new Scheduler();
    public static final long adminId = 712603472;

    public Bot() {
    }

    @Override
    public synchronized void onUpdateReceived(Update update) {

        if(update.hasMessage()){
            if(update.getMessage().hasText()){

                botLogger.info("Экземпляр " + update.getMessage().getFrom().getId() + " есть? "
                + UsersManager.getUsersThreads().containsKey((long)update.getMessage().getFrom().getId()));

                if(update.getMessage().getText().equalsIgnoreCase("/start")
                        || update.getMessage().getText().equalsIgnoreCase("/st")
                || update.getMessage().getText().equalsIgnoreCase("start")
                || update.getMessage().getText().equalsIgnoreCase("st")){

                    if(update.getMessage().getChatId() != (long)update.getMessage().getFrom().getId()){
                        String lastName = update.getMessage().getFrom().getLastName() == null ? "" :
                               " " + update.getMessage().getFrom().getLastName();

                        sendMessage(update.getMessage().getFrom().getFirstName()
                                         + lastName + " ответил вам в личные сообщения\uD83D\uDCAC",
                                update.getMessage().getChatId());
                    }
                        scheduler.scheduleMessageText(update);
                        UsersManager.getUsersThreads().get((long)update.getMessage().getFrom().getId()).setStep(0d);
                }
                else if(update.getMessage().getText().equals("test")); //testMessage(update);
                else {
                    if(update.getMessage().hasText()) scheduler.scheduleMessageText(update);
                    else scheduler.scheduleCallBack(update);
                }
            }
        }else if(update.hasCallbackQuery()){
                scheduler.scheduleCallBack(update);
            }
        }
   /* @Override
    public void onUpdateReceived(Update update) {
        botLogger.info(" Экземпляр есть? - " + UsersManager.getUsersThreads().containsKey((long)update.getMessage().getFrom().getId()));

        if(update.hasMessage()) {
            if (update.getMessage().hasText()) {
                if (update.getMessage().getText().equals("/start")) {
                    if (!UsersManager.getUsersThreads().containsKey((long) update.getMessage().getFrom().getId())) {
                        new UserThread(update).start();
                        sendMessage("⬇️ Ваш выбор ⬇️", (long) update.getMessage().getFrom().getId(), inlineKeyboardMarkup);
                        UsersManager.getUsersThreads().get((long) update.getMessage().getFrom().getId()).setStep(1.0);
                    }

                    if (update.getMessage().getChatId() != (long) update.getMessage().getFrom().getId()) { //если сообщение пришло в публичный чат
                        sendMessage(update.getMessage().getFrom().getFirstName() + " ответила Вам в личные сообщения",
                                update.getMessage().getChatId());
                    }
                }
            }
        }
        else if(update.hasCallbackQuery()){
            {
            botLogger.info("Bot - Это обратный вызов");
                UsersManager.getUsersThreads().get((long) update.getCallbackQuery().getMessage().getFrom().getId())
                        .activationSession(update);
            }
        }
        else {
            UsersManager.getUsersThreads().get((long)update.getMessage().getFrom().getId()).activationSession(update);
            botLogger.info("Bot - Это текст");
        }
    }*/

    public synchronized void sendMessage(String setText, Long setChatId, ReplyKeyboardMarkup replyKeyboardMarkup, Integer setReplyMessageId){
        SendMessage sendMessage = new SendMessage().setText(setText).setChatId(String.valueOf(setChatId));
        if(replyKeyboardMarkup != null) sendMessage.setReplyMarkup(replyKeyboardMarkup);
        if(setReplyMessageId != null) sendMessage.setReplyToMessageId(setReplyMessageId);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public synchronized void sendMessage(String setText, Long setChatId) {
        SendMessage sendMessage = new SendMessage().setText(setText).setChatId(String.valueOf(setChatId));
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public synchronized void sendMessage(String setText, Long setChatId, ReplyKeyboardMarkup replyKeyboardMarkup) {
        SendMessage sendMessage = new SendMessage().setText(setText).setChatId(String.valueOf(setChatId))
                .setReplyMarkup(replyKeyboardMarkup);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public synchronized void sendMessage(String setText, Long setChatId, InlineKeyboardMarkup inlineKeyboardMarkup) {
        //botLogger.info("Inline sendMess");
        SendMessage sendMessage = new SendMessage().setText(setText).setChatId(String.valueOf(setChatId));
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public synchronized void sendMessageClearMeetings() {
        //botLogger.info("Inline sendMess");
        SendMessage sendMessage = new SendMessage().setText("Все встречи удалены.").setChatId(String.valueOf(adminId));
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public synchronized void sendMessageToTakeMeet (Long chatId, Long fromChatId, int messageId){
        ForwardMessage forwardMessage = new ForwardMessage().setChatId(chatId)
                .setFromChatId((fromChatId))
                .setMessageId(messageId);
        //int replyMessageId = update.getMessage().getMessageId();
        //835509957
        /*SendMessage sendMessage = new SendMessage().setChatId((long) 835509957).setText("123")
                .setReplyToMessageId();*/

        try {
            execute(forwardMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public String getBotUsername() {
        return "**CHAPPIE RoBot**";
    }

    public String getBotToken() {
        return   "1113127140:AAHSKVk2hBJtxPf4zYY_imA2atiees1Weac";
    }
}
