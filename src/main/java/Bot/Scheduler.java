package Bot;

import AdminsPanel.*;
import AdminsPanel.AdminsClasses.Admin;
import AdminsPanel.UsersClasses.Member;
import Main.Main;
import Meeting.Meetings;
import Users.UserKeyboard;
import Users.UserThread;
import Users.UsersManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.*;

public class Scheduler {

    private final Map<Long, UserThread> userThreadMap;

    private static final Logger schedulerLogger = LogManager.getLogger(Scheduler.class);
    private static ManagerDatabase managerDatabase;
    private static AdminsManager adminsManager;
    private static UsersManagerA usersManagerA;
    private static KeyboardRow keyboard = new KeyboardRow();
    private static List<KeyboardRow> keyboardRows = new ArrayList<>();
    private static ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

    public Scheduler() {
        managerDatabase = Main.getManagerDatabase();
        adminsManager = managerDatabase.getAdminsManager();
        usersManagerA = managerDatabase.getUsersManagerA();
        userThreadMap = UsersManager.getUsersThreads();
        keyboard.add("Присоединиться к группе");
        keyboardRows.clear();
        keyboardRows.add(keyboard);
        replyKeyboardMarkup.setKeyboard(keyboardRows).setResizeKeyboard(true).setSelective(true);
    }

    public synchronized void scheduleCallBack(Update update) {
        long id  = update.getCallbackQuery().getFrom().getId();
        if (UsersManagerA.getUsersMap().containsKey((int)id)) {
             checkInstanceToUsersThreads(update);
            if(checkCallbackCancel(update)){
                schedulerLogger.info("Запрос на удаление встречи" );
                Meetings.deleteMeeting(update);
            }
            else if(checkCallBackTake(update)){
                schedulerLogger.info("Запрос на обмен встречи");
                Meetings.takeMeeting(update);
            }
            else userThreadMap.get(id).activationSession(update);
        }
        else if (UsersManagerA.getAddNewUserMap().containsKey((int)id)){
            UsersManagerA.inRequestHandler(update);
        }
        else if (AdminsManager.getAdministrators().containsKey((int)id)){
            if(checkCallbackCancel(update)){
                schedulerLogger.info("Запрос на удаление встречи" );
                Meetings.deleteMeeting(update);
            }
            else if(update.getCallbackQuery().getData().startsWith("decision")) Meetings.decisionsMeets(update);
            else AdminsManager.inRequestHandler(update);
        }
        else UsersManager.getBot().sendMessage("Неверный выбор." +
                            "\nОтправьте мне команду /st для начала работы.",
                    id);
    }

    public synchronized void scheduleMessageText(Update update) {
        long id = update.getMessage().getFrom().getId();
        if(UsersManagerA.getUsersMap().containsKey((int)id)){
        checkInstanceToUsersThreads(update);
        userThreadMap.get(id).activationSession(update);
        }
        else if (AdminsManager.getAdministrators().containsKey((int)id)){
            AdminsManager.inRequestHandler(update);
        }
        else if(AdminsManager.getAddNewAdminMap().containsKey((int) id)) AdminsManager.inRequestHandler(update);
        else {
            schedulerLogger.info("Create user.");
            if (UsersManagerA.inRequestHandler(update)) {
                UsersManager.getBot().sendMessage("Вы не состоите ни в одной группе.\n" +
                                "Присоединитесь к группе чтобы продолжить⬇️", id
                ,replyKeyboardMarkup);
            }
        }
    }

    private Long checkInstanceToUsersThreads(Update update){ //Проверяем есть ли экземпляр и добавляем если отсутствует
        Long key;
        if(update.hasCallbackQuery()) key = (long)update.getCallbackQuery().getFrom().getId();
        else key = (long)update.getMessage().getFrom().getId();
        if(!userThreadMap.containsKey(key)) new UserThread(update).start();
        return key;
    }

    private boolean checkCallbackCancel(Update update){
        if(update.getCallbackQuery().getData().contains("-")){
            return update.getCallbackQuery().getData().split("-")[0].equals("cancel");
        }
        else return false;
    }

    private boolean checkCallBackTake (Update update){
        if(update.getCallbackQuery().getData().contains("takeMeet")){
            return update.getCallbackQuery().getData().split("\\?")[0].equals("takeMeet");
        }
        return false;
    }
    public synchronized void adminsAccess(Update update) throws IOException {
        schedulerLogger.info("admin access");
        String data = update.hasCallbackQuery() ? update.getCallbackQuery().getData() :
                update.getMessage().getText();
        long id = update.hasCallbackQuery() ? update.getCallbackQuery().getFrom().getId() :
                update.getMessage().getFrom().getId();
        if (id == Bot.getMainAdminId()) {
            if(data.equals("/debugdelete")){
                Map<String, String> map = new HashMap<>();
                for (Map.Entry pair: AdminsManager.getAdministrators().entrySet()
                     ) {
                     Admin admin = (Admin) pair.getValue();
                    map.put(admin.getGroup().getGroupName(), "debug?deladmin:" + admin.getUserId());
                }
                UsersManager.getBot().sendMessage("Действующие администраторы, нажав на группу вы ее удалите.",
                        Bot.getMainAdminId(), UserKeyboard.getInlineKeyboard(map));
            }
            else if(data.startsWith("debug?deladmin")){
                int adminId = Integer.parseInt(data.split(":")[1]);
                List<Integer> ids = new ArrayList<>();
                Admin admin = AdminsManager.getAdministrators().get(adminId);
                for (Map.Entry pair: admin.getGroup().getUsersThisAdmin().entrySet()
                     ) {
                    Member member = (Member) pair.getValue();
                    UsersManagerA.getUsersMap().remove(member.getUserId());
                    ids.add(member.getUserId());
                }
                AdminsManager.getAdministrators().remove(adminId);
                ManagerDatabase.executeSqlStatement("DELETE FROM admins " +
                        "WHERE admin_id = " + adminId, "DELETE");
                //DELETE FROM products WHERE price = 10; команда на удаление
                for (Integer value: ids
                     ) {
                    ManagerDatabase.executeSqlStatement("DELETE FROM users " +
                            "WHERE user_id = " + value, "DELETE");
                }
                UsersManager.getBot().sendMessage(admin.getGroup().getGroupName() + " удалена.",
                        Bot.getMainAdminId());
            }
            else if(data.equals("/debugnewpass")){
                AdminsManager.setMasterPassword(AdminsManager.getNewPassword());
                ManagerDatabase.executeSqlStatement("UPDATE password " +
                        "SET pass =" + AdminsManager.getMasterPassword() +
                        " WHERE id = 1", "UPDATE");
                UsersManager.getBot().sendMessage("new pass",
                        Bot.getMainAdminId());
                UsersManager.getBot().sendMessage(AdminsManager.getMasterPassword(),
                        Bot.getMainAdminId());
                //UPDATE products SET price = 10 WHERE price = 5; Команда на обновление
            }
            else if(data.equals("/debuggetpass")){
                UsersManager.getBot().sendMessage(AdminsManager.getMasterPassword(),
                        Bot.getMainAdminId());
            }
            else if(data.equals("/debughelp")){
                String help = "/debugdelete\n" + "/debugnewpass\n" + "/debuggetpass\n" + "/debugdeluser\n" +
                        "/debugres\n" + "/debugstatistic\n";
                UsersManager.getBot().sendMessage(help,
                        id);
            }
            else if(data.equals("/debugdeluser")){
                Map<String, String> map = new HashMap<>();
                for (Map.Entry pair: AdminsManager.getAdministrators().entrySet()
                ) {
                    Admin admin = (Admin) pair.getValue();
                    map.put(admin.getGroup().getGroupName(), "debug?usersgroup:" + admin.getUserId());
                }
                UsersManager.getBot().sendMessage("Просмотр списка участников по группам.",
                        id, UserKeyboard.getInlineKeyboard(map));
            }
            else if(data.startsWith("debug?usersgroup")){
                Map<String, String> map = new HashMap<>();
                int adminId = Integer.parseInt(data.split(":")[1]);
                Admin admin = AdminsManager.getAdministrators().get(adminId);
                for (Map.Entry pair: admin.getGroup().getUsersThisAdmin().entrySet()){
                    Member member = (Member) pair.getValue();
                    map.put(member.getLastName() + " " + member.getFirstName(), "debug?deluser:" + member.getUserId());
                }
                UsersManager.getBot().sendMessage("Участники группы - "
                                + admin.getGroup().getGroupName() + " нажав на ФИ вы удалите участника.",
                        id, UserKeyboard.getInlineKeyboard(map));
             }
            else if(data.startsWith("debug?deluser")){
                int userId = Integer.parseInt(data.split(":")[1]);
                Member member = UsersManagerA.getUsersMap().get(userId);
                member.deleteThisDeb();
            }
            else if(data.equals("/debugstatistic")){
                schedulerLogger.info( AdminsManager.statistics.toString());
                String countMeets = "Количество размещенных встреч - " +
                        AdminsManager.statistics.get(0) + "\n";
                String countRequest = "Количество сессий - " +
                        AdminsManager.statistics.get(1).toString() + "\n";
                String res = countMeets + countRequest;
                schedulerLogger.info( AdminsManager.statistics.toString());
                UsersManager.getBot().sendMessage(res,
                        id);
            }
            /*else if(data.startsWith("/debugres")){
                String [] args = Main.getArg();
                StringBuilder cmd = new StringBuilder();
                cmd.append(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java ");
                for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
                    cmd.append(jvmArg + " ");
                }
                cmd.append("-cp ").append(ManagementFactory.getRuntimeMXBean().getClassPath()).append(" ");
                cmd.append(Main.class.getName()).append(" ");
                for (String arg : args) {
                    cmd.append(arg).append(" ");
                }
                Runtime.getRuntime().exec(cmd.toString());
                System.exit(0);
            }*/
        }
    }
}
