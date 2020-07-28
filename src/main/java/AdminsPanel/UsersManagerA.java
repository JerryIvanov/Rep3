package AdminsPanel;

import AdminsPanel.AdminsClasses.Admin;
import AdminsPanel.UsersClasses.Timer;
import AdminsPanel.UsersClasses.Member;
import Bot.Bot;
import Main.Main;
import Users.UserKeyboard;
import Users.UsersManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsersManagerA {
    private static Map<Integer, Member> users = new HashMap<>();
    private static ManagerDatabase managerDatabase = Main.getManagerDatabase();
    private static final Logger usersLogger = LogManager.getLogger(UsersManagerA.class);
    private static Map<Integer, AddNewUser> addNewUserMap = new HashMap<>();
    private static Bot bot = UsersManager.getBot();

    public UsersManagerA() {
        for (Member member : managerDatabase.createUsersInstance()
        ) {
            if(AdminsManager.getAdministrators().containsKey(member.getGroupId())) {
                users.put(member.getUserId(), member);
            }
            else {
                ManagerDatabase.executeSqlStatement("DELETE FROM users " +
                        "WHERE user_id = " + member.getUserId(), "DELETE");
            }
        }
        usersLogger.info("Users added to map");
    }

    public static synchronized boolean inRequestHandler(Update update) {
        //usersLogger.info("InRequest");
        int userId = update.hasCallbackQuery() ? update.getCallbackQuery().getFrom().getId()
                : update.getMessage().getFrom().getId();
        if (!addNewUserMap.containsKey(userId)) {
            new AddNewUser(update, userId).start();
            return true;
        } else {
            addNewUserMap.get(userId).activation(update);
            return false;
        }
    }

    /*"CREATE TABLE IF NOT EXISTS users(" +
            "user_id INTEGER NOT NULL UNIQUE," +
            "group_id INTEGER NOT NULL," +
            "first_name VARCHAR (25) NOT NULL," +
            "last_name VARCHAR (25) NOT NULL)", "CREATE");*/
    private synchronized static void addNewUser(AddNewUser addNewUser) {//INSERT INTO products VALUES (1, 'Cheese', 9.99); команда на добавление
        ManagerDatabase.executeSqlStatement("INSERT INTO users VALUES(" +
                "'" + addNewUser.userId + "'," +
                "'" + addNewUser.groupId + "'," +
                "'" + addNewUser.firstName + "'," +
                "'" + addNewUser.lastName + "')", "INSERT");

        Admin admin = addNewUser.administrator;
        Member member = new Member(addNewUser.userId, addNewUser.groupId
                , addNewUser.firstName, addNewUser.lastName);
        users.put(addNewUser.userId, member);
        bot.sendMessage("Вы были успешно добавлены в группу, отправьте в ответном" +
                " сообщении /st для начала работы✍️", (long) addNewUser.userId);
        admin.addedUserNotification(member);
        addNewUser.closeProcess();
    }

    public synchronized static Map<Integer, Member> getUsersMap() {
        return users;
    }

    public synchronized static Map<Integer, AddNewUser> getAddNewUserMap() {
        return addNewUserMap;
    }

    public synchronized static List<Member> getUserList() {
        List<Member> list = new ArrayList<>();
        for (Map.Entry pair : users.entrySet()
        ) {
            list.add((Member) pair.getValue());
        }
        return list;
    }

    public static class AddNewUser extends Thread implements NewUserProcess {
        private Update update;
        private Admin administrator;
        private Integer userId;
        private Integer groupId;
        private String firstName;
        private String lastName;
        private boolean isActive = false;
        private byte step = 0;

        public AddNewUser(Update update, Integer userId) {
            this.update = update;
            this.userId = userId;
            addNewUserMap.put(userId, this);
            new Timer(this);
        }

        public synchronized void activation(Update update) {
            //usersLogger.info("TEST THERE");
            this.isActive = true;
            this.update = update;
        }

        @Override
        public void setStep(int step) {

        }

        @Override
        public int getUserId() {
            return userId;
        }

        public int getTimeSleeping() {
            return (1000 * 60) * 2;
        }

        @Override
        public void closeProcess() {
            addNewUserMap.remove(userId);
            this.interrupt();
        }

        @Override
        public void run() {
            while (!this.isInterrupted()) {
                while (isActive && !this.isInterrupted()) {
                    if (step == 0) {
                        if (update.hasMessage() && update.getMessage().getText()
                                .equalsIgnoreCase("Присоединиться к группе")) {
                            if (AdminsManager.getAdministrators().size() > 0) {
                                Map<String, String> map = new HashMap<>();
                                for (Map.Entry pair : AdminsManager.getAdministrators().entrySet()
                                ) {
                                    Admin admin = (Admin) pair.getValue();
                                    map.put(admin.getGroupName(), "enteringGroup:" + admin.getUserId());
                                }
                                InlineKeyboardMarkup inlineKeyboardMarkup = UserKeyboard.getInlineKeyboard(map);
                                bot.sendMessage(
                                        "\uD83E\uDDCDТеперь вам нужно выбрать своего руководителя\uD83E\uDDCD",
                                        (long) userId, inlineKeyboardMarkup);
                                bot.sendMessage("⬆️", (long) userId, new ReplyKeyboardRemove());
                                isActive = false;
                                step++;
                                usersLogger.info("Registration new user step =  " + step);
                            } else {
                                bot.sendMessage("В данный момент не " +
                                        "добавлено ни одного администратора.", (long) userId, new ReplyKeyboardRemove());
                                closeProcess();
                            }
                        }
                    } else if (step == 1) {

                        //usersLogger.info(update.getCallbackQuery().getMessage());
                        if (update.hasCallbackQuery() &&
                                update.getCallbackQuery().getData().split(":")[0].equals("enteringGroup")) {

                            int idAdmin = Integer.parseInt(update.getCallbackQuery().getData()
                                    .split(":")[1]);
                            if (AdminsManager.getAdministrators().containsKey(idAdmin)) {
                                bot.sendMessage("В ответном сообщении укажите пароль.", (long) userId);
                                administrator = AdminsManager.getAdministrators().get(idAdmin);
                                usersLogger.info(administrator.getPassword());
                                isActive = false;
                                step = 2;
                                usersLogger.info("Registration new user step =  " + step);
                            }
                        } else {
                            bot.sendMessage("Сделали неверный выбор.\uD83D\uDE20", (long) userId);
                            isActive = false;
                        }
                    }
                        else if (step == 2) {
                            usersLogger.info("Step 2 =  " + step);
                            if (update.hasMessage() && update.getMessage().getText().equals(administrator.getPassword())) {
                                bot.sendMessage("В ответном сообщении укажите Фамилию/Имя через пробел" +
                                        "\nПример - Иванов Иван.", (long) userId);
                                groupId = administrator.getUserId();
                                isActive = false;
                                step = 3;
                                usersLogger.info("Registration new user step =  " + step);
                            } else {
                                bot.sendMessage("Вы указали неверный пароль!\n" +
                                        "Повторите ввод: пароль.", (long) userId);
                                isActive = false;
                            }

                        } else if (step == 3) {
                            if (update.hasMessage()) {
                                usersLogger.info("Registration new user final step");
                                String[] flName = update.getMessage().getText().split(" ");
                                firstName = flName[1];
                                lastName = flName[0];
                                addNewUser(this);
                                usersLogger.info("Registration new complete");
                            }
                        }
                    }
                }
            }
        }
    }

