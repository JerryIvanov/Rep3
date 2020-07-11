package LineAndStation;

import Meeting.Meeting;

import java.util.ArrayList;
import java.util.List;

public class MetroStation {
    private  String NameMetroStation;

    private List<Meeting> meetings;


    public MetroStation(String nameMetroStation, List<Meeting> meetings) {
        NameMetroStation = nameMetroStation;
        this.meetings = meetings;
    }

    public synchronized String getNameMetroStation() {
        return NameMetroStation;
    }

    public List<Meeting> getMeetings() {
        return meetings;
    }

    }
