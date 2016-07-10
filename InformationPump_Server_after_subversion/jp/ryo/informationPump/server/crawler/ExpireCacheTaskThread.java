package jp.ryo.informationPump.server.crawler;

import jp.ryo.informationPump.server.collab.CollaborativeInformatoinManager;
import jp.ryo.informationPump.server.db.abstractor.*;
import jp.ryo.informationPump.server.persistent.PersistentManager;
import jp.ryo.informationPump.server.util.DBUtil;

public class ExpireCacheTaskThread extends Thread {
    
    public ExpireCacheTaskThread(String name){
        super(name);
    }
    public void run() {
        try {
//          クロールがドキュメントのリクエスト受信の妨げにならないように優先度を下げる
            Thread th = Thread.currentThread();
            th.setPriority(Thread.MIN_PRIORITY);
            
            CrawledDataManager manager = DBCrawledDataManager.getInstance();
            
            //キャッシュを一掃する(Userのも一掃するが問題ない)            
            DBCacheManager.removeAllCahe();
            
            System.gc();
            
////            //無理やりキャッシュに載せる
////            UserDBAbstractor u_abstractor = UserDBAbstractor.getInstace();
////            u_abstractor.getAllUser();
//            
//            CrawledDataDBAbstractor abstractor = CrawledDataDBAbstractor.getInstance();
//            
//            InvertedMap.setIsCanUse(false);
//            InvertedMap.makeInvertedMap(abstractor.getAllEntries(DBUtil.HATEBU_TYPE),DBUtil.HATEBU_TYPE); 
//            InvertedMap.makeInvertedMap(abstractor.getAllEntries(DBUtil.CEEK_NEWS_TYPE),DBUtil.CEEK_NEWS_TYPE);
//            //転置マップを使用可能に
//            InvertedMap.setIsCanUse(true);
//            
////            abstractor.getAllEntries(DBUtil.YOUTUBE_TYPE);
////            
//            PersistentManager p_manager = PersistentManager.getInstance();
//            
//            //コラボレイティブフィルタのキャッシュもexpireさせる
//            CollaborativeInformatoinManager collab_manaber = CollaborativeInformatoinManager.getInstance();
//            collab_manaber.expireCaches();
//            
//            //各オブジェクトの保存を行う
//            p_manager.escapeObjectsToFile();
                                    
            try {
                System.out.println("Updating Bookmark Counts start!!");
//              先に既存のエントリのブックマーク数を更新する            
                manager.updateBookmarkCounts(DBUtil.HATEBU_TYPE);
                System.out.println("updating Bookmark Counts finished!!");
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
