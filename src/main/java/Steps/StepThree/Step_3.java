package Steps.StepThree;

import Bot.Bot;
import LineAndStation.LineManager;
import Meeting.Meetings;
import Steps.StepOne.Step_1;
import Steps.StepZero.Step_0;
import Steps.StepsMethods;
import Users.UserThread;
import Users.UsersManager;

public class Step_3 {
    private static Step_3 step_3 = new Step_3();
    private static Bot bot = UsersManager.getBot();

    private Step_3(){};

    public synchronized static void step_3 (UserThread thread){
        if (thread.getMessage().equalsIgnoreCase("Назад")) {
            Step_0.stepZero(thread);
        }
        else if(thread.getMessage().equalsIgnoreCase("Мои предложения")){
            Meetings.getSuggestionUser(thread);
        }
        else if(thread.getUpdate().hasCallbackQuery()){
            StepsMethods.sendMetroStations(thread);
            thread.setMetroLineGiveMeet(LineManager.getMeetingsList().get(thread.getMessage()));
            thread.setStep(3.1);
        }
        else StepsMethods.sendStationNotFound(thread);
    }
}
