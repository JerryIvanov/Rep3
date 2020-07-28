package Steps.StepOne;

import Bot.Bot;
import Users.UserKeyboard;
import Users.UserThread;
import Users.UsersManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Step_1 {

    private static final Logger userLogger = LogManager.getLogger(Step_1.class);
    static private List<InlineKeyboardMarkup> inlineKeyboardMarkupListLines = new ArrayList<>();
    static private ReplyKeyboardMarkup replyKeyboardMarkupNoNotify;
    static private ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup().setResizeKeyboard(true);
    private static Step_1 stepOne = new Step_1();
    private static Bot bot = UsersManager.getBot();

    private Step_1() {
    }

    public synchronized static Step_1 getStepOne() {
        return stepOne;
    }

    public synchronized static void stepOne (UserThread thread){
        try {
            if(inlineKeyboardMarkupListLines.size() == 0) inlineKeyboardMarkupListLines.add(new InlineKeyboardMarkup()
                    .setKeyboard(UserKeyboard.getInlineKeyboardLines()));
            userLogger.info("getMessage = " + thread.getMessage());

            if(thread.getMessage().equalsIgnoreCase("Взять встречи" )) {//если приходит "взять встречи" отправляем ветки пользователю.
                //userLogger.info("size = " + inlineKeyboardMarkupListLines.size());
               // userLogger.info("Взять встречи - сработало.");
                thread.setMessageId(thread.getUpdate().getMessage().getMessageId());
                bot.sendMessage("✌️Выберите ветку метро для просмотра доступных встреч✌️"
                        , thread.getChatUserId(), inlineKeyboardMarkupListLines.get(0));
                bot.sendMessage("☝️", thread.getChatUserId(),
                        replyKeyboardMarkup.setKeyboard(UserKeyboard.getReplyKeyboards().get(0)));
                thread.setStep(2d);
            }
            else if(thread.getMessage().equalsIgnoreCase("Отдать встречи")){ //step 2.1
                bot.sendMessage("\uD83D\uDD01Выберите ветку метро для создания предложения\uD83D\uDD01️"
                ,thread.getChatUserId(), inlineKeyboardMarkupListLines.get(0));
                bot.sendMessage("☝", thread.getChatUserId()
                        , replyKeyboardMarkup.setKeyboard(UserKeyboard.getReplyKeyboards().get(1)));
                thread.setStep(3d);
            }
            else if(thread.getMessage().equalsIgnoreCase("Уведомить о новых встречах")){
                sendNotifyLines(thread);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void sendNotifyLines (UserThread thread){
        bot.sendMessage("\uD83D\uDD01Уведомить о новых встречах\uD83D\uDD01️"
                ,thread.getChatUserId(), inlineKeyboardMarkupListLines.get(0));
        if(replyKeyboardMarkupNoNotify == null){
            List<String> list = new ArrayList<>();
            list.add("Назад");
            list.add("Помощь");
            replyKeyboardMarkupNoNotify = new ReplyKeyboardMarkup().setResizeKeyboard(true)
                    .setKeyboard(UserKeyboard.getReplyKeyboardCustom(list));
        }
        bot.sendMessage("Если вы хотите получать все новые предложения по конкретной станции "
                        + "Выберите ветку метро, затем станцию.⬆️\n" +
                        "После этого все НОВЫЕ предложения размещенные на этой станции будут автоматически направлены вам.",
                thread.getChatUserId(), replyKeyboardMarkupNoNotify);
        thread.setStep(2.2);
        userLogger.info("sending info for user...");
    }

    public static List<InlineKeyboardMarkup> getInlineKeyboardMarkupListLines() {
        return inlineKeyboardMarkupListLines;
    }

}
