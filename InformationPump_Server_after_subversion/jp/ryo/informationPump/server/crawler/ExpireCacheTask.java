package jp.ryo.informationPump.server.crawler;

import java.util.TimerTask;

import jp.ryo.informationPump.server.collab.CollaborativeInformatoinManager;
import jp.ryo.informationPump.server.db.abstractor.*;
import jp.ryo.informationPump.server.persistent.PersistentManager;
import jp.ryo.informationPump.server.util.DBUtil;

public class ExpireCacheTask extends TimerTask {
    public static final int REMOVE_PERIOD_SECONDS =7200;

    public void run() {
        ExpireCacheTaskThread thread = new ExpireCacheTaskThread("ExpireCacheTaskThread");
        thread.start();
    }
}
