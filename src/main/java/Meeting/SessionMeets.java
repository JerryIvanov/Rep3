package Meeting;

import Main.Main;
import Users.Session;
import Users.UserThread;
import Users.UsersManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.*;

public class SessionMeets extends Thread{
    private Logger sessionLogger = LogManager.getLogger(SessionMeets.class);
    private Long idSession = (long)0;
    private static Map<Long, String> timeActual = new HashMap<>();
    private static Map<Long, BuildMeeting> buildMeetings = Meetings.getBuildMeetings();
    private int timeSession;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH.mm.ss");
    private BuildMeeting thread;
    private Meeting meeting;
    private String currentDate;
    private boolean isThread;
    private boolean collector;


    public SessionMeets (boolean collector, BuildMeeting thread, Meeting meeting, int timeSession){
        if(thread != null) {
            this.isThread = true;
            this.thread = thread;
            this.idSession = thread.getThread().getChatUserId();
        }
        else if(meeting != null){
            this.isThread = false;
            this.meeting = meeting;
            this.idSession = meeting.getIdMeeting();

        }
        this.currentDate = simpleDateFormat.format(new Date());
        this.timeSession = timeSession;
        this.collector = collector;
    }

    @Override
    public void run() {
        try {
            if (idSession != 0) {
                timeActual.remove(idSession);
                timeActual.put(idSession, currentDate);
                Thread.sleep(timeSession);
                sessionLogger.info(idSession + " - Время последней активности " + timeActual.get(idSession));
                sessionLogger.info(idSession + " - Текущее время " + simpleDateFormat.format(new Date()));
                if(timeActual.containsKey(idSession) && checkTimePassed(timeActual.get(idSession))) {
                    timeActual.remove(idSession);
                    if (isThread && (!thread.isInterrupted())) {
                        if (buildMeetings.containsKey(idSession)) {
                            buildMeetings.get(idSession).getThread().setStep(0d);
                            buildMeetings.get(idSession).interrupt();
                            buildMeetings.remove(idSession);
                        }
                        sessionLogger.info("Запрос на размещение предложения пользователя - " + idSession + " закрыт.");
                    } else {
                        if (meeting != null)
                            meeting.destroyThis();
                    }
                }
            }
            else Thread.sleep(timeSession);
            if(collector){
                List<Meeting> list = new ArrayList<Meeting>();
                for (Map.Entry pair: Meetings.getMeetings().entrySet()
                ) {
                    list.addAll((List<Meeting>) pair.getValue());
                }
                for (Meeting meet: list
                     ) {
                    meet.destroyThisSoftly();
                }
                Main.sleepTimeToDelete();
                UsersManager.getBot().sendMessageClearMeetings();
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
