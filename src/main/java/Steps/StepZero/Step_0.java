package Steps.StepZero;

import AdminsPanel.AdminsClasses.Admin;
import Bot.Bot;
import Users.UserThread;
import Users.UsersManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class Step_0 {
    private static final Logger userLogger = LogManager.getLogger(Step_0.class);
    private static Step_0 stepZero = new Step_0();
    private static Bot bot = UsersManager.getBot();

    static private KeyboardRow keyboardRowMeets = new KeyboardRow();
    static private KeyboardRow keyboardRowDays = new KeyboardRow();
    static private KeyboardRow keyboardRowHelp = new KeyboardRow();
    static private KeyboardRow keyboardRowNotify = new KeyboardRow();

    static private List<KeyboardRow> keyboardRows = new ArrayList<>();

    static private ReplyKeyboardMarkup replyKeyboardMarkup;
    static private ReplyKeyboardMarkup keyboardMarkupAdmin;
    static private SendSticker sendSticker;

    private Step_0(){};

   synchronized public static Step_0 getStepZero() {
        return stepZero;
    }

    synchronized public static void stepZero (UserThread thread){
       userLogger.info("Step zero in work.");
        if (replyKeyboardMarkup == null) {
             replyKeyboardMarkup = new ReplyKeyboardMarkup();
            keyboardRowMeets.add("Взять встречи");
            keyboardRowMeets.add("Отдать встречи");
            keyboardRowDays.add("Взять смену");
            keyboardRowDays.add("Отдать смену");
            keyboardRowHelp.add("Помощь");
            keyboardRowNotify.add("Уведомить о новых встречах");
            keyboardRows.clear();
            keyboardRows.add(keyboardRowMeets);
            keyboardRows.add(keyboardRowNotify);
            //keyboardRows.add(keyboardRowDays);
            //keyboardRows.add(keyboardRowHelp);
            replyKeyboardMarkup.setKeyboard(keyboardRows).setResizeKeyboard(true);
            sendSticker = new SendSticker().setSticker("CAACAgIAAxkBAAMFXxc9Gp2F2l9kUGch0Be25YUBFvMAAiIAA6_GURrWEfc7Bb1VZxoE")
                    .setReplyMarkup(replyKeyboardMarkup);
        }
        bot.sendMessage(sendSticker.setChatId(thread.getChatUserId()));
        bot.sendMessage("Через 5 минут с момента последней активности - сессия будет закрыта.", thread.getChatUserId());

        UsersManager.getUsersThreads().get(thread.getChatUserId()).setStep(1d);
        userLogger.info("Step zero finished.");
    }
    synchronized public static void stepZeroNoTime (UserThread thread){
        userLogger.info("Step zeroNoTime work.");
        if (replyKeyboardMarkup == null) {
            replyKeyboardMarkup = new ReplyKeyboardMarkup();
            keyboardRowMeets.add("Взять встречи");
            keyboardRowMeets.add("Отдать встречи");
            keyboardRowDays.add("Взять смену");
            keyboardRowDays.add("Отдать смену");
            keyboardRowHelp.add("Помощь");
            keyboardRows.clear();
            keyboardRows.add(keyboardRowMeets);
            //keyboardRows.add(keyboardRowDays);
            //keyboardRows.add(keyboardRowHelp);
            replyKeyboardMarkup.setKeyboard(keyboardRows).setResizeKeyboard(true);;
        }
        bot.sendMessage("Вы в начале⬇️", thread.getChatUserId(), replyKeyboardMarkup);

        UsersManager.getUsersThreads().get(thread.getChatUserId()).setStep(1d);
        userLogger.info("Step zeroNoTime finished.");
    }
    synchronized public static void startAdminStep (Admin admin){
        userLogger.info("Step start admin in work.");
        if(keyboardMarkupAdmin == null){
            keyboardMarkupAdmin = new ReplyKeyboardMarkup();
            KeyboardRow keyboardButtons = new KeyboardRow();
            List<KeyboardRow> rowList = new ArrayList<>();
            keyboardButtons.add("Встречи");
            keyboardButtons.add("Моя группа");
            rowList.add(keyboardButtons);
            keyboardMarkupAdmin.setKeyboard(rowList).setResizeKeyboard(true);
        }
        bot.sendMessage("⏳ ", (long)admin.getUserId(), keyboardMarkupAdmin);
        admin.setStep(2);
        userLogger.info("Step start admin finished.");
    }
}
