package Steps.StepTwo;

import Bot.Bot;
import Steps.StepsMethods;
import Users.UserThread;
import Users.UsersManager;

public class Step_2_1 {
    private static Step_2_1 step_2_1 = new Step_2_1();
    private static Bot bot = UsersManager.getBot();

    private Step_2_1(){};

    public synchronized static void step_2_1 (UserThread thread){
        if(thread.getMessage().equalsIgnoreCase("Назад")){
            StepsMethods.sendMetroLines(thread, 0);
            thread.setMetroLineTakeMeet(null);
            thread.setStep(2d);
        }
        else if(thread.getUpdate().hasCallbackQuery()){
            StepsMethods.sendOffersOnStation(thread);
        }
    }
}
