/*
 * �쐬��: 2006/02/07
 *
 * ���̐������ꂽ�R�����g�̑}�������e���v���[�g��ύX���邽��
 * �E�B���h�E > �ݒ� > Java > �R�[�h���� > �R�[�h�ƃR�����g
 */
package jp.ryo.informationPump.server;

import java.io.*;
import java.util.Timer;

import jp.crossfire.framework.database.HyperDbException;
import jp.ryo.informationPump.server.crawler.*;
import jp.ryo.informationPump.server.db.DBManager;
import jp.ryo.informationPump.server.exchange_server.ExchangeServer;
import jp.ryo.informationPump.server.persistent.PersistentManager;
import jp.ryo.informationPump.server.rpc.KikkerHahdllerMapping;
import jp.ryo.informationPump.server.web_server.WebServer;

public class CrawlerServer {
    private static final String SYS_OUT_REDIRECT_TO_FILE = "./log/sysout_crawler.log";
    private static final int KIKKER_XML_API_PORT = 7777;
    
    public static void main(String[] args) {
        init();
        
//        WebServer web_server = new WebServer();
//        web_server.start();
    }
    
    
    private static void init(){
        System.setProperty("sen.home","./sen-1.2.1");
//        PersistentManager pmanager = PersistentManager.getInstance();
//        pmanager.initEachObject();//�����O��̃f�[�^���ۑ�����Ă����ꍇ�͓ǂݍ���
        
//      �f�[�^�x�[�X�̃R�l�N�V�����v�[���̏���
        try {
            DBManager.initMySQL();
        } catch (HyperDbException e) {
            e.printStackTrace();
        }
        
        //�͂ău�N���[���[�̃^�C�}�[���N��
        System.out.println("Informatin Pump Hatebu Crawler started!!");
        Timer hatebu_crawler_timer = new Timer();
//        hatebu_crawler_timer.schedule(new HatebuCrawlerTask(),HatebuCrawlerTask.CRAWL_PERIOD_SECONDS*1000l,HatebuCrawlerTask.CRAWL_PERIOD_SECONDS*1000l);
        hatebu_crawler_timer.schedule(new HatebuCrawlerTask(),0l,HatebuCrawlerThread.CRAWL_PERIOD_SECONDS*1000l);
        
        //CEEK.JP NEWS�N���[���̃^�C�}�[���N��
        System.out.println("Informatin Pump CEEK.JP NEWS Crawler started!!");
        Timer ceek_jp_crawler_timer = new Timer();
//        ceek_jp_crawler_timer.scheduleAtFixedRate(new CeekJpNewsCrawlerTask(),CeekJpNewsCrawlerTask.CRAWL_PERIOD_SECONDS*1000l,CeekJpNewsCrawlerTask.CRAWL_PERIOD_SECONDS*1000l);
        ceek_jp_crawler_timer.scheduleAtFixedRate(new CeekJpNewsCrawlerTask(),0l,CeekJpNewsCrawlerTask.CRAWL_PERIOD_SECONDS*1000l);
        
        //Youtube�N���[���̃^�C�}�[���N��
        System.out.println("Informatin Pump Youtube Crawler started!!");
        Timer youtube_crawler_timer = new Timer();
//        youtube_crawler_timer.scheduleAtFixedRate(new YoutubeCrawlerTask(),YoutubeCrawlerTask.CRAWL_PERIOD_SECONDS*1000l,YoutubeCrawlerTask.CRAWL_PERIOD_SECONDS*1000l);
        youtube_crawler_timer.scheduleAtFixedRate(new YoutubeCrawlerTask(),0l,YoutubeCrawlerTask.CRAWL_PERIOD_SECONDS*1000l);
        
//      �G���g���폜�p�̃^�C�}�[���N��
        System.out.println("Remove Old Entries Tthread started!!");
        Timer remove_entry_timer = new Timer();
        remove_entry_timer.schedule(new RemoveOldEntriesTask(),0l,RemoveOldEntriesTask.REMOVE_PERIOD_SECONDS*1000l);
        
        try {
            BufferedOutputStream bstream = new BufferedOutputStream(new FileOutputStream(new File(SYS_OUT_REDIRECT_TO_FILE),true));
            PrintStream pstream = new PrintStream(bstream);
            System.setOut(pstream);
            System.setErr(pstream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
