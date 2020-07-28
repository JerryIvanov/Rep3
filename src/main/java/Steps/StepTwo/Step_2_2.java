package Steps.StepTwo;

import Bot.Bot;
import LineAndStation.LineManager;
import LineAndStation.MetroLine;
import LineAndStation.MetroStation;
import Steps.StepZero.Step_0;
import Steps.StepsMethods;
import Users.UserKeyboard;
import Users.UserThread;
import Users.UsersManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.HashMap;
import java.util.Map;

public class Step_2_2 {
    private static Bot bot = UsersManager.getBot();
    private static final Logger stepLogger = LogManager.getLogger(Step_2_2.class);

    private Step_2_2(){};

    public synchronized static void step_2_2 (UserThread thread){
        if (thread.getMessage().equalsIgnoreCase("Назад")) {
            Step_0.stepZero(thread);
        }
        else if(thread.getUpdate().hasCallbackQuery()){
            Map<String, String> map = new HashMap<>();
            if(LineManager.getMeetingsList().containsKey(thread.getMessage())){
                MetroLine metroLine = LineManager.getMeetingsList().get(thread.getMessage());
                for (MetroStation metro: metroLine.getMetroStations()) {
                    map.put(metro.getNameMetroStation(), metro.getNameMetroStation());
                }
                bot.sendMessage(("Ⓜ" + metroLine.getNameLine() + "Ⓜ️"), thread.getChatUserId(), UserKeyboard.getInlineKeyboard(map));
                bot.sendMessage(("Выберите станцию.\n" +
                        "Новые предложения по выбранной станции сразу же поступят вам\uD83D\uDE3C"), thread.getChatUserId());
                thread.setStep(3.2);
                stepLogger.info("Send metro station for user...");
            }
        }
    }
}
