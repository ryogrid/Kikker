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
    public static final int CRAWL_PERIOD_SECONDS =5*60;  //5���o�Ń`�F�b�N

    public void run() {
        try {
//          �N���[�����h�L�������g�̃��N�G�X�g��M�̖W���ɂȂ�Ȃ��悤�ɗD��x��������
            Thread th = Thread.currentThread();
            th.setPriority(Thread.MIN_PRIORITY);

            //CEEK.JP NEWS ���N���[��
            HashMap crawledResult = crawleCeekNews();
            //�N���[�������G���g�������
            analyzeCrawledCeekNewsEntries(crawledResult);

//            //�L���b�V������|����
//            DBCacheManager.removeCache(DBCacheManager.ALL_CEEK_NEWS_ENTRY_CACHE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void analyzeCrawledCeekNewsEntries(HashMap crawledResult) throws InterruptedException {
        System.out.println("CEEK.JP NEWS Analyze Start" + new Date());

//          �X���b�h�Ŕ񓯊��ɉ�͂��s��(�S�ẴG���g���ɂ���)
        Set result_entry_set = crawledResult.entrySet();
        Iterator result_itr = result_entry_set.iterator();
        while(result_itr.hasNext()){
             Map.Entry entry = (Map.Entry)result_itr.next();
             DocumentEntry each_document_entry = (DocumentEntry)entry.getValue();

             String marged_words[] = new String[0];

             String url_str = each_document_entry.getAddress();
             InDependentDocumentAnalyzer.giveDocumentToEngine(url_str,marged_words,DBUtil.CEEK_NEWS_TYPE);

             Thread.sleep(20000);//��C�ɃX���b�h�𐶐�����͕̂��ׂ��傫������̂ŋx�݋x�݂��
//             Thread.sleep(2000);//��C�ɃX���b�h�𐶐�����͕̂��ׂ��傫������̂ŋx�݋x�݂��
        }
        System.out.println("CEEK.JP NEWS Analyze Finished" + new Date());
    }

    private HashMap crawleCeekNews() {
        System.out.println("CEEK.JP NEWS Crawling start!!");
        CrawledDataManager manager = DBCrawledDataManager.getInstance();

        HashMap crawledResult = CeekNewsHelper.crawl();
        manager.margeClawledData(crawledResult,DBUtil.CEEK_NEWS_TYPE,false);

        PersistentManager p_manager = PersistentManager.getInstance();
        //�e�I�u�W�F�N�g�̕ۑ����s��
//        p_manager.escapeObjectsToFile();
        //���O�̏o�͂��s��

        System.out.println("CEEK.JP NEWS Crawling suceeded " + new Date());
        return crawledResult;
    }
}
