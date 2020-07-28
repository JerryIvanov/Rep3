package Main;

import AdminsPanel.ManagerDatabase;
import Bot.Bot;
import LineAndStation.LineManager;
import Users.UserKeyboard;
import Users.UsersManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import java.util.Date;
import Meeting.SessionMeets;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;


public class Main {

    static final Logger rootLogger = LogManager.getRootLogger();
    private static final Logger userLogger = LogManager.getLogger(Bot.class);
    public static final String DB_URL = ("URL");
    private static final String userName = "gssoomqcinrwxx";
    private static final String password = "pass";
    public static final String DB_Driver = "org.postgresql.Driver";
    private static UsersManager usersManager;
    private static LineManager lineManager;
    private static  UserKeyboard userKeyboard;
    private static ManagerDatabase managerDatabase;

    public static void main(String[] args) {
        /*System.getProperties().put("proxySet", "true");
        System.getProperties().put("socksProxyHost", "127.0.0.1");
        System.getProperties().put("socksProxyPort", "9150");*/
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        managerDatabase = new ManagerDatabase();
        lineManager = new LineManager();
        usersManager = UsersManager.getUsersManagerInstance();
        userKeyboard = new UserKeyboard();
        Bot bot = new Bot();
        usersManager.setBots(bot);
        sleepTimeToDelete();
        userLogger.info("start");
        try {
            telegramBotsApi.registerBot(bot);
            Class.forName(DB_Driver);
            userLogger.info("Connect to DB");
            createTables();
        }catch (TelegramApiRequestException e){
            e.printStackTrace();
        }
        catch (ClassNotFoundException e){
            e.printStackTrace();
            System.out.println("JDBC not found");
        }
    }

    private static void createTables(){
        ManagerDatabase.createMasterPassTable();
        ManagerDatabase.createTableUsers();
        ManagerDatabase.createTableAdmins();
    }


    public static ManagerDatabase getManagerDatabase() {
        return managerDatabase;
    }

    public static Connection getConnect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void sleepTimeToDelete(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String currentTime = simpleDateFormat.format(new Date());
        //userLogger.info("Date = " + currentTime);
        int hours = (Integer.parseInt(currentTime.split(":")[0]));
        int HH = ((Integer.parseInt(currentTime.split(":")[0])) + 3) * 60 * 60 * 1000;
        int mm = (Integer.parseInt(currentTime.split(":")[1])) * 60 * 1000;
        int ss = (Integer.parseInt(currentTime.split(":")[2])) * 1000;
        int timeToSleep = 24 * 60 * 60 * 1000;

        //go to

            timeToSleep = timeToSleep - HH - mm - ss;
        userLogger.info("Time to clearlist of meetings in minutes = " + TimeUnit.MILLISECONDS.toMinutes(timeToSleep));
        new SessionMeets(true, null, null, timeToSleep).start();
    }
}
