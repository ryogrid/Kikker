package jp.ryo.informationPump.server.web_server.rss;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import de.nava.informa.core.*;
import de.nava.informa.exporters.*;
import de.nava.informa.impl.basic.ChannelBuilder;

import jp.ryo.informationPump.server.KikkerConfigration;
import jp.ryo.informationPump.server.crawler.*;
import jp.ryo.informationPump.server.data.*;
import jp.ryo.informationPump.server.util.DBUtil;
import jp.ryo.informationPump.server.util.Util;
import jp.ryo.informationPump.server.web_server.WebServer;

public class RssGenerator {
    
    public static String generateHatebuSuggestRSS(UserProfile profile,SearchResult suggested_webs){
        ChannelBuilder builder = new ChannelBuilder();

        ChannelIF newChannel = builder.createChannel("Links Suggest to " + profile.getId());
        newChannel.setFormat(ChannelFormat.RSS_1_0);
        newChannel.setLanguage("ja");
        
        try {
            newChannel.setSite(new URL("http://" + WebServer.HOST_NAME + ":" + WebServer.PORT + "/?id=" + profile.getId() + "&type=html&category=hatebu"));

            newChannel.setLocation(
                new URL("http://" + WebServer.HOST_NAME + ":" + WebServer.PORT + "/?id=" + profile.getId() + "&type=html&category=hatebu"));
            
            int len = suggested_webs.results.length;
            
//          はてブからのsugguestを追加
            CrawledDataManager c_manager = DBCrawledDataManager.getInstance();
            DocumentSearchResult hatebu_sugguested = c_manager.search(profile.getTasteVector(),DBUtil.HATEBU_TYPE);
            for(int i=0;i<((hatebu_sugguested.results.length > KikkerConfigration.ALIGN_HATEBU_RSS_LIMIT)?KikkerConfigration.ALIGN_HATEBU_RSS_LIMIT:hatebu_sugguested.results.length);i++){
                String link_address = "http://" + WebServer.HOST_NAME +  ":" + WebServer.PORT + "/learn?url=" + Util.EscapeSharp(hatebu_sugguested.results[i].getAddress()) + "&category=hatebu";
                String title = hatebu_sugguested.results[i].getTitle();
                int user_view_count = hatebu_sugguested.results[i].getView_users();
                int point = (int)Math.floor(hatebu_sugguested.eval_points[i]*100);  
                
//              Suggestするページに付加するキーワード・参照回数の文字列を作成
                HashMap keywords = hatebu_sugguested.results[i].getKeywords();
                List list = Collections.list(Collections.enumeration(keywords.keySet()));
                StringBuffer buf = new StringBuffer();
                buf.append("keyword:");
                int web_key_len = list.size();
                for(int j=0;j<((web_key_len > KikkerConfigration.ALIGN_KEYWORD_LIMIT)?KikkerConfigration.ALIGN_KEYWORD_LIMIT : web_key_len);j++){
                    buf.append((String)list.get(j) + " ");
                }
                String key_list_str = buf.toString();
                
                ItemIF item =
                    builder.createItem(
                        newChannel,
                        (title==null)?link_address:title,
                        point + "point " + user_view_count + "user " + key_list_str + "<br><br><a href=\"" + Util.HTMLEscape(link_address) + "\">学習用リンク</a>",
                        new URL(hatebu_sugguested.results[i].getAddress()));
                item.setDate(hatebu_sugguested.results[i].getClawledDate());
                newChannel.addItem(item);    
            }
            
            //ユーザーの趣向の列挙を行う要素を1つ生成
            ArrayList sorted_user_taste_vec = Util.getSortedList(profile.getTasteVector());
            StringBuffer taste_buf = new StringBuffer();
            int usr_taste_len = sorted_user_taste_vec.size();
            for(int i=0;i<usr_taste_len;i++){
                KeyAndDoubleTFIDF key_tfidf = (KeyAndDoubleTFIDF)sorted_user_taste_vec.get(i); 
                taste_buf.append(i + "位:" + key_tfidf.tfidf + ":" + key_tfidf.keyword + "\n");
            }
            String keyword_rank_str = taste_buf.toString();
            
            ItemIF item =
                builder.createItem(
                    newChannel,
                    "Links Suggest to " + profile.getId(),
                    keyword_rank_str,
                    new URL("http://" + WebServer.HOST_NAME + ":" + WebServer.PORT + "/?id=" + profile.getId() + "&type=html&category=hatebu"));
            item.setDate(new Date());
            newChannel.addItem(item);    
            
            StringWriter swriter = new StringWriter();
            RSS_1_0_Exporter writer =
                new RSS_1_0_Exporter(swriter,"SJIS");
            writer.write(newChannel);
            
            swriter.write("\r\n");            
            
            return convertDescriptionTag(swriter.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static String generateYoutubeSuggestRSS(UserProfile profile,SearchResult suggested_webs){
        ChannelBuilder builder = new ChannelBuilder();

        ChannelIF newChannel = builder.createChannel("Youtube Video Suggest to " + profile.getId());
        newChannel.setFormat(ChannelFormat.RSS_1_0);
        newChannel.setLanguage("ja");
        
        try {
            newChannel.setSite(new URL("http://" + WebServer.HOST_NAME + ":" + WebServer.PORT + "/?id=" + profile.getId() + "&type=html&category=youtube"));

            newChannel.setLocation(
                new URL("http://" + WebServer.HOST_NAME + ":" + WebServer.PORT + "/?id=" + profile.getId() + "&type=html&category=youtube"));
            
            int len = suggested_webs.results.length;
            
//          Youtubeからのsugguestを追加
            CrawledDataManager c_manager = DBCrawledDataManager.getInstance();
            DocumentSearchResult youtube_sugguested = c_manager.search(profile.getTasteVector(),DBUtil.YOUTUBE_TYPE);
            for(int i=0;i<(youtube_sugguested.results.length>100?100:youtube_sugguested.results.length);i++){
                String link_address = "http://" + WebServer.HOST_NAME +  ":" + WebServer.PORT + "/learn?url=" + Util.EscapeSharp(youtube_sugguested.results[i].getAddress()) + "&category=youtube";
                String title = youtube_sugguested.results[i].getTitle();
                int user_view_count = youtube_sugguested.results[i].getView_users();
                int point = (int)Math.floor(youtube_sugguested.eval_points[i]*100);  
                
//              Suggestするページに付加するキーワード・参照回数の文字列を作成
                HashMap keywords = youtube_sugguested.results[i].getKeywords();
                List list = Collections.list(Collections.enumeration(keywords.keySet()));
                StringBuffer buf = new StringBuffer();
                buf.append("keyword:");
                int web_key_len = list.size();
                for(int j=0;j<((web_key_len > KikkerConfigration.ALIGN_KEYWORD_LIMIT)?KikkerConfigration.ALIGN_KEYWORD_LIMIT : web_key_len);j++){
                    buf.append((String)list.get(j) + " ");
                }
                String key_list_str = buf.toString();
                
                ItemIF item =
                    builder.createItem(
                        newChannel,
                        (title==null)?link_address:title,
                        point + "point " + user_view_count + "user " + key_list_str + "<br><a href=\"" + Util.EscapeSharp(link_address) + "\" target=\"_blank\"><img src=\"" + youtube_sugguested.results[i].getCategory() +  "\"/></a><br><br><a href=\"" + Util.HTMLEscape(link_address) + "\">学習用リンク</a>",
                        new URL(youtube_sugguested.results[i].getAddress()));
                item.setDate(youtube_sugguested.results[i].getClawledDate());
                newChannel.addItem(item);    
            }
            
            //ユーザーの趣向の列挙を行う要素を1つ生成
            ArrayList sorted_user_taste_vec = Util.getSortedList(profile.getTasteVector());
            StringBuffer taste_buf = new StringBuffer();
            int usr_taste_len = sorted_user_taste_vec.size();
            for(int i=0;i<usr_taste_len;i++){
                KeyAndDoubleTFIDF key_tfidf = (KeyAndDoubleTFIDF)sorted_user_taste_vec.get(i); 
                taste_buf.append(i + "位:" + key_tfidf.tfidf + ":" + key_tfidf.keyword + "\n");
            }
            String keyword_rank_str = taste_buf.toString();
            
            ItemIF item =
                builder.createItem(
                    newChannel,
                    "Youtube Video Suggest to " + profile.getId(),
                    keyword_rank_str,
                    new URL("http://" + WebServer.HOST_NAME + ":" + WebServer.PORT + "/?id=" + profile.getId() + "&type=html&category=youtube"));
            item.setDate(new Date());
            newChannel.addItem(item);    
            
            StringWriter swriter = new StringWriter();
            RSS_1_0_Exporter writer =
                new RSS_1_0_Exporter(swriter,"SJIS");
            writer.write(newChannel);
            
            swriter.write("\r\n");
            
//            //無理やりcontentタグを突っ込む
//            String splited_by_description[] = swriter.toString().split("</dc:date>");
//            StringBuffer result_buf = new StringBuffer();
//            result_buf.append(splited_by_description[0]);
//            int counter = 0;
//            for(int i=1;i<splited_by_description.length;i++){
//                result_buf.append("</dc:date>");
//                if(counter < (youtube_sugguested.results.length>100?100:youtube_sugguested.results.length)){
//                    String link_address = youtube_sugguested.results[i].getAddress();
//                    result_buf.append("<content:encoded><![CDATA[");
//                    
//                    int user_view_count = youtube_sugguested.results[i].getView_users();
//                    int point = (int)Math.floor(youtube_sugguested.eval_points[i]*100);  
//                    
////                  Suggestするページに付加するキーワード・参照回数の文字列を作成
//                    HashMap keywords = youtube_sugguested.results[i].getKeywords();
//                    List list = Collections.list(Collections.enumeration(keywords.keySet()));
//                    StringBuffer buf = new StringBuffer();
//                    buf.append("keyword:");
//                    int web_key_len = list.size();
//                    for(int j=0;j<((web_key_len > KikkerConfigration.ALIGN_KEYWORD_LIMIT)?KikkerConfigration.ALIGN_KEYWORD_LIMIT : web_key_len);j++){
//                        buf.append((String)list.get(j) + " ");
//                    }
//                    String key_list_str = buf.toString();
//                    
//                    result_buf.append("<p>" + point + "point " + user_view_count + "user " + key_list_str + "</p>");
//                    result_buf.append("<a href=" + Util.HTMLEscape(link_address)
//                            + "\" target=\"_blank\"><img src=\"" + youtube_sugguested.results[i-1].getCategory() +  "\"/></a>");
//                    result_buf.append("]]></content:encoded>");
//                    counter++;
//                }
//                result_buf.append(splited_by_description[i]);
//            }
            
//          StringBuffer result_buf = new StringBuffer(swriter.toString());
//          for(int i=0;i<hatebu_sugguested.results.length;i++){
//              result_buf.
//          }
            
//            System.out.println(result_buf.toString());
            
//            String result_str = result_buf.toString().replaceAll("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns=\"http://purl.org/rss/1.0/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:sy=\"http://purl.org/rss/1.0/modules/syndication/\">","<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns=\"http://purl.org/rss/1.0/\" xmlns:content=\"http://purl.org/rss/1.0/modules/content/\" xmlns:taxo=\"http://purl.org/rss/1.0/modules/taxonomy/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:syn=\"http://purl.org/rss/1.0/modules/syndication/\" xmlns:admin=\"http://webns.net/mvcb/\">");
            return convertDescriptionTag(swriter.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static String generateCeekJPNewsSuggestRSS(UserProfile profile,SearchResult suggested_webs){
        ChannelBuilder builder = new ChannelBuilder();

        ChannelIF newChannel = builder.createChannel("Links Suggest to " + profile.getId());
        newChannel.setFormat(ChannelFormat.RSS_1_0);
        newChannel.setLanguage("ja");
        
        try {
            newChannel.setSite(new URL("http://" + WebServer.HOST_NAME + ":" + WebServer.PORT + "/?id=" + profile.getId() + "&type=html&category=news"));

            newChannel.setLocation(
                new URL("http://" + WebServer.HOST_NAME + ":" + WebServer.PORT + "/?id=" + profile.getId() + "&type=html&category=news"));
            
//          Newsからのsugguestを追加
            CrawledDataManager c_manager = DBCrawledDataManager.getInstance();
            DocumentSearchResult hatebu_sugguested = c_manager.search(profile.getTasteVector(),DBUtil.CEEK_NEWS_TYPE);
            for(int i=0;i<(hatebu_sugguested.results.length>KikkerConfigration.ALIGN_NEWS_RSS_LIMIT?KikkerConfigration.ALIGN_NEWS_RSS_LIMIT:hatebu_sugguested.results.length);i++){
                String link_address = "http://" + WebServer.HOST_NAME +  ":" + WebServer.PORT + "/learn?url=" + Util.EscapeSharp(hatebu_sugguested.results[i].getAddress()) + "&category=news";
                String title = hatebu_sugguested.results[i].getTitle();
                int point = (int)Math.floor(hatebu_sugguested.eval_points[i]*100);  
                
//              Suggestするページに付加するキーワード
                HashMap keywords = hatebu_sugguested.results[i].getKeywords();
                List list = Collections.list(Collections.enumeration(keywords.keySet()));
                StringBuffer buf = new StringBuffer();
                buf.append("keyword:");
                int web_key_len = list.size();
                for(int j=0;j<((web_key_len > KikkerConfigration.ALIGN_KEYWORD_LIMIT)?KikkerConfigration.ALIGN_KEYWORD_LIMIT : web_key_len);j++){
                    buf.append((String)list.get(j) + " ");
                }
                String key_list_str = buf.toString();
                
                ItemIF item =
                    builder.createItem(
                        newChannel,
                        (title==null)?link_address:title,
                        point + "point " + key_list_str + "<br><br><a href=\"" + Util.HTMLEscape(link_address) + "\">学習用リンク</a>",
                        new URL(hatebu_sugguested.results[i].getAddress()));
                item.setDate(hatebu_sugguested.results[i].getClawledDate());
                newChannel.addItem(item);    
            }
            
            //ユーザーの趣向の列挙を行う要素を1つ生成
            ArrayList sorted_user_taste_vec = Util.getSortedList(profile.getTasteVector());
            StringBuffer taste_buf = new StringBuffer();
            int usr_taste_len = sorted_user_taste_vec.size();
            for(int i=0;i<usr_taste_len;i++){
                KeyAndDoubleTFIDF key_tfidf = (KeyAndDoubleTFIDF)sorted_user_taste_vec.get(i); 
                taste_buf.append(i + "位:" + key_tfidf.tfidf + ":" + key_tfidf.keyword + "\n");
            }
            String keyword_rank_str = taste_buf.toString();
            
            ItemIF item =
                builder.createItem(
                    newChannel,
                    "Links Suggest to " + profile.getId(),
                    keyword_rank_str,
                    new URL("http://" + WebServer.HOST_NAME + ":" + WebServer.PORT + "/?id=" + profile.getId() + "&type=html&category=news"));
            item.setDate(new Date());
            newChannel.addItem(item);    
            
            StringWriter swriter = new StringWriter();
            RSS_1_0_Exporter writer =
                new RSS_1_0_Exporter(swriter,"SJIS");
            writer.write(newChannel);
            
            swriter.write("\r\n");
            
            return convertDescriptionTag(swriter.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static String generateHatebuKeywordRSS(String keyword,StoreBoxForWebData suggested_webs[]){
        ChannelBuilder builder = new ChannelBuilder();

        ChannelIF newChannel = builder.createChannel("キーワード『" + Util.decodeQueryStringUTF8(keyword) + "』を含むページ");
        newChannel.setFormat(ChannelFormat.RSS_1_0);
        newChannel.setLanguage("ja");
        
        try {
            newChannel.setSite(new URL("http://" + WebServer.HOST_NAME + ":" + WebServer.PORT + "/?keyword="+ Util.getQueryString(keyword)+ "&type=html&category=hatebu"));

            newChannel.setLocation(
                new URL("http://" + WebServer.HOST_NAME + ":" + WebServer.PORT + "/?keyword="+ Util.getQueryString(keyword)+ "&type=html&category=hatebu"));
            
//          はてブからのものも追加する        
            CrawledDataManager clawle_manager = DBCrawledDataManager.getInstance();
            StoreBoxForDocumentEntry[] entries = clawle_manager.getTagetKeywordBelongs(keyword,DBUtil.HATEBU_TYPE);
            
            for(int i=0;i<entries.length;i++){
                String link_address = "http://" + WebServer.HOST_NAME +  ":" + WebServer.PORT + "/learn?url=" + Util.EscapeSharp(entries[i].data.getAddress()) + "&category=hatebu";
                String title = entries[i].data.getTitle();
                int user_view_count = entries[i].data.getView_users();
                                
//             Suggestするページに付加するキーワード文字列を作成
                HashMap keywords = entries[i].data.getKeywords();
                ArrayList sorted_web_vec = Util.getSortedList(keywords);
                StringBuffer buf = new StringBuffer();
                int web_key_len = sorted_web_vec.size();
                buf.append("keyword:");
                for(int j=0;j<((web_key_len > KikkerConfigration.ALIGN_KEYWORD_LIMIT)?KikkerConfigration.ALIGN_KEYWORD_LIMIT : web_key_len);j++){
                    buf.append(((KeyAndDoubleTFIDF)(sorted_web_vec.get(j))).keyword + " ");
                }
                String key_list_str = buf.toString();
                
                ItemIF item =
                    builder.createItem(
                        newChannel,
                        ((title==null)?link_address:title),
                        key_list_str + user_view_count + "users" + "<br><br><a href=\"" + Util.HTMLEscape(link_address) + "\">学習用リンク</a>",
                        new URL(entries[i].data.getAddress()));
                item.setDate(entries[i].date);
                newChannel.addItem(item);  
            }
            
            StringWriter swriter = new StringWriter();
            RSS_1_0_Exporter writer =
                new RSS_1_0_Exporter(swriter,"SJIS");
            writer.write(newChannel);
            
            swriter.write("\r\n");
            
            return convertDescriptionTag(swriter.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
   
    public static String generateCeekJPNewsKeywordRSS(String keyword,StoreBoxForWebData suggested_webs[]){
        ChannelBuilder builder = new ChannelBuilder();

        ChannelIF newChannel = builder.createChannel("キーワード『" + Util.decodeQueryStringUTF8(keyword) + "』を含むページ");
        newChannel.setFormat(ChannelFormat.RSS_1_0);
        newChannel.setLanguage("ja");
        
        try {
            newChannel.setSite(new URL("http://" + WebServer.HOST_NAME + ":" + WebServer.PORT + "/?keyword="+ Util.getQueryString(keyword)+ "&type=html&category=news"));

            newChannel.setLocation(
                new URL("http://" + WebServer.HOST_NAME + ":" + WebServer.PORT + "/?keyword="+ Util.getQueryString(keyword)+ "&type=html&category=news"));
            
            CrawledDataManager clawle_manager = DBCrawledDataManager.getInstance();
            StoreBoxForDocumentEntry[] entries = clawle_manager.getTagetKeywordBelongs(keyword,DBUtil.CEEK_NEWS_TYPE);
            
            for(int i=0;i<((entries.length > KikkerConfigration.ALIGN_NEWS_RSS_LIMIT)?KikkerConfigration.ALIGN_NEWS_RSS_LIMIT:entries.length);i++){
                String link_address = "http://" + WebServer.HOST_NAME +  ":" + WebServer.PORT + "/learn?url=" + Util.EscapeSharp(entries[i].data.getAddress()) + "&category=news";
                String title = entries[i].data.getTitle();
                                
//             Suggestするページに付加するキーワード文字列を作成
                HashMap keywords = entries[i].data.getKeywords();
                ArrayList sorted_web_vec = Util.getSortedList(keywords);
                StringBuffer buf = new StringBuffer();
                int web_key_len = sorted_web_vec.size();
                buf.append("keyword:");
                for(int j=0;j<((web_key_len > KikkerConfigration.ALIGN_KEYWORD_LIMIT)?KikkerConfigration.ALIGN_KEYWORD_LIMIT : web_key_len);j++){
                    buf.append(((KeyAndDoubleTFIDF)(sorted_web_vec.get(j))).keyword + " ");
                }
                String key_list_str = buf.toString();
                
                ItemIF item =
                    builder.createItem(
                        newChannel,
                        ((title==null)?link_address:title),
                        key_list_str + "<br><br><a href=\"" + Util.HTMLEscape(link_address) + "\">学習用リンク</a>",
                        new URL(entries[i].data.getAddress()));
                item.setDate(entries[i].date);
                newChannel.addItem(item);  
            }
            
            StringWriter swriter = new StringWriter();
            RSS_1_0_Exporter writer =
                new RSS_1_0_Exporter(swriter,"SJIS");
            writer.write(newChannel);
            
            swriter.write("\r\n");
            
            return convertDescriptionTag(swriter.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    //"<dc:description>"を<description>と置き換える
    private static String convertDescriptionTag(String str){
        String tmpStr =  str.replaceAll("<dc:description>","<description>");
        return tmpStr.replaceAll("</dc:description>","</description>");
    }
}
