package Steps.StepThree;

import Meeting.Meetings;
import Steps.StepOne.Step_1;
import Steps.StepZero.Step_0;
import Steps.StepsMethods;
import Users.UserKeyboard;
import Users.UserThread;
import Users.UsersManager;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;

public class Step_3_2 {

    private Step_3_2 (){}

    public synchronized static void step_3_2(UserThread thread){
        if(thread.getMessage().equalsIgnoreCase("Назад")){
            Step_1.sendNotifyLines(thread);
        }
        else if(thread.getUpdate().hasCallbackQuery()){
            int answer = Meetings.addNotifyForUser(thread);
            if(answer == 1){
                AnswerCallbackQuery query = new AnswerCallbackQuery()
                        .setCallbackQueryId(thread.getUpdate().getCallbackQuery().getId())
                        .setShowAlert(true)
                        .setText("Станция добавлена. \n" +
                                "Вы будете уведомлены о всех новых предложениях на станции - " + thread.getMessage());

                UsersManager.getBot().sendMessage(query);
                SendSticker sendSticker = new SendSticker().setSticker("CAACAgIAAxkBAAMCXxc2QqEdL8MH8dPVjjrdSOVVywoAAv8GAAJ5XOIJ9D0EnjZOniYaBA")
                        .setChatId(thread.getChatUserId());
                //UsersManager.getBot().sendMessage(sendSticker);
                Step_0.stepZeroNoTime(thread);
            }
            else if(answer == 2){
                AnswerCallbackQuery query = new AnswerCallbackQuery()
                        .setCallbackQueryId(thread.getUpdate().getCallbackQuery().getId())
                        .setShowAlert(true)
                        .setText("Станция " +  thread.getMessage() + " была добавлена ранее.\n" +
                                "Выберите другую станцию, " +
                                "или вернитесь к выбору ветки с помощью кнопки \"Назад\"." );
                UsersManager.getBot().sendMessage(query);
            }
            else {AnswerCallbackQuery query = new AnswerCallbackQuery()
                    .setCallbackQueryId(thread.getUpdate().getCallbackQuery().getId())
                    .setShowAlert(true)
                    .setText("Станция не была найдена, " +
                            "повторите выбор или или вернитесь к выбору ветки с помощью кнопки \"Назад\"." );
                UsersManager.getBot().sendMessage(query);
            }
        }
    }
}
