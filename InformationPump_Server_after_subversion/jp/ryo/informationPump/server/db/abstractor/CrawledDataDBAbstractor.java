package jp.ryo.informationPump.server.db.abstractor;

import java.io.Serializable;
import java.sql.Date;

import jp.ryo.informationPump.server.data.StoreBoxForDocumentEntry;
import jp.ryo.informationPump.server.db.WebDataDBManagerWrapper;
import jp.ryo.informationPump.server.db.tables.Webdocumententry;
import jp.ryo.informationPump.server.util.DBUtil;

public class CrawledDataDBAbstractor implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private static CrawledDataDBAbstractor instance;
    private WebDataDBManagerWrapper w_wrapper;
    
    private CrawledDataDBAbstractor(){
        w_wrapper = WebDataDBManagerWrapper.getInstance();
    }
    
    public static CrawledDataDBAbstractor getInstance(){
        if(instance==null){
            instance = new CrawledDataDBAbstractor();
        }
        return instance;
    }
    
    public StoreBoxForDocumentEntry[] getEntryWithURL(String url,int doc_type){
        Webdocumententry results[] =  w_wrapper.selectWithAddress(url,doc_type);
        if((results!=null)&&(results.length>=1)){
            return DBUtil.convertToStoreDocumentEntryArray(results);    
        }else{
            return null;
        }
    }
    
    //エントリ本体、タグ、キーワードも各テーブルへ追加してくれる
    public void putNewEntry(StoreBoxForDocumentEntry entry){
        w_wrapper.insertEntry(entry.data.getTitle(),entry.data.getAddress(),entry.data.getView_users(),entry.data.getView_users(),new Date(entry.date.getTime()),entry.data.getCategory(),entry.data.getDescription(),DBUtil.HATEBU_TYPE,entry.data.getKeywords(),entry.data.getTags(),((entry.data.isPreciseAnalyzed()==true)?1:0));
    }
    
    public StoreBoxForDocumentEntry[] getTargetsKeywordBelong(String keyword,int doc_type){
        if(doc_type == DBUtil.YOUTUBE_TYPE){
            if(DBCacheManager.isCached(DBCacheManager.YOUTUBE_CACHE_PREFIX + keyword)){
                return (StoreBoxForDocumentEntry[])DBCacheManager.getCache(DBCacheManager.YOUTUBE_CACHE_PREFIX + keyword);
            }else{
                StoreBoxForDocumentEntry result[]  = DBUtil.convertToStoreDocumentEntryArray(w_wrapper.selectEntryWithKeyword(keyword,doc_type));
                DBCacheManager.putCache(DBCacheManager.YOUTUBE_CACHE_PREFIX + keyword, result);
                return result;
            }
        }else{
            return  DBUtil.convertToStoreDocumentEntryArray(w_wrapper.selectEntryWithKeyword(keyword,doc_type));    
        }
    }
    
    //対象のキーワードに属し,同時に指定した時間より後にクロールされたエントリを取得
    public StoreBoxForDocumentEntry[] getTargetsKeywordBelongAfterADate(String keyword,Date date,int doc_type){
        return  DBUtil.convertToStoreDocumentEntryArray(w_wrapper.selectWithKeywordAfterADate(keyword,date,doc_type));
    }
    
    public void updateNewEntry(StoreBoxForDocumentEntry entry){
        w_wrapper.updateEntry(DBUtil.convertToWebdocumententry(entry));
    }
    
    public StoreBoxForDocumentEntry[] getAllEntries(int doc_type){
        if(doc_type == DBUtil.HATEBU_TYPE){
            if(DBCacheManager.isCached(DBCacheManager.ALL_HATEBU_ENTRY_CACHE)){
                System.out.println("ALL_HATEBU_ENTRY_CACHE hited!!");
                return (StoreBoxForDocumentEntry[])DBCacheManager.getCache(DBCacheManager.ALL_HATEBU_ENTRY_CACHE);
            }else{
                StoreBoxForDocumentEntry entries[] = DBUtil.convertToStoreDocumentEntryArray(w_wrapper.selectAll(doc_type));    
                DBCacheManager.putCache(DBCacheManager.ALL_HATEBU_ENTRY_CACHE,entries);
                return entries; 
            }    
        }else if(doc_type == DBUtil.CEEK_NEWS_TYPE){
            if(DBCacheManager.isCached(DBCacheManager.ALL_CEEK_NEWS_ENTRY_CACHE)){
                System.out.println("ALL_CEEK_NEWS_ENTRY_CACHE hited!!");
                return (StoreBoxForDocumentEntry[])DBCacheManager.getCache(DBCacheManager.ALL_CEEK_NEWS_ENTRY_CACHE);
            }else{
                StoreBoxForDocumentEntry entries[] = DBUtil.convertToStoreDocumentEntryArray(w_wrapper.selectAll(doc_type));    
                DBCacheManager.putCache(DBCacheManager.ALL_CEEK_NEWS_ENTRY_CACHE,entries);
                return entries; 
            }
        }else if(doc_type == DBUtil.YOUTUBE_TYPE){
            if(DBCacheManager.isCached(DBCacheManager.ALL_YOUTUBE_ENTRY_CACHE)){
                System.out.println("ALL_YOUTUBE_ENTRY_CACHE hited!!");
                return (StoreBoxForDocumentEntry[])DBCacheManager.getCache(DBCacheManager.ALL_YOUTUBE_ENTRY_CACHE);
            }else{
                StoreBoxForDocumentEntry entries[] = DBUtil.convertToStoreDocumentEntryArray(w_wrapper.selectAll(doc_type));    
                DBCacheManager.putCache(DBCacheManager.ALL_YOUTUBE_ENTRY_CACHE,entries);
                return entries; 
            }  
        }else{
            throw new RuntimeException();
        }
    }
    
    public void removeEntriesBeforeADate(java.util.Date date,int doc_type){
        w_wrapper.deleteBeforeADateEntries(new Date(date.getTime()),doc_type);
    }
    
}
