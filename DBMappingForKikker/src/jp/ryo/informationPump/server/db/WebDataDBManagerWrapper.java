package jp.ryo.informationPump.server.db;

import java.sql.*;
import java.sql.Date;
import java.util.*;


import jp.crossfire.framework.database.HyperDbException;
import jp.ryo.informationPump.server.db.tables.*;

public class WebDataDBManagerWrapper extends DBManager {
    private static final long serialVersionUID = 1L;
    private static WebDataDBManagerWrapper instance;
    
    private WebDataDBManagerWrapper() {
        super();
    }
    
    public Webdocumententry[] selectBeforeADateEntries(java.util.Date date,int doc_type){ 
        WebDataDBManager webManager = new WebDataDBManager();
        Hashtable input0 = new Hashtable();
        input0.put("date",new Date(date.getTime()));
        input0.put("doc_type",new Integer(doc_type));
        
        try {
            Connection con = getConnection(true);
            List list = null;
            try{
                list = webManager.selectEntriesBeforeADate(con,input0,null,null);    
            }finally{
                returnConnection(con,true);    
            }
            
            Iterator itr = list.iterator();
            Webdocumententry entries[] = new Webdocumententry[list.size()];
            int count = 0;
            while(itr.hasNext()){
                Hashtable tmp_entry = (Hashtable)itr.next();
                Webdocumententry new_entry = new Webdocumententry();
                new_entry.setTitle((String)tmp_entry.get("TITLE"));
                new_entry.setUrl((String)tmp_entry.get("URL"));
                new_entry.setCategory((String)tmp_entry.get("CATEGORY"));
                new_entry.setBookmarkCount((Integer)tmp_entry.get("BOOKMARK_COUNT"));
                new_entry.setCrawleddate((Date)tmp_entry.get("CRAWLEDDATE"));
                new_entry.setDescription((String)tmp_entry.get("DESCRIPTION"));
                new_entry.setViewUserCount((Integer)tmp_entry.get("VIEW_USER_COUNT"));
                new_entry.setDocType((Integer)tmp_entry.get("DOC_TYPE"));
                new_entry.setIsanalyzed((Integer)tmp_entry.get("ISANALYZED"));
                
                new_entry.setKey_tables(getKeywordTableWithAddress(new_entry.getUrl()));
                new_entry.setTag_tables(getTagTableWithAddress(new_entry.getUrl()));
                
                entries[count++] = new_entry;
            }
            
            return entries;
        } catch (HyperDbException e) {
            e.printStackTrace();
            return null;
        }
        
    }
    
    public void deleteBeforeADateEntries(java.util.Date date,int doc_type) {
        try {
            Webdocumententry selected[] =  selectBeforeADateEntries(date,doc_type);
            
            //値を持っておく
            WebDataDBManager webManager = new WebDataDBManager();
            Hashtable input0 = new Hashtable();
            input0.put("date",new Date(date.getTime()));
            input0.put("doc_type",new Integer(doc_type));
            
            //大本を削除
            Connection con = getConnection(true);
            try{
                webManager.deleteEntriesBeforeADate(con,input0);    
            }finally{
                returnConnection(con,true);    
            }
            
            //関連するエントリも削除しておく
            int len = selected.length;
            UserDBManager u_manager = new UserDBManager();
            for(int i=0;i<len;i++){
                Hashtable input1 = new Hashtable();
                input1.put("url",selected[i].getUrl());
                Connection con2 = getConnection(true);
                try{
                    u_manager.deleteUserViewedURLWithURL(con2,input1);    
                }finally{
                    returnConnection(con2,true);    
                }
                
                Hashtable input2 = new Hashtable();
                input2.put("address",selected[i].getUrl());
                
                con = getConnection(true);
                try{
                    webManager.deleteKeywordsWithAddress(con,input2);    
                }finally{
                    returnConnection(con,true);    
                }
                
                con = getConnection(true);
                try{
                    webManager.deleteTagWithAddress(con,input2);    
                }finally{
                    returnConnection(con,true);    
                }
            }
        } catch (HyperDbException e) {
            e.printStackTrace();
        }
    }
    
    public Webdocumententry[] selectWithKeywordAfterADate(String keyword,java.util.Date date,int doc_type){
        
        WebDataDBManager webManager = new WebDataDBManager();
        
        Hashtable input0 = new Hashtable();
        input0.put("keyword",keyword);
        input0.put("date",new java.sql.Date(date.getTime()));
        input0.put("doc_type",new Integer(doc_type));
        try {
            Connection con = getConnection(true);
            List list = null;
            try{
                list = webManager.selectDocumentsWithKeyAndDate(con,input0,null,null);    
            }finally{
                returnConnection(con,true);    
            }

            Iterator itr = list.iterator();
            Webdocumententry entries[] = new Webdocumententry[list.size()];
            int count = 0;
            while(itr.hasNext()){
                Hashtable tmp_entry = (Hashtable)itr.next();
                Webdocumententry new_entry = new Webdocumententry();
                new_entry.setTitle((String)tmp_entry.get("TITLE"));
                new_entry.setUrl((String)tmp_entry.get("URL"));
                new_entry.setCategory((String)tmp_entry.get("CATEGORY"));
                new_entry.setBookmarkCount((Integer)tmp_entry.get("BOOKMARK_COUNT"));
                new_entry.setCrawleddate((Date)tmp_entry.get("CRAWLEDDATE"));
                new_entry.setDescription((String)tmp_entry.get("DESCRIPTION"));
                new_entry.setViewUserCount((Integer)tmp_entry.get("VIEW_USER_COUNT"));
                new_entry.setDocType((Integer)tmp_entry.get("DOC_TYPE"));
                new_entry.setIsanalyzed((Integer)tmp_entry.get("ISANALYZED"));
                
                new_entry.setKey_tables(getKeywordTableWithAddress(new_entry.getUrl()));
                new_entry.setTag_tables(getTagTableWithAddress(new_entry.getUrl()));
                
                entries[count++] = new_entry;
            }
            
            return entries;
        } catch (HyperDbException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public Webdocumententry[] selectEntryWithKeyword(String keyword,int doc_type){
        
        WebDataDBManager webManager = new WebDataDBManager();
        
        Hashtable input0 = new Hashtable();
        input0.put("keyword",keyword);
        input0.put("doc_type",new Integer(doc_type));
        try {
            Connection con = getConnection(true);
            List list = null;
            try{
                list = webManager.selectDocumentsWithKey(con,input0,null,null);    
            }finally{
                returnConnection(con,true);    
            }
            
            
            Iterator itr = list.iterator();
            Webdocumententry entries[] = new Webdocumententry[list.size()];
            int count = 0;
            while(itr.hasNext()){
                Hashtable tmp_entry = (Hashtable)itr.next();
                Webdocumententry new_entry = new Webdocumententry();
                new_entry.setTitle((String)tmp_entry.get("TITLE"));
                new_entry.setUrl((String)tmp_entry.get("URL"));
                new_entry.setCategory((String)tmp_entry.get("CATEGORY"));
                new_entry.setBookmarkCount((Integer)tmp_entry.get("BOOKMARK_COUNT"));
                new_entry.setCrawleddate((Date)tmp_entry.get("CRAWLEDDATE"));
                new_entry.setDescription((String)tmp_entry.get("DESCRIPTION"));
                new_entry.setViewUserCount((Integer)tmp_entry.get("VIEW_USER_COUNT"));
                new_entry.setDocType((Integer)tmp_entry.get("DOC_TYPE"));
                new_entry.setIsanalyzed((Integer)tmp_entry.get("ISANALYZED"));
                
                new_entry.setKey_tables(getKeywordTableWithAddress(new_entry.getUrl()));
                new_entry.setTag_tables(getTagTableWithAddress(new_entry.getUrl()));
                
                entries[count++] = new_entry;
            }
            
            return entries;
        } catch (HyperDbException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public void updateEntry(Webdocumententry entry){ 
        try{
//          まずは削除
            deleteEntry(entry.getUrl(),entry.getDocType().intValue());
            //配列からHashMapへ
            WebdocumentKeywordTable key_tables[] =  entry.getKey_tables();
            HashMap key_table_map = new HashMap();
            int len = key_tables.length;
            for(int i=0;i<len;i++){
                key_table_map.put(key_tables[i].getKeyword(),key_tables[i].getTfidfValue());
            }
            WebdocumentTagTable tag_tables[] =  entry.getTag_tables();
            HashMap tag_table_map = new HashMap();
            len = tag_tables.length;
            for(int i=0;i<len;i++){
                tag_table_map.put(tag_tables[i].getTag(),tag_tables[i].getTfidfValue());
            }
            
            //新しいエントリとして追加
            insertEntry(entry.getTitle(),entry.getUrl(),entry.getBookmarkCount().intValue(),entry.getViewUserCount().intValue(),entry.getCrawleddate(),entry.getCategory(),entry.getDescription(),entry.getDocType().intValue(),key_table_map,tag_table_map,entry.getIsanalyzed().intValue());    
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    public WebdocumentKeywordTable[] getKeywordTableWithAddress(String address) {
        
        
        WebdocumentKeywordTable results[];
        try {
            WebDataDBManager webManager = new WebDataDBManager();
            Hashtable input0 = new Hashtable();
            input0.put("address",address);
            Connection con = getConnection(true);
            List list = null;
            try{
                list = webManager.selectKeyTableWithAddress(con,input0,null,null);
            }finally{
                returnConnection(con,true);    
            }
            
            Iterator itr = list.iterator();
            results = new WebdocumentKeywordTable[list.size()];
            int count=0;
            while(itr.hasNext()){
                Hashtable entry = (Hashtable)itr.next();
                WebdocumentKeywordTable new_entry = new WebdocumentKeywordTable();
                new_entry.setDocAddress((String)entry.get("DOC_ADDRESS"));
                new_entry.setKeyword((String)entry.get("KEYWORD"));
                new_entry.setTfidfValue((Double)entry.get("TFIDF_VALUE"));
                results[count++] = new_entry;
            }
            
            return results; 
        } catch (HyperDbException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public WebdocumentTagTable[] getTagTableWithAddress(String address) {
        WebdocumentTagTable results[];
        try {
            WebDataDBManager webManager = new WebDataDBManager();
            Hashtable input0 = new Hashtable();
            input0.put("address",address);
            Connection con = getConnection(true);
            List list = null;
            try{
                list = webManager.selectTagTableWithAddress(con,input0,null,null);                
            }finally{
                returnConnection(con,true);    
            }
            
            Iterator itr = list.iterator();
            results = new WebdocumentTagTable[list.size()];
            int count=0;
            while(itr.hasNext()){
                Hashtable entry = (Hashtable)itr.next();
                WebdocumentTagTable new_entry = new WebdocumentTagTable();
                new_entry.setDocAddress((String)entry.get("DOC_ADDRESS"));
                new_entry.setTag((String)entry.get("TAG"));
                new_entry.setTfidfValue((Double)entry.get("TFIDF_VALUE"));
                results[count++] = new_entry;
            }
            
            return results; 
        } catch (HyperDbException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static WebDataDBManagerWrapper getInstance(){
        if(instance==null){
            instance = new WebDataDBManagerWrapper();
        }
        return instance;
    }
    
    public void deleteAll() {
        WebDataDBManager webManager = new WebDataDBManager();
        Hashtable input0 = new Hashtable();
        try {
            Connection con = getConnection(true);
            try{
                webManager.deleteKeywordAll(con,input0);    
            }finally{
                returnConnection(con,true);    
            }
            
            con = getConnection(true);
            try{
                webManager.deleteTagAll(con,input0);    
            }finally{
                returnConnection(con,true);    
            }
            
            con = getConnection(true);
            try{
                webManager.deleteDocumentAll(con,input0);    
            }finally{
                returnConnection(con,true);    
            }
        } catch (HyperDbException e) {
            e.printStackTrace();
        }
    }
    
    public Webdocumententry[] selectAll(int doc_type) {
        try {
            WebDataDBManager webManager = new WebDataDBManager();
            Hashtable input0 = new Hashtable();
            input0.put("doc_type",new Integer(doc_type));
            Connection con = getConnection(true);
            List list = null;
            try{
                list = webManager.selectAll(con,input0,null,null);    
            }finally{
                returnConnection(con,true);    
            }
            
            Iterator itr = list.iterator();
            Webdocumententry entries[] = new Webdocumententry[list.size()];
            int count=0;
            while(itr.hasNext()){
                try {
                    Hashtable tmp_entry = (Hashtable) itr.next();
                    Webdocumententry new_entry = new Webdocumententry();
                    new_entry.setTitle((String) tmp_entry.get("TITLE"));
                    new_entry.setUrl((String) tmp_entry.get("URL"));
                    new_entry.setCategory((String) tmp_entry.get("CATEGORY"));
                    new_entry.setBookmarkCount((Integer) tmp_entry
                            .get("BOOKMARK_COUNT"));
                    new_entry.setCrawleddate((Date) tmp_entry
                            .get("CRAWLEDDATE"));
                    new_entry.setDescription((String) tmp_entry
                            .get("DESCRIPTION"));
                    new_entry.setViewUserCount((Integer) tmp_entry
                            .get("VIEW_USER_COUNT"));
                    new_entry.setDocType((Integer) tmp_entry.get("DOC_TYPE"));
                    new_entry.setIsanalyzed((Integer) tmp_entry
                            .get("ISANALYZED"));
                    new_entry
                            .setKey_tables(getKeywordTableWithAddress(new_entry
                                    .getUrl()));
                    new_entry.setTag_tables(getTagTableWithAddress(new_entry
                            .getUrl()));
                    entries[count++] = new_entry;
                } catch (Exception e) {
                    e.printStackTrace();
                }                
            }
            
            return entries;
        } catch (HyperDbException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public Webdocumententry[] selectWithAddress(String url,int doc_type) {
        try {
            WebDataDBManager webManager = new WebDataDBManager();
            Hashtable input0 = new Hashtable();
            input0.put("address",url);
            input0.put("doc_type",new Integer(doc_type));
            Connection con = getConnection(true);
            List list = null;
            try{
                list = webManager.selectWithAddress(con,input0,null,null);    
            }finally{
                returnConnection(con,true);    
            }
            
            Iterator itr = list.iterator();
            Webdocumententry entries[] = new Webdocumententry[list.size()];
            int count=0;
            while(itr.hasNext()){
                Hashtable tmp_entry = (Hashtable)itr.next();
                Webdocumententry new_entry = new Webdocumententry();
                new_entry.setTitle((String)tmp_entry.get("TITLE"));
                new_entry.setUrl((String)tmp_entry.get("URL"));
                new_entry.setCategory((String)tmp_entry.get("CATEGORY"));
                new_entry.setBookmarkCount((Integer)tmp_entry.get("BOOKMARK_COUNT"));
                new_entry.setCrawleddate((Date)tmp_entry.get("CRAWLEDDATE"));
                new_entry.setDescription((String)tmp_entry.get("DESCRIPTION"));
                new_entry.setViewUserCount((Integer)tmp_entry.get("VIEW_USER_COUNT"));
                new_entry.setDocType((Integer)tmp_entry.get("DOC_TYPE"));
                new_entry.setIsanalyzed((Integer)tmp_entry.get("ISANALYZED"));
                
                new_entry.setKey_tables(getKeywordTableWithAddress(url));
                new_entry.setTag_tables(getTagTableWithAddress(url));
                
                entries[count++] = new_entry;
            }
            
            return entries;
        } catch (HyperDbException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public void deleteEntry(String url,int doc_type) {
        try {
            WebDataDBManager webManager = new WebDataDBManager();
            Hashtable input0 = new Hashtable();
            input0.put("address",url);
            input0.put("doc_type",new Integer(doc_type));
            
            Connection con = getConnection(true);
            try{
                webManager.deleteKeywordsWithAddress(con,input0);    
            }finally{
                returnConnection(con,true);    
            }

            con = getConnection(true);
            try{
                webManager.deleteTagWithAddress(con,input0);    
            }finally{
                returnConnection(con,true);    
            }
            
            con = getConnection(true);
            try{
                webManager.deleteEntryWithAddress(con,input0);    
            }finally{
                returnConnection(con,true);    
            }
            
            Hashtable input1 = new Hashtable();
            input1.put("url",url);
            input0.put("doc_type",new Integer(doc_type));
            con = getConnection(true);
            try{
                UserDBManager u_manager = new UserDBManager();
                u_manager.deleteUserViewedURLWithURL(con,input0);                
            }finally{
                returnConnection(con,true);    
            }
        } catch (HyperDbException e) {
            e.printStackTrace();
        }
    }
    
    //※タイトルがあまりに長すぎる場合は100文字に
    public void insertEntry(String title,String url,int bookmark_count,int view_user_count,Date crawled_date,String category,String description,int doc_type,HashMap key_table,HashMap tag_table,int isAnalyzed) {
        try {
            //キーワードの配列に対応するデータをDBへ
            
            if(key_table!=null){
                Set entry_set = key_table.entrySet();
                Iterator itr = entry_set.iterator();
                while(itr.hasNext()){
                    Map.Entry entry = (Map.Entry)itr.next();
                    String keyword = (String)entry.getKey();
                    Double tfidf = (Double)entry.getValue();
                    
                    Hashtable input0 = new Hashtable();
                    input0.put("doc_address",url);
                    input0.put("keyword",keyword.toLowerCase());
                    input0.put("tfidf_value",tfidf);
                    
                    WebDataDBManager webDataManager = new WebDataDBManager();
                    Connection con = getConnection(true);
                    try{
                        webDataManager.insertNewKeyword(con,input0);    
                    }finally{
                        returnConnection(con,true);    
                    }
                }
            }
            
            
            //タグの配列に対応するデータをDBへ
            if(tag_table!=null){
                Set entry_set = tag_table.entrySet();
                Iterator itr = entry_set.iterator();
                while(itr.hasNext()){
                    Map.Entry entry = (Map.Entry)itr.next();
                    String tag = (String)entry.getKey();
                    Double tfidf = (Double)entry.getValue();
                    
                    Hashtable input0 = new Hashtable();
                    input0.put("doc_address",url);
                    input0.put("tag",tag.toLowerCase());
                    input0.put("tfidf_value",tfidf);
                    
                    WebDataDBManager webDataManager = new WebDataDBManager();
                    Connection con = getConnection(true);
                    try{
                        webDataManager.insertNewTag(con,input0);    
                    }finally{
                        returnConnection(con,true);    
                    }
                }
            }
            
            
            
            Hashtable input0 = new Hashtable();
            if(title.length()>100){
                title = title.substring(0,99);
            }
            input0.put("title",title);
            input0.put("url",url);
            input0.put("bookmark_count",new Integer(bookmark_count));
            input0.put("view_user_count",new Integer(view_user_count));
            input0.put("crawledDate",crawled_date);
            input0.put("category",category);
            input0.put("Description",description);
            input0.put("doc_type",new Integer(doc_type));
            input0.put("isAnalyzed",new Integer(isAnalyzed));
            
            WebDataDBManager webDataManager = new WebDataDBManager();
            Connection con = getConnection(true);
            try{
                webDataManager.insertNewEntry(con, input0);    
            }finally{
                returnConnection(con,true);    
            }
            
        } catch (HyperDbException e) {
            e.printStackTrace();
        }
    }
    
}
