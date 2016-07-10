package jp.ryo.informationPump.server.debug;

import jp.ryo.informationPump.server.persistent.PersistentManager;

public class SafePersistentTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        PersistentManager p_manager = PersistentManager.getInstance();
        p_manager.writeObjectToFile(PersistentManager.COUNTER_DATA_PATH + "test.persistent",new String("hoge"));
    }

}
