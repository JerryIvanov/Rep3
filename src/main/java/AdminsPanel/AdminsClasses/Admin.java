package AdminsPanel.AdminsClasses;

import AdminsPanel.AdminsManager;
import AdminsPanel.ManagerDatabase;
import AdminsPanel.NewUserProcess;
import AdminsPanel.UsersClasses.Member;
import AdminsPanel.UsersManagerA;
import Steps.StepZero.Step_0;
import Users.UserKeyboard;
import Users.UsersManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Admin extends Thread implements NewUserProcess {
private int adminId;
private int alternateId;
private int idAlternateAdmin;
private String alternatePass;
private String groupName;
private String firstName;
private String lastName;
private boolean owner;
private Update update;
private final int timeSession = (1000 * 60) * 5;
private int step = 1;
private Group group;

    private static final Logger adminLogger = LogManager.getLogger(Admin.class);

    public Admin(int adminId, int alternateId, String groupName,
                 String firstName, String lastName, String password, boolean owner, int idAlternateAdmin) {
        Map<Integer, Member> usersThisAdmin = new HashMap<>();
        this.adminId = adminId;
        this.alternateId = alternateId;
        this.groupName = groupName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.owner = owner;
        this.idAlternateAdmin = idAlternateAdmin;
        for (Member member : UsersManagerA.getUserList()
             ) {
            if(member.getGroupId() == this.adminId) usersThisAdmin.put(member.getUserId(), member);
        }
        this.group = new Group(usersThisAdmin, adminId, groupName, password);
        GroupManager.addGroup(group);
    }

    public void addedUserNotification(Member member){
        if (idAlternateAdmin == 0) {
            UsersManager.getBot().sendMessage("К вашей группе присоединился пользователь: " +
                    member.getLastName() + " " + member.getFirstName() + "❗️", (long)adminId);
            group.getUsersThisAdmin().put(member.getUserId(), member);
        }
        else {
            UsersManager.getBot().sendMessage("К вашей замещаемой группе присоединился пользователь: " +
            member.getLastName() + " " + member.getFirstName() + "❗️", (long)idAlternateAdmin);
            group.getUsersThisAdmin().put(member.getUserId(), member);
        }
    }
    public void notifyNewMeetings(SendPhoto sendPhoto){
        if (idAlternateAdmin == 0) UsersManager.getBot().sendMessage(sendPhoto);
        else {
            sendPhoto.setChatId((long)idAlternateAdmin);
            UsersManager.getBot().sendMessage(sendPhoto);
        }
    }

    public synchronized void activation (Update update){
        this.update = update;
        if(!SessionControl.adminThreads.containsKey(adminId)) UsersManager.getBot()
                .sendMessage("Через 5 минут неактивности ваша сессия будет закрыта.", (long)adminId);
        new SessionControl(this).start();
        new Thread(new AdminThread()).start();
        //adminLogger.info("TEST");
    }

    private class AdminThread implements Runnable{

        @Override
        public void run() {
            if(step == 1) startSession();
            else if(step == 2) step2(null);
            else if(step == 3) step3();
            else if(step == 31) step31();
        }
    }

    private synchronized void startSession(){
        Step_0.startAdminStep(this);
    }
    private synchronized void step2(String messageIn){
        String message;
        if (messageIn != null) message = messageIn;
        else message = update.hasCallbackQuery() ? update.getCallbackQuery().getData()
                : update.getMessage().getText();
        if(message.equalsIgnoreCase("Моя группа")){
            UsersManager.getBot().sendMessage("\uD83D\uDC65Это ваша группа.\uD83D\uDC65\n" +
                    "Выбрав нужный пункт вы сможете:\n" +
                    "1. Удалить участника группы.\n" +
                    "2. Назначить заместителя (имеет права на группу равные вашим).\n" +
                    "3. Сменить пароль для входа в группу.\n" +
                    "4. Разослать своей группе от имени бота любой текст.", (long)adminId
            , UserKeyboard.getAdminInlineKeyboard());
            UsersManager.getBot().sendMessage("Или вернуться на шаг назад\uD83D\uDC46"
            ,(long) adminId, new ReplyKeyboardRemove());
            step = 3;
        }
    }
    private synchronized void step3(){
        String data = update.getCallbackQuery().getData();
        if(data.equalsIgnoreCase("back")) {
            step = 1;
            startSession();
        }
        else if (data.equalsIgnoreCase("members")){
            Map<String, String> members = new LinkedHashMap<>();
            for (Map.Entry user:group.getUsersThisAdmin().entrySet()
                 ) {
                Member member1 = (Member)user.getValue();
                members.put(member1.getLastName() + " " + member1.getFirstName(), "delete?user=" +
                        member1.getUserId());
            }
            members.put("Назад", "back");
            if(members.size() > 1) {
                UsersManager.getBot().sendMessage("Это все пользователи состоящие в вашей группе." +
                            "\nНажав на кнопку с ФИ участника - вы исключите его из своей группы.", (long)adminId
            , UserKeyboard.getInlineKeyboard(members));
                step = 31;
                if(alternateId != 0){
                    members.clear();
                    for (Group group: GroupManager.getGroup()
                         ) {
                        if(alternateId == group.getAdminId()){
                            for (Map.Entry user: group.getUsersThisAdmin().entrySet()
                                 ) {
                                Member member1 = (Member) user.getValue();
                                members.put(member1.getLastName() + " " + member1.getFirstName(), "delete?user=" +
                                        member1.getUserId());
                            }
                        }
                    }
                    members.put("Назад", "back");
                    if (members.size() > 1)
                    UsersManager.getBot().sendMessage("Это все пользователи состоящие в замещаемой группе." +
                                    "\nНажав на кнопку с ФИ участника - вы исключите его из своей группы.", (long)adminId
                            , UserKeyboard.getInlineKeyboard(members));
                }
            }
            else UsersManager.getBot().sendMessage("В вашу группу не было добавлено ни одного участника.",
                    (long)adminId);
        }
        else if(data.equalsIgnoreCase("alternate")){
            alternatePass = alternatePass == null ? AdminsManager.getNewPassword() : alternatePass;
            UsersManager.getBot().sendMessage("Для того что бы сменить руководителя этой группы, назначаемому РГ" +
                    " нужно отправить боту текст alternate пароль⬇️", (long) adminId);
            UsersManager.getBot().sendMessage("alternate " + alternatePass, (long)adminId);
            Map<String, String> map = new HashMap<>();
            map.put("Назад", "back");
            String descr = "\n";
            if (alternateId != 0) {
                descr += "\nВы являетесь заместителем группы. Отказаться от управления замещаемой группой можно в любой" +
                        " момент нажав \"Отказаться\"";
                map.put("Отказаться", "deleteAlt");
            }
            UsersManager.getBot().sendMessage("Пароль будет сменен после использования автоматически.\n" +
                    "После назначения нового руководителя вы " +
                            "перестанете получать любые уведомления от группы.\n" + descr, (long)adminId
            , UserKeyboard.getInlineKeyboard(map));
            step = 31;
        }
        else if (data.equals("password")){
            Map<String, String> map = new LinkedHashMap<>();
            map.put("Сменить пароль", "changePass");
            map.put("Назад", "back");
            UsersManager.getBot().sendMessage("Здесь вы можете сменить пароль для присоединения к вашей группе\n"
                    , (long)adminId
                    , UserKeyboard.getInlineKeyboard(map));
            UsersManager.getBot().sendMessage("Ваш текущий пароль⬇️"
                    , (long)adminId);
            UsersManager.getBot().sendMessage(group.getPassword()
                    , (long)adminId);
            step = 31;
        }
        else if (data.equals("mailing")){
            Map<String, String> map = new LinkedHashMap<>();
            map.put("Назад", "back");
            UsersManager.getBot().sendMessage("Отправьте любой текст в ответном сообщении" +
                            " и оно будет доставлено от имени бота всем участникам группы, в том числе замещаемой."
                    , (long)adminId
                    , UserKeyboard.getInlineKeyboard(map));
            step = 31;
        }
    }
    private synchronized void step31 (){
        adminLogger.info("Step " + step);
        String data = update.hasCallbackQuery() ? update.getCallbackQuery().getData() : "sending";
        adminLogger.info(data);
        if(data.equalsIgnoreCase("back")){
            step = 2;
            step2("Моя группа");
        }
        else if(data.split("\\?")[0].equalsIgnoreCase("delete")){
            group.getUsersThisAdmin().get(Integer.parseInt(data.split("=")[1])).deleteThis(this);
        }
        else if(data.equals("deleteAlt")){
            Admin admin = AdminsManager.getAdministrators().get(alternateId);
            admin.setIdAlternateAdmin(0);
            ManagerDatabase.executeSqlStatement("UPDATE admins " +
                    "SET alternate_id =" + 0 + "WHERE admin_id =" + adminId, "UPDATE");
            ManagerDatabase.executeSqlStatement("UPDATE admins " +
                    "SET id_alternate_admin =" + 0 + "WHERE admin_id =" + alternateId, "UPDATE");
            UsersManager.getBot().sendMessage("Теперь вы управляете группой!", (long)alternateId);
            UsersManager.getBot().sendMessage("Вы больше не являетесь заместителем!", (long) adminId);
            this.alternateId = 0;
            step = 1;
            startSession();
        }
        else if(data.equals("changePass")){
            group.setPassword(AdminsManager.getNewPassword());
            step = 1;
            startSession();
            UsersManager.getBot().sendMessage("Пароль успешно изменен.\n" +
                    "Новый пароль⬇️", (long) adminId);
            UsersManager.getBot().sendMessage(group.getPassword(), (long) adminId);
        }
        else if(data.equals("sending")) {
            String message = "Ваш руководитель говорит:\n" + update.getMessage().getText();
            for (Map.Entry pair: group.getUsersThisAdmin().entrySet()
                 ) {
                UsersManager.getBot().sendMessage(message,  Long.valueOf(Integer.toString((Integer) pair.getKey())));
            }
            if(alternateId != 0){
                for (Group group: GroupManager.getGroup()
                     ) {
                    if(group.getAdminId() == alternateId){
                        for (Map.Entry pair: group.getUsersThisAdmin().entrySet()
                             ) {
                            UsersManager.getBot().sendMessage(message, Long.valueOf(Integer.toString((Integer) pair.getKey())));
                        }
                    }
                }
            }
            step = 1;
            startSession();
            UsersManager.getBot().sendMessage("Сообщение доставлено.",(long) adminId);

        }
    }

    public int getUserId() {
        return adminId;
    }

    @Override
    public void closeProcess() {
    }

    public String getGroupName() {
        return groupName;
    }

    public String getPassword() {
        return group.getPassword();
    }

    public void setStep(int step) {
        this.step = step;
    }

    @Override
    public int getTimeSleeping() {
        return timeSession;
    }

    public Group getGroup() {
        return group;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getAlternatePass() {
        return alternatePass;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isOwner() {
        return owner;
    }

    public int getAlternateId() {
        return alternateId;
    }

    public void setAlternateId(int alternateId) {
        this.alternateId = alternateId;
    }

    public int getIdAlternateAdmin() {
        return idAlternateAdmin;
    }

    public void setIdAlternateAdmin(int idAlternateAdmin) {
        this.idAlternateAdmin = idAlternateAdmin;
    }
}
