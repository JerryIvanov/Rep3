package AdminsPanel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import AdminsPanel.AdminsClasses.Admin;
import AdminsPanel.UsersClasses.Member;
import Main.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ManagerDatabase {
    private static Connection connection;
    private static final Logger baseAdminLogger = LogManager.getLogger(ManagerDatabase.class);
    private static AdminsManager adminsManager = null;
    private static UsersManagerA usersManagerA = null;
    private static ManagerDatabase managerDatabase;

    public ManagerDatabase() {
        try {
            connection = Main.getConnect();
            managerDatabase = Main.getManagerDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void close(){
        try {
            if(connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void reopenConnection(){
        try {
            if(connection == null || connection.isClosed()) connection = Main.getConnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void  executeSqlStatement(String sql, String context){
        try {
            reopenConnection();
            Statement statement = connection.createStatement();
            switch (context){
                case "CREATE" :
                case "INSERT" ://INSERT INTO products VALUES (1, 'Cheese', 9.99); команда на добавление
                case "DELETE" : {//DELETE FROM products WHERE price = 10; команда на удаление
                    statement.execute(sql);
                    break;
                }
                case "UPDATE" : {//UPDATE products SET price = 10 WHERE price = 5; Команда на обновление
                    statement.executeUpdate(sql);
                    break;
                }
            }
            close();

        } catch (SQLException e) {
            e.printStackTrace();
            //Пример запроса на получение данных SELECT ID = 2, NAME FROM CUSTOMER (получим по ид 2, поле Name)
            //EXISTS(SELECT * FROM your_table)
        }
    }

    synchronized Object [] selectSqlStatement (String sql, String type){
        try {
            reopenConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            Array array = resultSet.getArray(1);
            close();
            if (array != null) {
                switch (type){
                    case "TEXT": {
                        return (String []) array.getArray();
                    }
                    case "INTEGER" : {
                        return (Integer []) array.getArray();
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static synchronized List<Admin> createAdminsInstance (){
        List<Admin> admins = new ArrayList<>();
        try {
            reopenConnection();
            Statement statement = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

            ResultSet resultSet = statement.executeQuery("SELECT * FROM admins");
            if (resultSet.last()) {
                do {
                     int adminId = resultSet.getInt(1);
                     int alternateId = resultSet.getInt(2);
                     String groupName = resultSet.getString(3);
                     String firstName = resultSet.getString(4);
                     String lastName = resultSet.getString(5);
                     String password = resultSet.getString(6);
                     boolean owner = resultSet.getBoolean(7);
                     int idAlternate = resultSet.getInt(8);
                     admins.add(new Admin(adminId, alternateId, groupName, firstName, lastName, password,
                             owner, idAlternate));
                }while (resultSet.previous());
            }
            /*Подсказка какие поля в БД
            "admin_id INTEGER NOT NULL UNIQUE," +
                    "alternate_id INTEGER DEFAULT NULL UNIQUE," +
                    "group_name VARCHAR(100)," +
                    "first_name VARCHAR(25) NOT NULL," +
                    "last_name VARCHAR(25) NOT NULL," +
                    "login VARCHAR(15) NOT NULL," +
                    "password VARCHAR(15) NOT NULL," +
                    "owner BOOLEAN DEFAULT FALSE)"*/
        } catch (SQLException e) {
            e.printStackTrace();
        }
        close();
        return admins;
    }
    public static synchronized List<Member> createUsersInstance (){
        List<Member> members = new ArrayList<>();
        try {
            reopenConnection();
            Statement statement = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

            ResultSet resultSet = statement.executeQuery("SELECT * FROM users");

                if(resultSet.last()) {
                do {
                int userId = resultSet.getInt(1);
                int groupId = resultSet.getInt(2);
                String firstName = resultSet.getString(3);
                String lastName = resultSet.getString(4);
                members.add(new Member(userId, groupId, firstName, lastName));
            }while (resultSet.previous());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        close();
        return members;
    }

    public synchronized String getMasterPass(){
        try {
            reopenConnection();
            Statement statement = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE
            );
            //Пример запроса на получение данных SELECT ID = 2, NAME FROM CUSTOMER (получим по ид 2, поле Name)
            ResultSet resultSet = statement.executeQuery("SELECT * FROM password");
            resultSet.next();
            return resultSet.getString(2);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    /*Предикат EXISTS принимает значение TRUE, если подзапрос содержит любое количество строк,
    иначе его значение равно FALSE. Для NOT EXISTS все наоборот.
    Этот предикат никогда не принимает значение UNKNOWN.*/
    public static synchronized void createTableAdmins(){
        executeSqlStatement("CREATE TABLE IF NOT EXISTS admins(" +
                "admin_id INTEGER NOT NULL," +
                "alternate_id INTEGER DEFAULT 0," +
                "group_name VARCHAR(100)," +
                "first_name VARCHAR(25) NOT NULL," +
                "last_name VARCHAR(25) NOT NULL," +
                "password VARCHAR(16) NOT NULL," +
                "owner BOOLEAN DEFAULT FALSE," +
                "id_alternate_admin INTEGER DEFAULT 0)", "CREATE");
        adminsManager = new AdminsManager();
        baseAdminLogger.info("Base admins created.");
    }
    public static synchronized void createMasterPassTable(){
        executeSqlStatement("CREATE TABLE IF NOT EXISTS password(" +
                "id INTEGER NOT NULL UNIQUE," +
                "pass VARCHAR(16) NOT NULL)", "CREATE");//2SBqr6En5Z3jdrxW
        baseAdminLogger.info("Master pass table created.");
        //INSERT INTO products VALUES (1, 'Cheese', 9.99); команда на добавление
        /*executeSqlStatement("INSERT INTO password VALUES(" +
                "1, '2SBqr6En5Z3jdrxW')", "INSERT");*/
    }
    public static synchronized void createTableUsers(){
        executeSqlStatement("CREATE TABLE IF NOT EXISTS users(" +
                "user_id INTEGER NOT NULL UNIQUE," +
                "group_id INTEGER NOT NULL," +
                "first_name VARCHAR (25) NOT NULL," +
                "last_name VARCHAR (25) NOT NULL)", "CREATE");


    }

    public synchronized UsersManagerA getUsersManagerA() {
        return usersManagerA;
    }

    public synchronized void setUsersManagerA(UsersManagerA usersManagerA) {
        ManagerDatabase.usersManagerA = usersManagerA;
    }

    public  AdminsManager getAdminsManager() {
        return adminsManager;
    }

    public ManagerDatabase getManagerDatabase() {
        return managerDatabase;
    }
}
