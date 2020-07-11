package Users;

import LineAndStation.MetroLine;
import LineAndStation.MetroStation;
import Meeting.Meeting;
import Meeting.Meetings;
import Steps.StepOne.Step_1;
import Steps.StepThree.Step_3_1;
import Steps.StepTwo.Step_2;
import Steps.StepThree.Step_3;
import Steps.StepTwo.Step_2_1;
import Steps.StepZero.Step_0;
import org.telegram.telegrambots.meta.api.objects.Update;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserThread extends Thread{

    private static final Logger userLogger = LogManager.getLogger(UserThread.class);
    private MetroLine metroLineGiveMeet;
    private MetroLine metroLineTakeMeet;
    private Double step = 0.0;
    private Long chatUserId;
    private String firstName;
    private String lastName;
    private boolean sessionIsActive;
    private Update update;
    private String message;
    private final static int timeSessionUserThread = (1000 * 60) * 5;
    private int messageId;

    public UserThread (Update update){
        if (update.hasCallbackQuery()) chatUserId = (long) update.getCallbackQuery().getFrom().getId();
        else chatUserId = (long)update.getMessage().getFrom().getId();
        firstName = update.getMessage().getFrom().getFirstName();
        lastName = update.getMessage().getFrom().getLastName();
        this.update = update;
    }



    @Override
    public void run() {
        try {
                new Session(this, timeSessionUserThread).start();
                UsersManager.getUsersThreads().put(chatUserId, this);

                userLogger.info("Экземпляр добавлен - " + UsersManager.getUsersThreads().containsValue(this));
                UsersManager.getBot().sendMessage("Через 5 минут неактивности ваша сессия будет закрыта.", chatUserId);
                activationSession(update);

            while (!this.isInterrupted()){
                if(!sessionIsActive) Thread.sleep(300);
                if(sessionIsActive){
                    messageHandler();
                    deactivateSession();
                }

            }
        } catch (Exception e) {
            //e.printStackTrace();
            userLogger.info("Thread user " + chatUserId + " is stopped");
        }
    }

    private synchronized void messageHandler ()  {
        if(step == 0.0){
            userLogger.info("step 0.0 worked");
           Step_0.stepZero(this);
           this.metroLineGiveMeet = null;
            step = 1d;
        }
        else if (step == 1d) {
            userLogger.info("Step 1 worked.");
            Step_1.stepOne(this);
        }
        else if (step == 2d) {
            userLogger.info("Step 2 worked.");
            Step_2.stepTwo(this);
        }
        else if(step == 2.1){
            userLogger.info("Step 2.1 worked.");
            Step_2_1.step_2_1(this);
        }
        else if (step == 3d){
            userLogger.info("Step 3 worked.");
            Step_3.step_3(this);
        }
        else if(step == 3.1){
            userLogger.info("Step 3.1 worked.");
            new Step_3_1(this).start();
        }
        else if (step == 3.01){
            userLogger.info("Step 3.01 worked");
            Meetings.addMeeting(this);
        }
    }
    public synchronized void activationSession(Update update){
        if(update.hasCallbackQuery()) {
            message = update.getCallbackQuery().getData().toUpperCase();
        }
        else message = update.getMessage().getText().toUpperCase();
        this.update = update;
        new Session(this, timeSessionUserThread).start();
        sessionIsActive = true;
        userLogger.info("Сессия активна.");
    }

    private synchronized void deactivateSession(){
        sessionIsActive = false;
        userLogger.info("Сессия неактивна.");
    }

    public synchronized void setStep(Double step) {
        this.step = step;
    }

    public Update getUpdate() {
        return update;
    }

    public String getMessage() {
        return message;
    }

    public Long getChatUserId() {
        return chatUserId;
    }

    public Double getStep() {
        return step;
    }

    public void setMetroLineGiveMeet(MetroLine metroLineGiveMeet) {
        this.metroLineGiveMeet = metroLineGiveMeet;
        userLogger.info("MetroLine = " + this.metroLineGiveMeet.getNameLine());
    }

    public MetroLine getMetroLineGiveMeet() {
        return metroLineGiveMeet;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setMetroLineTakeMeet(MetroLine metroLineTakeMeet) {
        this.metroLineTakeMeet = metroLineTakeMeet;
    }

    public MetroLine getMetroLineTakeMeet() {
        return metroLineTakeMeet;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getMessageId() {
        return messageId;
    }
}





