package AdminsPanel;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface NewUserProcess {
    public int getTimeSleeping();
    public void activation(Update update);
    public void setStep(int step);
    public int getUserId();
    public void closeProcess();
}
