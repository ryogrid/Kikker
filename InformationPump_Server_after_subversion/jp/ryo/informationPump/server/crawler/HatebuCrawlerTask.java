package jp.ryo.informationPump.server.crawler;

import java.util.*;

import jp.ryo.informationPump.server.*;
import jp.ryo.informationPump.server.collab.CollaborativeInformatoinManager;
import jp.ryo.informationPump.server.crawler.analyze.DependentDocumentAnalyzer;
import jp.ryo.informationPump.server.crawler.analyze.InDependentDocumentAnalyzer;
import jp.ryo.informationPump.server.data.DocumentEntry;
import jp.ryo.informationPump.server.data.StoreBoxForDocumentEntry;
import jp.ryo.informationPump.server.db.abstractor.DBCacheManager;
import jp.ryo.informationPump.server.helper.HatebuHelper;
import jp.ryo.informationPump.server.log.ServerLogger;
import jp.ryo.informationPump.server.persistent.PersistentManager;
import jp.ryo.informationPump.server.util.DBUtil;


public class HatebuCrawlerTask extends TimerTask {
    public void run() {
        Thread hatebu_th = new HatebuCrawlerThread("HatebuCrawlerThread");
        hatebu_th.start();
        System.out.println("HatebuCrawlerTask#run finished " + new Date());
    }
}
