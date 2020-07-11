package Users;

import java.util.HashMap;
import java.util.Map;
import Bot.*;

public class UsersManager {
    private static Map<Long, UserThread> usersThreads = new HashMap<Long, UserThread>();
    private static UsersManager usersManager = new UsersManager();
    private static Bot [] bot = new Bot[1];
    private static Map<Long, Session> userSessions = new HashMap<>();

    private UsersManager(){

    }

    public synchronized static Map<Long, Session> getUserSessions() {
        return userSessions;
    }

    public synchronized static UsersManager getUsersManagerInstance() {
        return usersManager;
    }

    public  synchronized static Map<Long, UserThread> getUsersThreads() {
        return usersThreads;
    }

    public synchronized void setBots(Bot bot) {
        if(UsersManager.bot[0] == null) UsersManager.bot[0] = bot;
    }

    public synchronized static Bot getBot() {
        return bot[0];
    }
}
