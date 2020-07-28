package AdminsPanel.AdminsClasses;

import AdminsPanel.NewUserProcess;
import Users.UsersManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import java.util.HashMap;
import java.util.Map;

public class SessionControl extends Thread{
    public static Map<Integer, SessionControl> adminThreads = new HashMap<>();
    private static final Logger sessionLogger = LogManager.getLogger(SessionControl.class);
    private int id;
    private NewUserProcess user;

    public SessionControl(NewUserProcess user) {
        this.id = user.getUserId();
        this.user = user;
    }

    @Override
    public void run() {
        if(adminThreads.containsKey(this.id)){
            adminThreads.get(this.id).interrupt();
            adminThreads.remove(this.id);
        }
        adminThreads.put(id, this);
        try {
            Thread.sleep(this.user.getTimeSleeping());
            if(!this.isInterrupted()){
                adminThreads.get(id).interrupt();
                adminThreads.remove(id);
                user.setStep(1);
                UsersManager.getBot().sendMessage("⏱Ваша сессия закрыта⏱", (long) id, new ReplyKeyboardRemove());
            }
        } catch (InterruptedException e) {
            sessionLogger.info("" + this.getName() + " closed by new thread.");
        }
    }
}
