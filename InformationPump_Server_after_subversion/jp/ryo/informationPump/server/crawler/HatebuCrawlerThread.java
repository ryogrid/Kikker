package jp.ryo.informationPump.server.crawler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

import org.htmlparser.util.ParserException;

import jp.ryo.informationPump.server.crawler.analyze.InDependentDocumentAnalyzer;
import jp.ryo.informationPump.server.data.DocumentEntry;
import jp.ryo.informationPump.server.helper.HatebuHelper;
import jp.ryo.informationPump.server.util.DBUtil;

public class HatebuCrawlerThread extends Thread {
public static final int CRAWL_PERIOD_SECONDS = 180;
public static final int A_TIME_CRAWL_PAGE_COUNT = 1;
private static int all_instance_count = 0;

private static Object rock_object = new Object();

private static final int SIMONTANEOUS_RUNNNING_LIMIT = 3;
    
    public HatebuCrawlerThread(String name){
        super(name);
    }
    
    public void run() {
        try {
            tryMoving();
            System.out.println("HatebuCrawlerThread instance count is " + all_instance_count + " now");
            
            System.out.println("HatebuCrawlerThread#run started at " + new Date());
//          クロールがドキュメントのリクエスト受信の妨げにならないように優先度を下げる
            Thread th = Thread.currentThread();
            th.setPriority(Thread.MIN_PRIORITY);
            
            //はてブをクロール
            HashMap crawledResult = crawleHatebu();            

            //クロールしたエントリを解析
            analyzeCrawledHatebuEntries(crawledResult);
            System.out.println("HatebuCrawlerThread#run ended at " + new Date());
        } catch (Throwable e) {
            e.printStackTrace();
        } finally{
            returnMovingLight();
        }
    }
    
    public static void tryMoving(){
        synchronized(rock_object){
            while(all_instance_count > SIMONTANEOUS_RUNNNING_LIMIT){
                try {
                    rock_object.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            all_instance_count++;
        }
    }
    
    public static void returnMovingLight(){
        synchronized (rock_object) {
            all_instance_count--;
            rock_object.notifyAll();
        }
    }
    
    private void analyzeCrawledHatebuEntries(HashMap crawledResult) throws InterruptedException {
        System.out.println("HatebuCrawlerThread#analyzeCrawledHatebuEntriesanalyzeCrawledHatebuEntries started " + new Date());
        
        try {
    //          スレッドで非同期に解析を行う(全てのエントリについて)
            Set result_entry_set = crawledResult.entrySet();
            Iterator result_itr = result_entry_set.iterator();
            while(result_itr.hasNext()){
                 Map.Entry entry = (Map.Entry)result_itr.next();
                 DocumentEntry each_hatebu_entry = (DocumentEntry)entry.getValue();
                 
                 //キーワードとタグをマージして配列へ
                 HashMap keywords = each_hatebu_entry.getKeywords();
                 if(each_hatebu_entry.getTags()!=null){
                     keywords.putAll(each_hatebu_entry.getTags());    
                 }
                 String marged_words[] = new String[keywords.size()];
                 Set set = keywords.keySet();
                 Iterator itr = set.iterator();
                 int index=0;
                 while(itr.hasNext()){
                     String word = (String)itr.next();
                     marged_words[index++] = word;
                 }
                 
                 
                 String url_str = each_hatebu_entry.getAddress();
                 InDependentDocumentAnalyzer.giveDocumentToEngine(url_str,marged_words,DBUtil.HATEBU_TYPE);

                 Thread.sleep(1000);//一気にスレッドを生成するのは負荷が大きすぎるので休み休みやる
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        System.out.println("HatebuCrawlerThread#analyzeCrawledHatebuEntries ended " + new Date());
    }

    private HashMap crawleHatebu() throws ParserException, MalformedURLException, IOException {
        System.out.println("HatebuCrawlerThread#crawleHatebu started " + new Date() );
        CrawledDataManager manager = DBCrawledDataManager.getInstance();
        
        HashMap crawledResult = HatebuHelper.crawl(A_TIME_CRAWL_PAGE_COUNT);
        manager.margeClawledData(crawledResult,DBUtil.HATEBU_TYPE,false);
        
        System.out.println("HatebuCrawlerThread#crawleHatebu ended " + new Date());
        return crawledResult;
    }
}
