package jp.ryo.informationPump.server.helper;

import java.util.*;

import org.htmlparser.Node;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.tags.Div;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.*;

import jp.ryo.informationPump.server.crawler.CrawledDataManager;
import jp.ryo.informationPump.server.crawler.DBCrawledDataManager;
import jp.ryo.informationPump.server.data.*;
import jp.ryo.informationPump.server.util.DBUtil;
import jp.ryo.informationPump.server.util.Util;
import de.nava.informa.impl.basic.Item;

public class CeekNewsHelper {
    private static final String CEEK_NEWS_NATIONAL_ADDRESS = "http://news.ceek.jp/rss/national.rdf";
    private static final String CEEK_NEWS_POLITICS_ADDRESS = "http://news.ceek.jp/rss/politics.rdf";
    private static final String CEEK_NEWS_WORLD_ADDRESS = "http://news.ceek.jp/rss/world.rdf";
    private static final String CEEK_NEWS_CHINA_COREA_ADDRESS = "http://news.ceek.jp/rss/triple.rdf";
    private static final String CEEK_NEWS_BUSINESS_ADDRESS = "http://news.ceek.jp/rss/business.rdf";
    private static final String CEEK_NEWS_IT_ADDRESS = "http://news.ceek.jp/rss/it.rdf";
    private static final String CEEK_NEWS_SPORTS_ADDRESS = "http://news.ceek.jp/rss/sports.rdf";
    private static final String CEEK_NEWS_ENTERTAINMENT_ADDRESS = "http://news.ceek.jp/rss/entertainment.rdf";
    private static final String CEEK_NEWS_SCIENCE_ADDRESS = "http://news.ceek.jp/rss/science.rdf";
    private static final String CEEK_NEWS_OBITUARIES_ADDRESS = "http://news.ceek.jp/rss/obituaries.rdf";
    private static final String CEEK_NEWS_LOCAL_ADDRESS = "http://news.ceek.jp/rss/local.rdf";
    private static final String CEEK_NEWS_ETC_ADDRESS = "http://news.ceek.jp/rss/etc.rdf";
    
    private static  Date national_lastupdated;
    private static  Date politics_lastupdated;
    private static  Date world_lastupdated;
    private static  Date china_corea_lastupdated;
    private static  Date business_lastupdated;
    private static  Date it_lastupdated;
    private static  Date sports_lastupdated;
    private static  Date entertainment_lastupdated;
    private static  Date science_lastupdated;
    private static  Date obituaries_lastupdated;
    private static  Date local_lastupdated;
    private static  Date etc_lastupdated;
    
    public static final String NATIONAL = "社会";
    public static final String POLITICS = "政治";
    public static final String WORLD = "国際";
    public static final String CHINA = "中国・朝鮮";
    public static final String BUSINESS = "ビジネス";
    public static final String IT = "電脳";
    public static final String SPORTS = "スポーツ";
    public static final String ENTERTAINMENT = "エンターテイメント";
    public static final String SCIENCE = "サイエンス";
    public static final String OBITUARIES = "訃報・人事";
    public static final String LOCAL = "地方・地域";
    public static final String ETC = "その他";
    
    private static CeekJPNewsEntry[] getCeekNewsNationalEntries() {
        RSSGetResult result =  getCeekNewsEntries(CEEK_NEWS_NATIONAL_ADDRESS,national_lastupdated);
        national_lastupdated = result.last_updated;
        return Util.convertToCeekJPNewsEntryArray(result.items);
    }

    private static CeekJPNewsEntry[] getCeekNewsPoliticsEntries() {
        RSSGetResult result =  getCeekNewsEntries(CEEK_NEWS_POLITICS_ADDRESS,politics_lastupdated);
        politics_lastupdated = result.last_updated;
        return Util.convertToCeekJPNewsEntryArray(result.items);
    }

    private static CeekJPNewsEntry[] getCeekNewsWorldEntries() {
        RSSGetResult result =  getCeekNewsEntries(CEEK_NEWS_WORLD_ADDRESS,world_lastupdated);
        world_lastupdated = result.last_updated;
        return Util.convertToCeekJPNewsEntryArray(result.items);
    }

    private static CeekJPNewsEntry[] getCeekNewsChinaCoreaEntries() {
        RSSGetResult result =  getCeekNewsEntries(CEEK_NEWS_CHINA_COREA_ADDRESS,china_corea_lastupdated);
        china_corea_lastupdated = result.last_updated;
        return Util.convertToCeekJPNewsEntryArray(result.items);
    }

    private static CeekJPNewsEntry[] getCeekNewsBusinessEntries() {
        RSSGetResult result =  getCeekNewsEntries(CEEK_NEWS_BUSINESS_ADDRESS,business_lastupdated);
        business_lastupdated = result.last_updated;
        return Util.convertToCeekJPNewsEntryArray(result.items);
    }

    private static CeekJPNewsEntry[] getCeekNewsItEntries() {
        RSSGetResult result =  getCeekNewsEntries(CEEK_NEWS_IT_ADDRESS,it_lastupdated);
        it_lastupdated = result.last_updated;
        return Util.convertToCeekJPNewsEntryArray(result.items);
    }

    private static CeekJPNewsEntry[] getCeekNewsSportsEntries() {
        RSSGetResult result =  getCeekNewsEntries(CEEK_NEWS_SPORTS_ADDRESS,sports_lastupdated);
        sports_lastupdated = result.last_updated;
        return Util.convertToCeekJPNewsEntryArray(result.items);
    }

    private static CeekJPNewsEntry[] getCeekNewsEntertainmentEntries() {
        RSSGetResult result =  getCeekNewsEntries(CEEK_NEWS_ENTERTAINMENT_ADDRESS,entertainment_lastupdated);
        entertainment_lastupdated = result.last_updated;
        return Util.convertToCeekJPNewsEntryArray(result.items);
    }

    private static CeekJPNewsEntry[] getCeekNewsScienceEntries() {
        RSSGetResult result =  getCeekNewsEntries(CEEK_NEWS_SCIENCE_ADDRESS,science_lastupdated);
        science_lastupdated = result.last_updated;
        return Util.convertToCeekJPNewsEntryArray(result.items);
    }

    private static CeekJPNewsEntry[] getCeekNewsObituariesEntries() {
        RSSGetResult result =  getCeekNewsEntries(CEEK_NEWS_OBITUARIES_ADDRESS,obituaries_lastupdated);
        obituaries_lastupdated = result.last_updated;
        return Util.convertToCeekJPNewsEntryArray(result.items);
    }

    private static CeekJPNewsEntry[] getCeekNewsLocalEntries() {
        RSSGetResult result =  getCeekNewsEntries(CEEK_NEWS_LOCAL_ADDRESS,local_lastupdated);
        local_lastupdated = result.last_updated;
        return Util.convertToCeekJPNewsEntryArray(result.items);
    }

    private static CeekJPNewsEntry[] getCeekNewsEtcEntries() {
        RSSGetResult result =  getCeekNewsEntries(CEEK_NEWS_ETC_ADDRESS,etc_lastupdated);
        etc_lastupdated = result.last_updated;
        return Util.convertToCeekJPNewsEntryArray(result.items);
    }

    private static RSSGetResult getCeekNewsEntries(String url,Date last_update) {
        RSSGetResult result = RSSHelper.getRSSItems(url,last_update);
        return result;
    }

    public static CeekJPNewsEntry[] getAllCeekNewsEntries() {
        ArrayList final_result = new ArrayList();

//        CeekJPNewsEntry tmp_entries[] = getCeekNewsRecentEntries();
//        int len = tmp_entries.length;
//        for (int i = 0; i < len; i++) {
//            final_result.add(tmp_entries[i]);
//        }
//
//        tmp_entries = getCeekNewsRecentEntries();
//        len = tmp_entries.length;
//        for (int i = 0; i < len; i++) {
//            final_result.add(tmp_entries[i]);
//        }

        CeekJPNewsEntry tmp_entries[] = getCeekNewsBusinessEntries();
        int len = tmp_entries.length;
        for (int i = 0; i < len; i++) {
            final_result.add(tmp_entries[i]);
        }

        tmp_entries = getCeekNewsChinaCoreaEntries();
        len = tmp_entries.length;
        for (int i = 0; i < len; i++) {
            final_result.add(tmp_entries[i]);
        }

        tmp_entries = getCeekNewsEntertainmentEntries();
        len = tmp_entries.length;
        for (int i = 0; i < len; i++) {
            final_result.add(tmp_entries[i]);
        }

        tmp_entries = getCeekNewsEtcEntries();
        len = tmp_entries.length;
        for (int i = 0; i < len; i++) {
            final_result.add(tmp_entries[i]);
        }

        tmp_entries = getCeekNewsItEntries();
        len = tmp_entries.length;
        for (int i = 0; i < len; i++) {
            final_result.add(tmp_entries[i]);
        }

        tmp_entries = getCeekNewsLocalEntries();
        len = tmp_entries.length;
        for (int i = 0; i < len; i++) {
            final_result.add(tmp_entries[i]);
        }

        tmp_entries = getCeekNewsNationalEntries();
        len = tmp_entries.length;
        for (int i = 0; i < len; i++) {
            final_result.add(tmp_entries[i]);
        }

        tmp_entries = getCeekNewsObituariesEntries();
        len = tmp_entries.length;
        for (int i = 0; i < len; i++) {
            final_result.add(tmp_entries[i]);
        }

        tmp_entries = getCeekNewsPoliticsEntries();
        len = tmp_entries.length;
        for (int i = 0; i < len; i++) {
            final_result.add(tmp_entries[i]);
        }

        tmp_entries = getCeekNewsScienceEntries();
        len = tmp_entries.length;
        for (int i = 0; i < len; i++) {
            final_result.add(tmp_entries[i]);
        }

        tmp_entries = getCeekNewsSportsEntries();
        len = tmp_entries.length;
        for (int i = 0; i < len; i++) {
            final_result.add(tmp_entries[i]);
        }

        tmp_entries = getCeekNewsWorldEntries();
        len = tmp_entries.length;
        for (int i = 0; i < len; i++) {
            final_result.add(tmp_entries[i]);
        }

        int final_len = final_result.size();
        CeekJPNewsEntry final_results_arr[] = new CeekJPNewsEntry[final_len];
        for (int i = 0; i < final_len; i++) {
            final_results_arr[i] = (CeekJPNewsEntry) final_result.get(i);
        }

        return final_results_arr;
    }

    // key=リンクアドレス(String) Value=WecDocumentEntryを要素とする,つまり各エントリの情報を含んだHashMapを返す
    public static HashMap crawl(){
        HashMap result_map = new HashMap();
        CeekJPNewsEntry entries[] = getAllCeekNewsEntries();
        
        int len = entries.length;
        
        CrawledDataManager c_manager = DBCrawledDataManager.getInstance();
        //すでに解析済みのエントリではないかチェックしながらコンバート
        for(int i=0;i<len;i++){
            if(c_manager.getEntryWithAddress(entries[i].url,DBUtil.CEEK_NEWS_TYPE)==null){
                result_map.put(entries[i].url,Util.convertToDocumentEntry(entries[i]));    
            }
        }
        
        System.out.println("Crawle to CEEK.JP News Succeeded!! :" + result_map.size() + "entries");
        
        return result_map;
    }
}
