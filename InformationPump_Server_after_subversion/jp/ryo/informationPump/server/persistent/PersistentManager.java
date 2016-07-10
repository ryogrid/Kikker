package jp.ryo.informationPump.server.persistent;

import java.io.*;

import jp.ryo.informationPump.server.*;
import jp.ryo.informationPump.server.collab.CollaborativeInformatoinManager;
import jp.ryo.informationPump.server.crawler.*;
import jp.ryo.informationPump.server.util.CounterManager;
import jp.ryo.informationPump.server.web_server.CookieManager;

public class PersistentManager {
    private static PersistentManager manager;
    public static String DATA_FILE_PATH = "./data/";
    
    public static final String USER_DATA_PATH = DATA_FILE_PATH + "user_data/";
    public static final String USER_ADMIN_FILE = "UserAdmin.persistent";
    
    public static final String WEB_DATA_PATH =  DATA_FILE_PATH + "web_meta_data/";
    public static final String WEB_META_ADMIN_FILE = "WebMetadataAdmin.persistent";
    
    public static final String CRAWLED_DATA_PATH =  DATA_FILE_PATH + "crawled_data/";
    public static final String CRAWLED_DATA_MANAGER_FILE = "CrawledDataManager.persistent";
    
    public static final String COUNTER_DATA_PATH =  DATA_FILE_PATH + "counter_data/";
    public static final String COUNTER_DATA_MANAGER_FILE = "CounterManager.persistent";
    
    public static final String COOKIE_DATA_PATH =  DATA_FILE_PATH + "cookie_data/";
    public static final String COOKIE_DATA_FILE = "cookie_data.persistent";
    
    public static final String COLLAB_DATA_PATH =  DATA_FILE_PATH + "collab_data/";

    //COLLAB_ARRAY_DATA_FILEはWeb上で動かす場合には用いない
    public static final String COLLAB_ARRAY_DATA_FILE = "collab_array_data.persistent";
    public static final String COLLAB_MANAGER_DATA_FILE = "collab_manager_data.persistent";
    
    private PersistentManager(){
        
    }
    
    public static PersistentManager getInstance(){
        if(manager == null){
            manager = new PersistentManager();
            return manager;
        }else{
            return manager;
        }
    }
    
    public Object readObjectFromFile(String filepath){
        try {
            FileInputStream fin = new FileInputStream(new File(filepath));
            BufferedInputStream bin = new BufferedInputStream(fin);
            ObjectInputStream oin = new ObjectInputStream(bin);
            Object obj = oin.readObject();
            
            fin.close();
            bin.close();
            oin.close();
            
            return obj;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public void writeObjectToFile(String filepath,Object obj){
        try {
            FileOutputStream fout = new FileOutputStream(new File(filepath + "_tmp"));
            BufferedOutputStream bout = new BufferedOutputStream(fout);
            ObjectOutputStream oout = new ObjectOutputStream(bout);
            
            oout.writeObject(obj);
            oout.close();
            bout.close();
            fout.close();
            
            File resultFile = new File(filepath + "_tmp");
            if((resultFile != null)&& (resultFile.exists())){
                File past_file = new File(filepath);
                if(past_file.exists()){
                    past_file.delete();
                }
                resultFile.renameTo(past_file);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //ファイルから各々のインスタンスを復元する
    public void initEachObject(){
        File test_file = new File(WEB_DATA_PATH + WEB_META_ADMIN_FILE);
        if(test_file.exists()){
            System.out.println("now reading last times saved WebMetaData");
            DBWebMetadataAdmin.setInstanceFromFile((WebMetadataAdmin)readObjectFromFile(WEB_DATA_PATH + WEB_META_ADMIN_FILE));
            System.out.println("Saved data reading finished");
        }
        test_file = new File(USER_DATA_PATH + USER_ADMIN_FILE);
        if(test_file.exists()){
            System.out.println("now reading last times saved User Data");
            DBUserAdmin.setInstanceFromFile((UserAdmin)readObjectFromFile(USER_DATA_PATH + USER_ADMIN_FILE));
            System.out.println("Saved data reading finished");
        }
        test_file = new File(CRAWLED_DATA_PATH + CRAWLED_DATA_MANAGER_FILE);
        if(test_file.exists()){
            System.out.println("now reading last times saved Crawled Data");
            DBCrawledDataManager.setInstanceFromFile((CrawledDataManager)readObjectFromFile(CRAWLED_DATA_PATH + CRAWLED_DATA_MANAGER_FILE));
            System.out.println("Saved data reading finished");
        }
        test_file = new File(COUNTER_DATA_PATH + COUNTER_DATA_MANAGER_FILE);
        if(test_file.exists()){
            System.out.println("now reading last times saved Counter Data");
            CounterManager.setInstanceFromFile((CounterManager)readObjectFromFile(COUNTER_DATA_PATH + COUNTER_DATA_MANAGER_FILE));
            System.out.println("Saved data reading finished");
        }
        test_file = new File(COLLAB_DATA_PATH + COLLAB_MANAGER_DATA_FILE);
        if(test_file.exists()){
            System.out.println("now reading last times saved Collab Manager Data");
            CollaborativeInformatoinManager.setInstance((CollaborativeInformatoinManager)readObjectFromFile(COLLAB_DATA_PATH+ COLLAB_MANAGER_DATA_FILE));
            System.out.println("Saved data reading finished");
        }
        CookieManager.setEscapedDataFromFile();
    }
    
    //オブジェクトをファイルに待避する
    public void escapeObjectsToFile(){
        synchronized (DBWebMetadataAdmin.getInstance()) {
            writeObjectToFile(WEB_DATA_PATH + WEB_META_ADMIN_FILE,DBWebMetadataAdmin.getInstance());
        }
        synchronized (DBUserAdmin.getInstance()) {
            writeObjectToFile(USER_DATA_PATH + USER_ADMIN_FILE,DBUserAdmin.getInstance());
        }
        synchronized (DBCrawledDataManager.getInstance()) {
            writeObjectToFile(CRAWLED_DATA_PATH + CRAWLED_DATA_MANAGER_FILE,DBCrawledDataManager.getInstance());
        }
        synchronized (CounterManager.getInstance()) {
            writeObjectToFile(COUNTER_DATA_PATH + COUNTER_DATA_MANAGER_FILE,CounterManager.getInstance());
        }
        CookieManager.escapeObjectToFile();
    }
}
