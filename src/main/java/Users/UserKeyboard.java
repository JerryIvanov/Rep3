package Users;

import LineAndStation.LineManager;
import LineAndStation.MetroLine;
import LineAndStation.MetroStation;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.*;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserKeyboard {
    private static Map<Integer, List<KeyboardRow>> ReplyKeyboards = new HashMap<>();
    private static Map<Integer, List<List<InlineKeyboardButton>>> inlineKeyboardLines = new HashMap<>();
    private static Map<String, List<List<InlineKeyboardButton>>> stationMap = new HashMap<>();

    static final Logger logUserKeyboard = LogManager.getLogger(UserKeyboard.class);
    MetroLine metroLine;

    //Lines
    private static boolean linesInit = false;
    private static List<InlineKeyboardButton> keyboardButtons1 = new ArrayList<>();
    private static List<InlineKeyboardButton> keyboardButtons2 = new ArrayList<>();
    private static List<InlineKeyboardButton> keyboardButtons3 = new ArrayList<>();
    private static List<InlineKeyboardButton> keyboardButtons4 = new ArrayList<>();
    private static List<InlineKeyboardButton> keyboardButtons5 = new ArrayList<>();
    private static List<InlineKeyboardButton> keyboardButtons6 = new ArrayList<>();
    private static List<InlineKeyboardButton> keyboardButtons7 = new ArrayList<>();
    private static List<InlineKeyboardButton> testButton = new ArrayList<>();

    private static List<List<InlineKeyboardButton>> linesButtonList = new ArrayList<>();
    private static List<List<InlineKeyboardButton>> stationsButtonList = new ArrayList<>();

    private static InlineKeyboardMarkup adminInlineKeyboard;


    public UserKeyboard() {
        //Step One
        KeyboardRow backAndHelp = new KeyboardRow();
        backAndHelp.add("Назад");
        backAndHelp.add("Помощь");

        KeyboardRow near = new KeyboardRow();

        KeyboardRow myOffers = new KeyboardRow();
        myOffers.add("Мои предложения");

        List<KeyboardRow> replyMarkupStepOneTake = new ArrayList<KeyboardRow>();
        replyMarkupStepOneTake.add(backAndHelp);
        List<KeyboardRow> replyMarkupStepOneGive = new ArrayList<KeyboardRow>();
        replyMarkupStepOneGive.add(backAndHelp);
        replyMarkupStepOneGive.add(myOffers);


        ReplyKeyboards.put(1, replyMarkupStepOneGive);
        ReplyKeyboards.put(0, replyMarkupStepOneTake);

    }

    public static synchronized Map<Integer, List<KeyboardRow>> getReplyKeyboards() {
        return ReplyKeyboards;
    }

    public synchronized static List<List<InlineKeyboardButton>> getInlineKeyboardLines() throws IOException {


        if (!linesInit) {
            logUserKeyboard.info("Инициализация начата...");
            linesButtonList.add(keyboardButtons1);
            linesButtonList.add(keyboardButtons2);
            linesButtonList.add(keyboardButtons3);
            linesButtonList.add(keyboardButtons4);
            linesButtonList.add(keyboardButtons5);
            linesButtonList.add(keyboardButtons6);
            linesButtonList.add(keyboardButtons7);
            //logUserKeyboard.info("Кнопки добавлены.");

            int count = 0;
            String[] lines = new String[14];
            for (Map.Entry pair : LineManager.getMeetingsList().entrySet()
            ) {
                lines[count] = String.valueOf(pair.getKey());
                count++;
            }

            //Arrays.sort(lines);

            //logUserKeyboard.info("Массив линий метро создан.");
            //logUserKeyboard.info("Размер массива с кнопками - " + linesButtonList.size());
            //logUserKeyboard.info("Размер массива с линиями - " + lines.length);
            count = 0;
            for (List<InlineKeyboardButton> button : linesButtonList
            ) {
                button.add(new InlineKeyboardButton().setText(lines[count]).setCallbackData(lines[count]));
                count++;
                button.add(new InlineKeyboardButton().setText(lines[count]).setCallbackData(lines[count]));
                count++;
                //logUserKeyboard.info("count = " + count);
            }
            //linesButtonList.add(testButton);
            //testButton.add(new InlineKeyboardButton().setText("TEST").setCallbackData("/TEST"));
            inlineKeyboardLines.put(0, linesButtonList);
            logUserKeyboard.info("Инициализация окончена");
        }
        linesInit = true;
        return inlineKeyboardLines.get(0);
    }

    public synchronized static InlineKeyboardMarkup getKeyboardButtonsStations(MetroLine metroLine) {
        stationMap.remove(metroLine.getNameLine());
            logUserKeyboard.info("Инициализация станций начата...");
            List<List<InlineKeyboardButton>> list = new ArrayList<>();
            int count = 0;
            int index = 0;
            for (MetroStation s : metroLine.getMetroStations()
            ) {
                if (count % 2 == 0) {
                    list.add(new ArrayList<>());
                    list.get(index).add(new InlineKeyboardButton().setText(s.getMeetings().size() +
                            " - " + s.getNameMetroStation()).setCallbackData(s.getNameMetroStation()));
                    count++;
                } else {
                    list.get(index).add(new InlineKeyboardButton().setText(s.getMeetings().size() +
                            " - " + s.getNameMetroStation()).setCallbackData(s.getNameMetroStation()));
                    count++;
                    index++;
                }
            }
            stationMap.put(metroLine.getNameLine(), list);
            logUserKeyboard.info("Инициализация станций завершена.");
            return new InlineKeyboardMarkup().setKeyboard(stationMap.get(metroLine.getNameLine()));
    }

    public synchronized static InlineKeyboardMarkup getInlineKeyboard (Map<String, String> map){
        List<List<InlineKeyboardButton>> list = new ArrayList<>();
        int count = 0;
        int index = 0;
        for (Map.Entry pair: map.entrySet()
             ) {
            if (count % 2 == 0) {
                list.add(new ArrayList<>());
                list.get(index).add(new InlineKeyboardButton().setText((String) pair.getKey())
                        .setCallbackData((String) pair.getValue()));
                count++;
            } else {
                list.get(index).add(new InlineKeyboardButton().setText((String) pair.getKey())
                        .setCallbackData((String) pair.getValue()));
                count++;
                index++;
            }
        }
        return new InlineKeyboardMarkup().setKeyboard(list);
    }

    public synchronized static InlineKeyboardMarkup getAdminInlineKeyboard(){
        if (adminInlineKeyboard == null) {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("Участники", "members");
            map.put("Заместитель", "alternate");
            map.put("Пароль", "password");
            map.put("Рассылка", "mailing");
            map.put("Назад", "back");
            adminInlineKeyboard = getInlineKeyboard(map);
        }

        return adminInlineKeyboard;
    }
    public synchronized static List<KeyboardRow> getReplyKeyboardCustom(List<String> list){
        List<KeyboardRow> keyboardRows = new ArrayList<KeyboardRow>();
        for (int i = 0; i < list.size() + 1; i++){
            keyboardRows.add(new KeyboardRow());
        }
        int count1 = 0;
        int count = 0;
        for (String st: list
             ) {
            if(count1 != 0 && count1 % 2 == 0) count++;
            keyboardRows.get(count).add(st);
            count1++;
        }
        logUserKeyboard.info("sending replyKeyboard.");
        return keyboardRows;
    }
}