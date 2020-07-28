package AdminsPanel.AdminsClasses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupManager {
    private static List<Group> groups = new ArrayList<>();

    public synchronized static void addGroup (Group group){
        groups.add(group);
    }
    public synchronized static List<Group> getGroup(){
        return groups;
    }
}
