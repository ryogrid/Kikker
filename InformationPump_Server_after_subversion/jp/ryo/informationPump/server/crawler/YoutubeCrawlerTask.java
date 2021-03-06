package jp.ryo.informationPump.server.crawler;

import java.util.*;

import org.htmlparser.Node;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.*;
import org.htmlparser.util.*;

import youtube.YoutubeHelper;

import jp.ryo.informationPump.server.crawler.analyze.DependentDocumentAnalyzer;
import jp.ryo.informationPump.server.data.DocumentEntry;
import jp.ryo.informationPump.server.db.abstractor.DBCacheManager;
import jp.ryo.informationPump.server.helper.HatebuHelper;
import jp.ryo.informationPump.server.persistent.PersistentManager;
import jp.ryo.informationPump.server.util.*;

//categoryに画像のURL,DescriptionにIDを入れておく
public class YoutubeCrawlerTask extends TimerTask{
    public static final int CRAWL_PERIOD_SECONDS = 420;
    public static final int A_TIME_CRAWL_PAGE_COUNT = 15;
    private static final Double DEFAULT_TAG_VALUE = new Double(500);
    
    private static final String YOUTUBE_CRAWL_BASE_URL = "http://www.youtube.com";
    private static final String YOUTBUE_RECENT_PAGE_QUERY = "/browse?s=mr&t=&c=0&p=";
    
    public void run() {
        try {
//          クロールがドキュメントのリクエスト受信の妨げにならないように優先度を下げる
            Thread th = Thread.currentThread();
            th.setPriority(Thread.MIN_PRIORITY);
            
            //Youtubeをクロール
            crawleYoutube();
            
//          キャッシュを一掃する
//            DBCacheManager.removeCache(DBCacheManager.ALL_YOUTUBE_ENTRY_CACHE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HashMap crawleYoutube() {
//        CrawledDataManager manager = DBCrawledDataManager.getInstance();
        
        crawl(A_TIME_CRAWL_PAGE_COUNT);
//        HashMap crawledResult = crawl(A_TIME_CRAWL_PAGE_COUNT);
//        manager.margeClawledData(crawledResult,DBUtil.YOUTUBE_TYPE,true);

        PersistentManager p_manager = PersistentManager.getInstance();
        
        //各オブジェクトの保存を行う
//        p_manager.escapeObjectsToFile();
        //ログの出力も行う
//        ServerLogger.writeLog("Hatebu crawled");
        
        System.out.println("Youtube Crawling suceeded " + new Date());
        return null;
    }
    
    public static HashMap crawl(int crawl_pages){
        HashMap result_map = new HashMap();
        
        CrawledDataManager manager = DBCrawledDataManager.getInstance();
        
        for(int i=1;i<=crawl_pages;i++){
//            RequestCounter.checkCanDoCommunicate();
            
            String html = Util.doGet(YOUTUBE_CRAWL_BASE_URL + YOUTBUE_RECENT_PAGE_QUERY + i);
            Lexer lexer = new Lexer(html);
            org.htmlparser.Parser parser = new org.htmlparser.Parser(lexer);
            
//            System.out.println(html);
            Date current_date = new Date();
            try {
                NodeList entry_list = parser.extractAllNodesThatMatch(new HasAttributeFilter("class","v120vEntry"));
                SimpleNodeIterator entry_itr =  entry_list.elements();
//                HashMap crawledDatas = new HashMap();
                while(entry_itr.hasMoreNodes()){
                    try {
                        Node entry = entry_itr.nextNode();
                        NodeList each_divs = entry.getChildren();
                        
                        //画像へのリンクとビデオページへのリンクを得る
                        NodeList vstill_divs = each_divs.extractAllNodesThatMatch(new HasAttributeFilter("class","vstill"));
                        Node vstill_div_arr[] = vstill_divs.toNodeArray();
                        Node a_vstill =vstill_div_arr[0];
                        LinkTag vstill_a_tag = (LinkTag)a_vstill.getFirstChild().getNextSibling();
                        String video_link = vstill_a_tag.getLink();
                        ImageTag video_image_tag = (ImageTag) vstill_a_tag.getFirstChild();
                        String image_link = video_image_tag.getImageURL();
                        
                        //タイトルを得る
                        NodeList vtitle_divs = each_divs.extractAllNodesThatMatch(new HasAttributeFilter("class","vtitle"));
                        Node vtitle_div_arr[] = vtitle_divs.toNodeArray();
                        Node a_vtitle =vtitle_div_arr[0];
                        LinkTag vtitle_a_tag = (LinkTag)a_vtitle.getFirstChild().getNextSibling();
                        String video_title = vtitle_a_tag.getLinkText();

                        //閲覧数を得る
                        NodeList vfacets_divs = each_divs.extractAllNodesThatMatch(new HasAttributeFilter("class","vfacets"));
                        Node vfacets_div_arr[] = vfacets_divs.toNodeArray();
                        Node a_vfacets =vfacets_div_arr[0];
                        Node vfacets_children[] = a_vfacets.getChildren().toNodeArray();
                        Node count_node = vfacets_children[12];
                        int view_count = Integer.parseInt(count_node.getText().trim()); 

                        //ビデオのページを取得してそこからタグ情報を持ってくる
                        String video_page_html = Util.doGet(YOUTUBE_CRAWL_BASE_URL + video_link);
                        Lexer video_page_lexer = new Lexer(video_page_html);
                        org.htmlparser.Parser video_page_parser = new org.htmlparser.Parser(video_page_lexer);
                        NodeList tags_list = video_page_parser.extractAllNodesThatMatch(new HasAttributeFilter("class","tags"));
                        if(tags_list.size() == 0){
                            continue;
                        }
                        Node tags_list_arr[] = tags_list.toNodeArray();
                        Node children_arr[] = tags_list_arr[0].getChildren().toNodeArray();
                        
                        HashMap keywords_map = new HashMap();
                        
                        Node tags_begin = children_arr[1];
                        NodeList tags_begin_list = tags_begin.getChildren();
                        SimpleNodeIterator tags_begin_itr = tags_begin_list.elements();
                        while(tags_begin_itr.hasMoreNodes()){
                            Node tmp_node = tags_begin_itr.nextNode();
                            if(tmp_node instanceof LinkTag){
                              keywords_map.put(((LinkTag)tmp_node).getLinkText(),DEFAULT_TAG_VALUE);
                            }
                        }
                        
                        if(children_arr.length >= 4){
                            Node tags_remain = children_arr[3];
                            NodeList tags_remain_list = tags_remain.getChildren();
                            SimpleNodeIterator tags_remain_itr = tags_remain_list.elements();
                            while(tags_remain_itr.hasMoreNodes()){
                                Node tmp_node = tags_remain_itr.nextNode();
                                if(tmp_node instanceof LinkTag){
                                  keywords_map.put(((LinkTag)tmp_node).getLinkText(),DEFAULT_TAG_VALUE);
                                }
                            }                        
                        }

                        DocumentEntry new_entry = new DocumentEntry(YOUTUBE_CRAWL_BASE_URL + video_link,current_date,keywords_map,new HashMap(),video_title,view_count,image_link,DBUtil.YOUTUBE_TYPE);
                        new_entry.setClawledDate(current_date);
                        String splited1[] = video_link.split("=");
                        String splited2[] = splited1[1].split("&");
                        new_entry.setDescription(splited2[0]);
                        new_entry.setSorted_keywords(new ArrayList());
                        
                        HashMap tmp_map = new HashMap();
                        tmp_map.put(video_link,new_entry);
                        
                        manager.margeClawledData(tmp_map,DBUtil.YOUTUBE_TYPE,true);
//                        crawledDatas.put(video_link,new_entry);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
//                result_map.putAll(crawledDatas);
            } catch (ParserException e1) {
                e1.printStackTrace();
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        
        System.out.println("Crawle to Youtube Succeeded!!");
        return result_map;
    }
}
