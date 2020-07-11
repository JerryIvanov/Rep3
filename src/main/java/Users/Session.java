package Users;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Session extends Thread{

    private Logger sessionLogger = LogManager.getLogger(Session.class);
    private Long idSession;
    private static Map<Long, UserThread> userThreads = UsersManager.getUsersThreads();
    private static Map<Long, String> timeActual = new HashMap<>();
    private int timeSession;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH.mm.ss");
    private String currentDate;



    public Session(UserThread thread, int timeSession){
        this.timeSession = timeSession;
        this.idSession = thread.getChatUserId();
        currentDate = simpleDateFormat.format(new Date());
        sessionLogger.info(idSession + " - Начало сессии в " + currentDate);
    }

    @Override
    public void run() {
        try {
                timeActual.remove(idSession);
                timeActual.put(idSession, currentDate);
                Thread.sleep(timeSession);
                sessionLogger.info(idSession + " - Время последней активности " + timeActual.get(idSession));
                sessionLogger.info(idSession + " - Текущее время " + simpleDateFormat.format(new Date()));
                if (timeActual.containsKey(idSession) && checkTimePassed(timeActual.get(idSession))) {
                    timeActual.remove(idSession);
                    userThreads.get(idSession).interrupt();
                    userThreads.remove(idSession);
                    UsersManager.getBot().sendMessage("⏰Ваша сессия закрыта⏰", idSession);
                }

        } catch (InterruptedException e) {
            sessionLogger.info("Сессия пользователя " + idSession + " закрыта.(sleep interrupted)");
        }
    }

    private boolean checkTimePassed(String currentDate){
        String [] newDateTime = simpleDateFormat.format(new Date()).split("\\.");
        String [] oldDateTime = currentDate.split("\\.");
        int newDateTimeNum = parseToSeconds(newDateTime);
        int oldDateTimeNum = parseToSeconds(oldDateTime);
        sessionLogger.info( idSession + " - Прошло времени сессии " + (newDateTimeNum - oldDateTimeNum));
        return ((newDateTimeNum - oldDateTimeNum)) == timeSession / 1000;
    }

    private int parseToSeconds(String [] arr){
        int HH = Integer.parseInt(arr[0]) * 60 * 60;
        int mm = Integer.parseInt(arr[1]) * 60;
        int ss = Integer.parseInt(arr[2]);
        return (HH + mm + ss);
    }
}
