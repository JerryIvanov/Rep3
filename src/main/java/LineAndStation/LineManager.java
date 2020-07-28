package LineAndStation;

import Meeting.Meeting;

import java.io.*;
import java.net.URL;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LineManager {
    static final Logger lineLogger = LogManager.getLogger(LineManager.class);
    private static Map<String, MetroLine> meetingsList = new LinkedHashMap<>();
    private static Map<String, MetroStation> metroStationMap = new HashMap<>();
    private static File file = new File("TargetBot/src/main/resources/metroStation/MetroStation.txt");
    private final static String pathToResMetroStation = "C:\\Users\\JerryIvanov\\IdeaProjects\\TeleBot2\\src\\" +
            "main\\resources\\metroStation\\MetroStation.txt";



    public LineManager() {
        //File file = new File("https://api.superjob.ru/2.0/suggest/town/4/metro/all/");
        //BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "CP1251"));
        int count = 0;
        String string;
        String [] arrayString;
        String line = "";
        while (count < LinesAndStations.getStr().length){
            string = LinesAndStations.getStr()[count];
            if(string.equals("")) continue;
            //lineLogger.info(string);
            arrayString = string.split("\\.");
            //lineLogger.info(arrayString.length);
            if(arrayString[1].contains("ветка")){
                line = arrayString[0].toUpperCase() + " " + arrayString[1].toUpperCase();
                meetingsList.put(line, new MetroLine(line, new ArrayList<>()));
                //lineLogger.info(meetingsList.containsKey(line) + " " + meetingsList.get(line).getNameLine());
                count++;
            }
            else {
                if(!string.isEmpty()){
                meetingsList.get(line).getMetroStations().add(new MetroStation(arrayString[1].trim().toUpperCase(), new ArrayList<>()));
                //lineLogger.info(arrayString[1].trim().toUpperCase());
                }
                count++;
            }

        }
        for (Map.Entry pair: meetingsList.entrySet()
             ) {
            MetroLine metroLine = (MetroLine) pair.getValue();
            for (MetroStation metro: metroLine.getMetroStations()
                 ) {
                metroStationMap.put(metro.getNameMetroStation(), metro);
            }
        }
    }

    public synchronized static Map<String, MetroLine> getMeetingsList() {
        return meetingsList;
    }

    public static String getPathToResMetroStation() {
        return pathToResMetroStation;
    }

    public static Map<String, MetroStation> getMetroStationMap() {
        return metroStationMap;
    }
}
