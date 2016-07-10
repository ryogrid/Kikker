package jp.ryo.informationPump.server.crawler;

import java.util.TimerTask;

import jp.ryo.informationPump.server.collab.CollaborativeInformatoinManager;
import jp.ryo.informationPump.server.db.abstractor.*;
import jp.ryo.informationPump.server.util.DBUtil;

public class RemoveOldEntriesTask extends TimerTask {
    public static final int REMOVE_PERIOD_SECONDS =3600;
    
    public void run() {
        try {
//          クロールがドキュメントのリクエスト受信の妨げにならないように優先度を下げる
            Thread th = Thread.currentThread();
            th.setPriority(Thread.MIN_PRIORITY);
            
            CrawledDataManager manager = DBCrawledDataManager.getInstance();
            //古いエントリを削除する
            manager.removeOldEntries(DBUtil.HATEBU_TYPE);
            manager.removeOldEntries(DBUtil.CEEK_NEWS_TYPE);
            manager.removeOldEntries(DBUtil.YOUTUBE_TYPE);
            
            //コラボレイティブフィルタ用の情報も古いものを消す
            CollaborativeInformatoinManager collab_manager = CollaborativeInformatoinManager.getInstance();
            collab_manager.removeOldEntries();
            
//            //キャッシュを一掃する(Userのも一掃するが問題ない)            
//            DBCacheManager.removeAllCahe();
//            
//            //無理やりキャッシュに載せる
//            UserDBAbstractor u_abstractor = UserDBAbstractor.getInstace();
//            u_abstractor.getAllUser();
//            
//            CrawledDataDBAbstractor abstractor = CrawledDataDBAbstractor.getInstance();
//            abstractor.getAllEntries(DBUtil.HATEBU_TYPE);
//            abstractor.getAllEntries(DBUtil.CEEK_NEWS_TYPE);
//            abstractor.getAllEntries(DBUtil.YOUTUBE_TYPE);
            
//            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
