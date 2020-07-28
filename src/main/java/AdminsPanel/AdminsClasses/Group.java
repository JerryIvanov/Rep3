package AdminsPanel.AdminsClasses;

import AdminsPanel.UsersClasses.Member;

import java.util.Map;

public class Group {
    private Map<Integer, Member> usersThisAdmin;
    private int adminId;
    private String groupName;
    private String password;

    public Group(Map<Integer, Member> usersThisAdmin, int adminId, String groupName, String password) {
        this.usersThisAdmin = usersThisAdmin;
        this.adminId = adminId;
        this.groupName = groupName;
        this.password = password;
    }


    public Map<Integer, Member> getUsersThisAdmin() {
        return usersThisAdmin;
    }


    public String getGroupName() {
        return groupName;
    }


    public int getAdminId() {
        return adminId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
