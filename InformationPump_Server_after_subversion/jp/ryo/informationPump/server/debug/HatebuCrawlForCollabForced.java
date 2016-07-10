package jp.ryo.informationPump.server.debug;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

import org.htmlparser.util.ParserException;

import jp.crossfire.framework.database.HyperDbException;
import jp.ryo.informationPump.server.collab.CollaborativeInformatoinManager;
import jp.ryo.informationPump.server.crawler.CrawledDataManager;
import jp.ryo.informationPump.server.crawler.DBCrawledDataManager;
import jp.ryo.informationPump.server.db.DBManager;
import jp.ryo.informationPump.server.helper.HatebuHelper;
import jp.ryo.informationPump.server.persistent.PersistentManager;
import jp.ryo.informationPump.server.util.DBUtil;

public class HatebuCrawlForCollabForced {

    /**
     * @param args
     */
    public static void main(String[] args) {
//      データベースのコネクションプールの準備
        try {
            DBManager.initMySQL();
        } catch (HyperDbException e) {
            e.printStackTrace();
        }
        
        CrawledDataManager manager = DBCrawledDataManager.getInstance();
        
        CollaborativeInformatoinManager collab_manager = CollaborativeInformatoinManager.getInstance();
        HashMap crawledResult = null;
        try {
            crawledResult = HatebuHelper.crawl(200);
        } catch (ParserException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        manager.margeClawledData(crawledResult,DBUtil.HATEBU_TYPE,false);
        
        PersistentManager p_manager = PersistentManager.getInstance();
        p_manager.writeObjectToFile(PersistentManager.COLLAB_DATA_PATH + PersistentManager.COLLAB_MANAGER_DATA_FILE + "_forced",collab_manager);
    }

}
