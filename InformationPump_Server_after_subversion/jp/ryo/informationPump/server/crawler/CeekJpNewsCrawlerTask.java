package jp.ryo.informationPump.server.crawler;

import java.util.*;

import jp.ryo.informationPump.server.crawler.analyze.DependentDocumentAnalyzer;
import jp.ryo.informationPump.server.crawler.analyze.InDependentDocumentAnalyzer;
import jp.ryo.informationPump.server.data.DocumentEntry;
import jp.ryo.informationPump.server.db.abstractor.DBCacheManager;
import jp.ryo.informationPump.server.helper.CeekNewsHelper;
import jp.ryo.informationPump.server.persistent.PersistentManager;
import jp.ryo.informationPump.server.util.DBUtil;

public class CeekJpNewsCrawlerTask extends TimerTask {
    public static final int CRAWL_PERIOD_SECONDS =5*60;  //5感覚でチェック

    public void run() {
        try {
//          クロールがドキュメントのリクエスト受信の妨げにならないように優先度を下げる
            Thread th = Thread.currentThread();
            th.setPriority(Thread.MIN_PRIORITY);

            //CEEK.JP NEWS をクロール
            HashMap crawledResult = crawleCeekNews();
            //クロールしたエントリを解析
            analyzeCrawledCeekNewsEntries(crawledResult);

//            //キャッシュを一掃する
//            DBCacheManager.removeCache(DBCacheManager.ALL_CEEK_NEWS_ENTRY_CACHE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void analyzeCrawledCeekNewsEntries(HashMap crawledResult) throws InterruptedException {
        System.out.println("CEEK.JP NEWS Analyze Start" + new Date());

//          スレッドで非同期に解析を行う(全てのエントリについて)
        Set result_entry_set = crawledResult.entrySet();
        Iterator result_itr = result_entry_set.iterator();
        while(result_itr.hasNext()){
             Map.Entry entry = (Map.Entry)result_itr.next();
             DocumentEntry each_document_entry = (DocumentEntry)entry.getValue();

             String marged_words[] = new String[0];

             String url_str = each_document_entry.getAddress();
             InDependentDocumentAnalyzer.giveDocumentToEngine(url_str,marged_words,DBUtil.CEEK_NEWS_TYPE);

             Thread.sleep(20000);//一気にスレッドを生成するのは負荷が大きすぎるので休み休みやる
//             Thread.sleep(2000);//一気にスレッドを生成するのは負荷が大きすぎるので休み休みやる
        }
        System.out.println("CEEK.JP NEWS Analyze Finished" + new Date());
    }

    private HashMap crawleCeekNews() {
        System.out.println("CEEK.JP NEWS Crawling start!!");
        CrawledDataManager manager = DBCrawledDataManager.getInstance();

        HashMap crawledResult = CeekNewsHelper.crawl();
        manager.margeClawledData(crawledResult,DBUtil.CEEK_NEWS_TYPE,false);

        PersistentManager p_manager = PersistentManager.getInstance();
        //各オブジェクトの保存を行う
//        p_manager.escapeObjectsToFile();
        //ログの出力も行う

        System.out.println("CEEK.JP NEWS Crawling suceeded " + new Date());
        return crawledResult;
    }
}
