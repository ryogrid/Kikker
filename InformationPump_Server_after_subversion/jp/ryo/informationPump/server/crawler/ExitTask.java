package jp.ryo.informationPump.server.crawler;

import java.util.TimerTask;

public class ExitTask extends TimerTask {
    public static final int EXIT_PERIOD_MINUTES = 360; 
    
    public void run() {
        System.exit(0);
    }

}
