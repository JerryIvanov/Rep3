package LineAndStation;

import java.util.List;

public class MetroLine {
    private String nameLine;
    private List<MetroStation> metroStations;

    public MetroLine (String nameLine, List<MetroStation> metroStations){
        this.nameLine = nameLine;
        this.metroStations = metroStations;
    }

    public synchronized String getNameLine() {
        return nameLine;
    }

    public synchronized List<MetroStation> getMetroStations() {
        return metroStations;
    }
}
