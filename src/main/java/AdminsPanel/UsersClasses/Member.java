package AdminsPanel.UsersClasses;

import AdminsPanel.AdminsClasses.Admin;
import AdminsPanel.AdminsManager;
import AdminsPanel.ManagerDatabase;
import AdminsPanel.UsersManagerA;
import Meeting.Meeting;
import Users.UsersManager;

import java.util.HashMap;
import java.util.Map;

public class Member {
    /*user_id INTEGER NOT NULL UNIQUE," +
            "group_id INTEGER NOT NULL," +
            "first_name VARCHAR (25) NOT NULL," +
            "last_name VARCHAR (25) NOT NULL)"*/
    private int userId;
    private int groupId;
    private String firstName;
    private String lastName;
    private Map<Long, Meeting> meetingMap = new HashMap<>();

    public Member(int userId, int groupId, String firstName, String lastName) {
        this.userId = userId;
        this.groupId = groupId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getUserId() {
        return userId;
    }

    public int getGroupId() {
        return groupId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Map<Long, Meeting> getMeetingMap() {
        return meetingMap;
    }

    public void deleteThis(Admin admin){
        admin.getGroup().getUsersThisAdmin().remove(userId);
        UsersManagerA.getUsersMap().remove(userId);
        ManagerDatabase.executeSqlStatement("DELETE FROM users " +
                "WHERE user_id = " + userId, "DELETE");//DELETE FROM products WHERE user_id = 10; команда на удаление
        UsersManager.getBot().sendMessage("Вы были удалены из группы.", (long)userId);
        UsersManager.getBot().sendMessage("Вы удалили пользователя: " + lastName + "" +
                " " + firstName, (long)admin.getUserId());
    }
    public void deleteThisDeb(){
        Admin admin = AdminsManager.getAdministrators().get(groupId);
        admin.getGroup().getUsersThisAdmin().remove(userId);
        UsersManagerA.getUsersMap().remove(userId);
        ManagerDatabase.executeSqlStatement("DELETE FROM users " +
                "WHERE user_id = " + userId, "DELETE");//DELETE FROM products WHERE user_id = 10; команда на удаление
        UsersManager.getBot().sendMessage("Вы были удалены из группы.", (long)userId);
        UsersManager.getBot().sendMessage("Вы удалили пользователя: " + lastName + "" +
                " " + firstName, Bot.Bot.getMainAdminId());
    }

}
