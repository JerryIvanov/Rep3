package Steps.StepThree;

import Bot.Bot;
import LineAndStation.LineManager;
import LineAndStation.MetroLine;
import LineAndStation.MetroStation;
import Meeting.*;
import Steps.StepTwo.Step_2_1;
import Steps.StepsMethods;
import Users.UserThread;
import Users.UsersManager;
import com.google.inject.internal.cglib.core.$CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Step_3_1 extends Thread{
    private UserThread thread;
    private static final Logger stepLogger = LogManager.getLogger(Step_3_1.class);
    private static Bot bot = UsersManager.getBot();

    public Step_3_1(UserThread thread){
        this.thread = thread;
    }


    @Override
    public void run() {
        if(thread.getMessage().equalsIgnoreCase("Назад")){
            StepsMethods.sendMetroLines(thread, 1);
            thread.setStep(3d);
        }
        else if (thread.getUpdate().hasCallbackQuery()){
                 if(LineManager.getMeetingsList().containsKey(thread.getMessage())){
                     StepsMethods.sendMetroStations(thread);
                 }
                 else {
                     stepLogger.info("3_1:MetroLine = " + thread.getMetroLineGiveMeet().getNameLine());
                     Meetings.addMeeting(thread);
                     thread.setStep(3.01);
                 }
        }
    }
}
