package LineAndStation;

import Meeting.Meeting;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LineManager {
    static final Logger lineLogger = LogManager.getLogger(LineManager.class);
    private static Map<String, MetroLine> meetingsList = new TreeMap<>();
    private final static String pathToResMetroStation = "C:\\Users\\JerryIvanov\\IdeaProjects\\TeleBot2\\src\\main\\resources\\metroStation\\MetroStation.txt";

    public LineManager() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(pathToResMetroStation), "CP1251"));
            String string;
            String [] arrayString;
            String line = "";
            while (reader.ready()){
                string = reader.readLine();
                if(string.equals("")) continue;
                //lineLogger.info(string);
                arrayString = string.split("\\.");
                //lineLogger.info(arrayString.length);
                if(arrayString[1].contains("ветка")){
                    line = arrayString[0].toUpperCase() + " " + arrayString[1].toUpperCase();
                    meetingsList.put(line, new MetroLine(line, new ArrayList<>()));
                    lineLogger.info(meetingsList.containsKey(line) + " " + meetingsList.get(line).getNameLine());
                }
                else {
                    if(!string.isEmpty()){
                    meetingsList.get(line).getMetroStations().add(new MetroStation(arrayString[1].trim().toUpperCase(), new ArrayList<>()));
                    lineLogger.info(arrayString[1].trim().toUpperCase());
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized static Map<String, MetroLine> getMeetingsList() {
        return meetingsList;
    }

    public static String getPathToResMetroStation() {
        return pathToResMetroStation;
    }
}
