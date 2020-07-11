package Steps.StepTwo;

import LineAndStation.LineManager;
import Meeting.*;
import Steps.StepOne.Step_1;
import Steps.StepZero.Step_0;
import Steps.StepsMethods;
import Users.UserThread;
import Users.UsersManager;
import Bot.Bot;

public class Step_2 implements StepsMethods {
    private static Step_2 step2 = new Step_2();
    private static Bot bot = UsersManager.getBot();

    private Step_2() {
    }

    public synchronized static Step_2 getStep2() {
        return step2;
    }

    synchronized public static void stepTwo(UserThread thread) {
        if (thread.getMessage().equalsIgnoreCase("Назад")) {
            Step_0.stepZero(thread);
        }
        else if (thread.getUpdate().hasCallbackQuery()) {
                StepsMethods.sendMetroStations(thread);
                thread.setMetroLineTakeMeet(LineManager.getMeetingsList().get(thread.getMessage()));
                thread.setStep(2.1);
        }
        else StepsMethods.sendStationNotFound(thread);
    }
}