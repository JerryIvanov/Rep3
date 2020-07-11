package Bot;

import Meeting.Meetings;
import Steps.StepZero.Step_0;
import Users.UserThread;
import Users.UsersManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Scheduler {

    private final Map<Long, UserThread> userThreadMap;
    private static final Logger schedulerLogger = LogManager.getLogger(Scheduler.class);

    public Scheduler() {

        userThreadMap = UsersManager.getUsersThreads();

    }

    public synchronized void scheduleCallBack(Update update) {
        Long id = checkInstanceToUsersThreads(update);
        if(checkCallbackCancel(update)){
            schedulerLogger.info("Запрос на удаление встречи" );
            Meetings.deleteMeeting(update);
        }
        else if(checkCallBackTake(update)){
            schedulerLogger.info("Запрос на обмен встречи");
            Meetings.takeMeeting(update);
        }
        else userThreadMap.get(id).activationSession(update);
    }

    public synchronized void scheduleMessageText(Update update) {
        long id = checkInstanceToUsersThreads(update);
        userThreadMap.get(id).activationSession(update);
    }



    private Long checkInstanceToUsersThreads(Update update){ //Проверяем есть ли экземпляр и добавляем если отсутствует
        Long key;
        if(update.hasCallbackQuery()) key = (long)update.getCallbackQuery().getFrom().getId();
        else key = (long)update.getMessage().getFrom().getId();
        if(!userThreadMap.containsKey(key)) new UserThread(update).start();
        return key;
    }

    private boolean checkCallbackCancel(Update update){
        if(update.getCallbackQuery().getData().contains("-")){
            return update.getCallbackQuery().getData().split("-")[0].equals("cancel");
        }
        else return false;
    }

    private boolean checkCallBackTake (Update update){
        if(update.getCallbackQuery().getData().contains("takeMeet")){
            return update.getCallbackQuery().getData().split("\\?")[0].equals("takeMeet");
        }
        return false;
    }
}
