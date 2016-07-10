package jp.ryo.informationPump.server.db.abstractor;

import java.util.HashMap;

import jp.ryo.informationPump.server.data.UserProfile;

public class DBCacheManager {
//  StoreBoxForHatebuEntry[]‚ª•Ô‚é
    public static final String ALL_HATEBU_ENTRY_CACHE = "ALL_HATEBU_ENTRY";
//  StoreBoxForHatebuEntry[]‚ª•Ô‚é
    public static final String ALL_CEEK_NEWS_ENTRY_CACHE = "ALL_CEEK_NEWS_ENTRY";
    
//  UserProfile[]‚ª•Ô‚é
    public static final String ALL_USER_PROFILE_CACHE = "ALL_USER_PROFILE";
//  StoreBoxForHatebuEntry[]‚ª•Ô‚é
    public static final String ALL_YOUTUBE_ENTRY_CACHE = "ALL_YOUTUBE_ENTRY";    
    
    
//“Ë‚Á‚Ş‚Ìprefix    
    //‚±‚ê‚ª‚Â‚¢‚Ä‚é‚Ì‚ÍUserProfile‚ª•Ô‚é
    public static final String USER_CACHE_PREFIX = "USER_";
    //‚±‚ê‚ª‚Â‚¢‚Ä‚é‚Ì‚ÍStoreBoxDocumentEntry‚ª•Ô‚é
    public static final String HATEBU_CACHE_PREFIX = "HATEBU_";
    //‚±‚ê‚ª‚Â‚¢‚Ä‚é‚Ì‚ÍStoreBoxDocumentEntry‚ª•Ô‚é
    public static final String CEEK_NEWS_CACHE_PREFIX = "CEEK_NEWS_";
//  ‚±‚ê‚ª‚Â‚¢‚Ä‚é‚Ì‚ÍStoreBoxDocumentEntry[]‚ª•Ô‚é
    public static final String YOUTUBE_CACHE_PREFIX = "YOUTUBE_";
    
    private static HashMap cache_map = new HashMap();
    
    public static boolean isCached(String key){
        return cache_map.containsKey(key);
    }
    
    public static void putCache(String key,Object obj){
        cache_map.put(key,obj);
    }
    
    public static Object getCache(String key){
        return cache_map.get(key);
    }
    
    public static void removeAllCahe(){
        cache_map.clear();
        cache_map = null;
        cache_map = new HashMap();
    }
    
    public static void removeCache(String key){
        cache_map.remove(key);
    }
}
