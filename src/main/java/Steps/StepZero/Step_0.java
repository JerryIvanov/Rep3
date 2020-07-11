package Steps.StepZero;

import Bot.Bot;
import Meeting.Meeting;
import Meeting.Meetings;
import Users.UserThread;
import Users.UsersManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.objects.Update;
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

    static private List<KeyboardRow> keyboardRows = new ArrayList<>();

    static private ReplyKeyboardMarkup replyKeyboardMarkup;

    private Step_0(){};

   synchronized public static Step_0 getStepZero() {
        return stepZero;
    }

    synchronized public static void stepZero (UserThread thread){
       userLogger.info("Step zero in work.");
        if (replyKeyboardMarkup == null) {
             replyKeyboardMarkup = new ReplyKeyboardMarkup().setResizeKeyboard(true);
            keyboardRowMeets.add("Взять встречи");
            keyboardRowMeets.add("Отдать встречи");
            keyboardRowDays.add("Взять смену");
            keyboardRowDays.add("Отдать смену");
            keyboardRowHelp.add("Помощь");
            keyboardRows.clear();
            keyboardRows.add(keyboardRowMeets);
            keyboardRows.add(keyboardRowDays);
            keyboardRows.add(keyboardRowHelp);
            replyKeyboardMarkup.setKeyboard(keyboardRows);
        }
        bot.sendMessage("⏳ ", thread.getChatUserId(), replyKeyboardMarkup);

        UsersManager.getUsersThreads().get(thread.getChatUserId()).setStep(1d);
        userLogger.info("Step zero finished.");
    }
}
