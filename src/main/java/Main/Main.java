package Main;

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


public class Main {

    static UsersManager usersManager;
    static LineManager lineManager;
    static final Logger rootLogger = LogManager.getRootLogger();
    static final Logger userLogger = LogManager.getLogger(Bot.class);
    public static final String DB_URL = "jdbc:h2:/C:/Users/JerryIvanov/IdeaProjects/TeleBot2/db/stockExchange";
    public static final String DB_Driver = "org.h2.Driver";


    public static void main(String[] args) {
        /*System.getProperties().put("proxySet", "true");
        System.getProperties().put("socksProxyHost", "127.0.0.1");
        System.getProperties().put("socksProxyPort", "9150");*/
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        Bot bot = new Bot();
        usersManager = UsersManager.getUsersManagerInstance();
        usersManager.setBots(bot);
        lineManager = new LineManager();
        UserKeyboard userKeyboard = new UserKeyboard();
        sleepTimeToDelete();
        userLogger.info("start");
        try {
            telegramBotsApi.registerBot(bot);
            Class.forName(DB_Driver);
            Connection connection = DriverManager.getConnection(DB_URL);
            userLogger.info("Соединение с СУБД установлено");
            connection.close();
            userLogger.info("Соединение с СУБД разорвано");
        }catch (TelegramApiRequestException e){
            e.printStackTrace();
        }
        catch (ClassNotFoundException e){
            e.printStackTrace();
            System.out.println("JDBC драйвер не найден");
        }
        catch (SQLException e){
            e.printStackTrace();
            System.out.println("Ошибка SQL");
        }
    }

    public static void sleepTimeToDelete(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String currentTime = simpleDateFormat.format(new Date());
        //userLogger.info("Date = " + currentTime);
        int HH = (Integer.parseInt(currentTime.split(":")[0])) * 60 * 60 * 1000;
        int mm = (Integer.parseInt(currentTime.split(":")[1])) * 60 * 1000;
        int ss = (Integer.parseInt(currentTime.split(":")[2])) * 1000;
        int timeToSleep = 21 * 60 * 60 * 1000;

        if(timeToSleep - HH == 0 && mm == 0) timeToSleep += 4 * 60 * 60 * 1000;
        else {
            timeToSleep -= HH;
            timeToSleep -= mm;
            timeToSleep -= ss;
        }
        //int timeToSleep = 1000 * 120;
        userLogger.info("Time to clearlist of meetings in minutes = " + timeToSleep / 1000 / 60 );
        new SessionMeets(true, null, null, timeToSleep).start();
    }
}
