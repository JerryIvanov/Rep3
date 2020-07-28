package Bot;

import AdminsPanel.AdminsManager;
import Users.UsersManager;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Bot extends TelegramLongPollingBot  {
    private static final Logger botLogger = LogManager.getLogger(Bot.class);
    private static final Scheduler scheduler = new Scheduler();
    public static final long mainAdminId = 712603472;

    public Bot() {
    }

    @Override
    public synchronized void onUpdateReceived(Update update) {

        if(update.hasMessage() && update.getMessage().hasText()){
                if(update.getMessage().getText().equalsIgnoreCase("/start")
                        || update.getMessage().getText().equalsIgnoreCase("/st")){

                    if(update.getMessage().getChatId() != (long)update.getMessage().getFrom().getId()){
                        String lastName = update.getMessage().getFrom().getLastName() == null ? "" :
                               " " + update.getMessage().getFrom().getLastName();

                        sendMessage(update.getMessage().getFrom().getFirstName()
                                         + lastName + " ответил вам в личные сообщения\uD83D\uDCAC",
                                update.getMessage().getChatId());
                    }
                        scheduler.scheduleMessageText(update);
                        //UsersManager.getUsersThreads().get((long)update.getMessage().getFrom().getId()).setStep(0d);
                }
                else if(update.getMessage().getText().equalsIgnoreCase("Взять смену") ||
                update.getMessage().getText().equalsIgnoreCase("Отдать смену")){
                    sendMessage("Здесь пока ничего нет, доступен обмен встречами - Взять/Отдать встречи.",
                            (long)update.getMessage().getFrom().getId());
                }
                else if(update.getMessage().getText().equals("test")); //testMessage(update);
                else if(update.getMessage().getText().equalsIgnoreCase("/addadmin")) {
                    AdminsManager.inRequestHandler(update);
                }
                else if(update.getMessage().getText().startsWith("/debug")){
                    try {
                        scheduler.adminsAccess(update);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if(update.getMessage().getText().split(" ")[0].equals("alternate"))
                    AdminsManager.changeAdmin(update);
                else scheduler.scheduleMessageText(update);
            }
        else if(update.hasCallbackQuery()){
            if(update.getCallbackQuery().getData().startsWith("debug")){
                try {
                    scheduler.adminsAccess(update);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else scheduler.scheduleCallBack(update);
            }
        else scheduler.scheduleMessageText(update);
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
        //botLogger.info("Keyboard = " + replyKeyboardMarkup.toString());
        try {
            execute(sendMessage);
            //botLogger.info("Send start mess.");
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

    public synchronized void sendMessage(EditMessageReplyMarkup editMessageReplyMarkup){
        try {
            execute(editMessageReplyMarkup);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public synchronized void sendMessage(String setText, Long setChatId, ReplyKeyboardRemove replyKeyboardRemove) {
        //botLogger.info("Inline sendMess");
        SendMessage sendMessage = new SendMessage().setText(setText).setChatId(String.valueOf(setChatId));
        sendMessage.setReplyMarkup(replyKeyboardRemove);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public synchronized void sendMessage(SendPhoto sendPhoto){
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public synchronized void sendMessage(SendSticker sendSticker){
        try {
            execute(sendSticker);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public synchronized void sendMessage (SendChatAction sendChatAction){
        try {
            execute(sendChatAction);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public synchronized void sendMessage (AnswerCallbackQuery answerCallbackQuery){
        try {
            execute(answerCallbackQuery);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public synchronized void sendMessageClearMeetings() {
        //botLogger.info("Inline sendMess");
        SendMessage sendMessage = new SendMessage().setText("Все встречи удалены.").setChatId(String.valueOf(mainAdminId));
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


    public static long getMainAdminId() {
        return mainAdminId;
    }


    /*public String getBotUsername() {
        return "My_Testing3000Bot";
    }

    public String getBotToken() {
        return   "998003448:AAHTZ8rpvqwKG4E77iXqi7AziBHqt4rCxcg";
    }*/

    public String getBotUsername() {
        return "**CHAPPIE RoBot**";
    }

    public String getBotToken() {
        return   "token";
    }
}
