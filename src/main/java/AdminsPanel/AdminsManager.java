package AdminsPanel;

import AdminsPanel.AdminsClasses.Admin;
import AdminsPanel.UsersClasses.Timer;
import AdminsPanel.UsersClasses.Member;
import Bot.Bot;
import Main.Main;
import Meeting.Meeting;
import Users.UserKeyboard;
import Users.UsersManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.*;

public class AdminsManager {
    private static Map<Integer, Admin> administrators = new HashMap<>();
    private static ManagerDatabase managerDatabase = Main.getManagerDatabase();
    private static final Logger adminsLogger = LogManager.getLogger(AdminsManager.class);
    private static Map<Integer, AddNewAdmin> addNewAdminMap = new HashMap<>();
    private static Bot bot = UsersManager.getBot();
    private static String masterPassword;
    public static List<Integer> statistics = new ArrayList<>(5);

    public AdminsManager (){
        for (int i = 0; i < 5; i++){
            statistics.add(0);
        }
        /*ManagerDatabase.executeSqlStatement("INSERT INTO admins (admin_id, first_name, last_name," +
                "login, password, owner) " +
                "VALUES (835509957, 'Григорий', 'Данильченко', 'postgresql', '0000', TRUE)", "INSERT");*/
        //adminsLogger.info("Addition first admin complete.");
        for (Admin admin : ManagerDatabase.createAdminsInstance()) {
            administrators.put(admin.getUserId(), admin);
        }
        managerDatabase.setUsersManagerA(new UsersManagerA());
        adminsLogger.info("Base users created.");
        administrators.clear();
        for (Admin admin : ManagerDatabase.createAdminsInstance()) {
            administrators.put(admin.getUserId(), admin);
        }
        adminsLogger.info("Administrators added to map");
        masterPassword = managerDatabase.getMasterPass();
        if(masterPassword != null) adminsLogger.info("Master pass set");
        else adminsLogger.info("Master pass = " + null);
    }


    public static synchronized void inRequestHandler(Update update) {
        int userId = update.hasCallbackQuery() ? update.getCallbackQuery().getFrom().getId()
                : update.getMessage().getFrom().getId();
        if (!addNewAdminMap.containsKey(userId) && !administrators.containsKey(userId)) {
            UsersManagerA.getAddNewUserMap().remove(userId);
            new AddNewAdmin(update, userId).start();
            bot.sendMessage("Для регистрации вас в качестве " +
                    "руководителя группы отправьте в ответном сообщении пароль.", (long)userId);
        }
        else if(!administrators.containsKey(userId)) addNewAdminMap.get(userId).activation(update);
        if(administrators.containsKey(userId)) administrators.get(userId).activation(update);
    }
    private synchronized static void addNewAdmin(AddNewAdmin addNewAdmin){
        int adminId = addNewAdmin.adminId;
        int alternateId = addNewAdmin.alternateId;
        String groupName = addNewAdmin.groupName;
        String firstName = addNewAdmin.firstName;
        String lastName = addNewAdmin.lastName;
        String password = addNewAdmin.password;
        boolean owner = addNewAdmin.owner;
        ManagerDatabase.executeSqlStatement("INSERT INTO admins VALUES(" +
                adminId + "," +
                "'" + alternateId + "'," +
                "'" + groupName + "'," +
                "'" + firstName + "'," +
                "'" + lastName + "'," +
                "'" + password + "'," +
                 owner + ")", "INSERT");
        /*executeSqlStatement("INSERT INTO password VALUES(" +
                "1, '2SBqr6En5Z3jdrxW')", "INSERT");*/
        Admin admin = new Admin(adminId, alternateId, groupName, firstName, lastName, password, owner, 0);
        administrators.put(adminId, admin);
        bot.sendMessage("Вы были успешно добавлены в качестве руководителя.\n" +
                "На ваше имя была создана группа.\n" +
                "Пароль для входа в вашу группу⬇️", (long) adminId);
        bot.sendMessage(password, (long)adminId);
        bot.sendMessage("Пароль вы сможете изменить в любой момент в панели управления группой.\n" +
                "Для начала работы отправьте команду /st", (long)adminId);
        addNewAdmin.closeProcess();
    }
    public static synchronized void changeAdmin(Update update){
        String pass = update.getMessage().getText().split(" ")[1];
        int id = update.getMessage().getFrom().getId();
        Admin admin = null;
        Admin oldAdmin = null;
        boolean found = false;
        int oldId;
        if (!UsersManagerA.getUsersMap().containsKey(update.getMessage().getFrom().getId()) &&
        AdminsManager.getAdministrators().containsKey(update.getMessage().getFrom().getId())) {
            for (Map.Entry pair: administrators.entrySet()
                 ) {
                admin = (Admin) pair.getValue();
                if(pass.equals(admin.getAlternatePass())){
                    oldId = admin.getUserId();
                    admin.setIdAlternateAdmin(id);
                    oldAdmin = admin;
                        admin = AdminsManager.getAdministrators().get(id);
                        admin.setAlternateId(oldId);
                        ManagerDatabase.executeSqlStatement("UPDATE admins " +
                                "SET alternate_id =" + oldId + "WHERE admin_id =" + id, "UPDATE");
                        ManagerDatabase.executeSqlStatement("UPDATE admins " +
                            "SET id_alternate_admin =" + id + "WHERE admin_id =" + oldId, "UPDATE");

                        found = true;
                        break;
                }
            }
            if(found){
                bot.sendMessage("Вы были назначены руководителем.\uD83D\uDE3C\n" +
                        oldAdmin.getGroup().getGroupName(), (long)update.getMessage().getFrom().getId());
                bot.sendMessage("Был назначен новый руководитель вашей группы.\uD83D\uDE40",
                        (long)oldAdmin.getUserId());
            }
            else bot.sendMessage("Неверный пароль или группа была удалена ранее.\uD83D\uDE15",
                    (long) update.getMessage().getFrom().getId());
        }else bot.sendMessage("Вы не можете стать заместителем этой группы так " +
                "как состоите в качестве участника в другой группе.\uD83D\uDE15",(long) update.getMessage().getFrom().getId());

    }
    public synchronized void addMeetingUser(Meeting meeting){
        int userId = (int)meeting.getOwnerId();
        Member member = UsersManagerA.getUsersMap().get(userId);
        int adminId = member.getGroupId();
        Map<String, String> map = new HashMap<>();
        map.put("Удалить", ("cancel-" + meeting.getIdMeeting() + "-" + meeting.getOwnerId()));
        map.put("К дежурному", ("toDuty-" + meeting.getIdMeeting()));

        String description = "Новое предложение от:\n" + member.getLastName() + " " + member.getFirstName() +
                "\n" + meeting.getDescriptionMeet();
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setCaption(description).setReplyMarkup(UserKeyboard.getInlineKeyboard(map))
                .setChatId((long) adminId);
        sendPhoto.setPhoto(meeting.getSendPhoto().getPhoto());
        adminsLogger.info("Ownerid = " + sendPhoto.getChatId());
        AdminsManager.getAdministrators().get(adminId).notifyNewMeetings(sendPhoto);
        member.getMeetingMap().put(meeting.getIdMeeting(), meeting);
    }

    public synchronized static String getNewPassword() {
        int count = 16;
        String newPass = "";
        while (count > 0) {
            int random = (int) (Math.random() * 100);
            if(random < 20){
                random += (48 - random) + (Math.random() * 10);
                newPass += (char) random;
                count--;
            }
            else if (random > 47 && random < 58) {
                newPass += (char) random;
                count--;
            }
            else if(random > 64 && random < 91){
                newPass += (char) random;
                count--;
            }
            else {
                random += (97 - random) + (Math.random() * 10) + 16;
                newPass += (char) random;
                count--;
            }
        }
        return newPass;
    }
    public synchronized static Map<Integer, Admin> getAdministrators() {
        return administrators;
    }

    public synchronized static Map<Integer, AddNewAdmin> getAddNewAdminMap() {
        return addNewAdminMap;
    }

    public static class AddNewAdmin extends Thread implements NewUserProcess {
        Update update;
        private int adminId;
        private int alternateId;
        private String groupName;
        private String firstName;
        private String lastName;
        private String password;
        private boolean owner;
        private boolean isActive = false;
        private byte step = 0;

        public AddNewAdmin(Update update, Integer adminId){
            this.update = update;
            this.adminId = adminId;
            addNewAdminMap.put(adminId, this);
            new Timer(this);
        }

        public void activation(Update update){
            this.isActive = true;
            this.update = update;
        }

        @Override
        public void closeProcess() {
            addNewAdminMap.remove(adminId);
            this.interrupt();
        }

        @Override
        public void setStep(int step) {

        }

        @Override
        public int getUserId() {
            return 0;
        }

        public int getTimeSleeping() {
            return (1000 * 60) * 5;
        }



        @Override
        public void run() {
            while (!this.isInterrupted()){
                while (this.isActive && !this.isInterrupted()){
                    if(step == 0) {
                        if (update.hasMessage() && update.getMessage().getText().equals(masterPassword)) {
                            bot.sendMessage("Укажите в ответном сообщении " +
                                    "Фамилию/Имя через пробел.\uD83D\uDC71\uD83C\uDFFB\u200D♂️\n" +
                                    "Пример - Иванов Иван.", (long) adminId);
                            step++;
                            isActive = false;
                        } else {
                            bot.sendMessage("Вы указали неверный пароль, повторите ввод.\uD83D\uDE3F", (long) adminId);
                            isActive = false;
                        }
                    }
                        else if(step == 1) {
                            if(update.hasMessage()){
                                String [] lfName = update.getMessage().getText().split(" ");
                                lastName = lfName[0];
                                firstName = lfName[1];
                                groupName = "Группа: " + lastName + " " + firstName;
                                password = getNewPassword();
                                alternateId = 0;
                                owner = false;
                                addNewAdmin(this);
                                isActive = false;}
                            }
                    }
                }
            }
        }

    public static void setMasterPassword(String masterPassword) {
        AdminsManager.masterPassword = masterPassword;
    }

    public static String getMasterPassword() {
        return masterPassword;
    }
}
