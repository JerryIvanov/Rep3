package AdminsPanel.UsersClasses;

import AdminsPanel.NewUserProcess;

public class Timer extends Thread{

    private NewUserProcess thread;

    public Timer (NewUserProcess thread){
        this.thread = thread;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(thread.getTimeSleeping());
            thread.closeProcess();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
