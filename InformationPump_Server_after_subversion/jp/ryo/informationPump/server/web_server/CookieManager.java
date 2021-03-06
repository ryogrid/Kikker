package jp.ryo.informationPump.server.web_server;

import java.io.File;
import java.util.Date;
import java.util.HashMap;

import jp.ryo.informationPump.server.data.CookieEntry;
import jp.ryo.informationPump.server.persistent.PersistentManager;
import jp.ryo.informationPump.server.util.CounterManager;
import jp.ryo.informationPump.server.util.Util;

public class CookieManager {
    public static final int COOKIE_EXPIRE_MINUTES = 60*24*7;//とりあえず一週間
    public static final String COOKIE_KEY_NAME = "kikker_cookie";
    public static final String COOKIE_APPLY_PATH = "/";
    private static HashMap logined_members = new HashMap();;

    private CookieManager(){
    }
    
    public static String login(String id){
        String cookie_str = Util.generateHash(id);
        logined_members.put(cookie_str,new CookieEntry(id,new Date()));
        return cookie_str;
    }
    
    public static void logout(String cookie_str){
        logined_members.remove(cookie_str);
    }
    
    public static boolean isLogined(String cookie_str){
        if(!logined_members.containsKey(cookie_str)){
            return false;
        }else{
            Date now_date = new Date();
            CookieEntry entry = (CookieEntry)logined_members.get(cookie_str);
            Date logined_date = entry.logined_date;
            //Expireしてたら
            if((logined_date.getTime() + 60000*COOKIE_EXPIRE_MINUTES)< now_date.getTime()){
                logined_members.remove(cookie_str);
                return false;
            }else{
                return true;
            }
        }
    }
    
    //ログインしている事を確認してから呼ぶ事
    public static String getIDByCookie(String cookie_value){
        CookieEntry entry = (CookieEntry)logined_members.get(cookie_value);
        return entry.id;
    }

    public static void escapeObjectToFile() {
        PersistentManager p_manager = PersistentManager.getInstance();
        p_manager.writeObjectToFile(PersistentManager.COOKIE_DATA_PATH + PersistentManager.COOKIE_DATA_FILE,logined_members);
    }
    
    public static void setEscapedDataFromFile(){
        File test_file = new File(PersistentManager.COOKIE_DATA_PATH + PersistentManager.COOKIE_DATA_FILE);
        PersistentManager p_manager = PersistentManager.getInstance();
        
        if(test_file.exists()){
            System.out.println("now reading last times saved Cookie Data");
            logined_members =  ((HashMap)p_manager.readObjectFromFile(PersistentManager.COOKIE_DATA_PATH + PersistentManager.COOKIE_DATA_FILE));
            System.out.println("Saved data reading finished");
        }
    }
}
