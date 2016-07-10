package jp.ryo.informationPump.server.learn;

import java.util.*;

import jp.ryo.informationPump.server.DBUserAdmin;
import jp.ryo.informationPump.server.UserAdmin;
import jp.ryo.informationPump.server.crawler.CrawledDataManager;
import jp.ryo.informationPump.server.crawler.DBCrawledDataManager;
import jp.ryo.informationPump.server.data.StoreBoxForDocumentEntry;
import jp.ryo.informationPump.server.data.UserProfile;
import jp.ryo.informationPump.server.util.DBUtil;

public class UserTasteUpdater extends Thread {
    private String user_id;
    private String doc_address;
    private int doc_type;

    public UserTasteUpdater(String user_id, String doc_address,int doc_type) {
        super();
        this.user_id = user_id;
        this.doc_address = doc_address;
        this.doc_type = doc_type;
    }

    public void run() {
        try {
            this.setPriority(Thread.MIN_PRIORITY);
            
            UserAdmin u_manager = DBUserAdmin.getInstance();
            UserProfile profile = u_manager.getUser(user_id);
            HashMap user_vec = profile.getTasteVector();
            
            CrawledDataManager c_manager = DBCrawledDataManager.getInstance();
            StoreBoxForDocumentEntry entry = c_manager.getEntryWithAddress(doc_address,doc_type);
            
            HashMap page_vec = entry.data.getKeywords();
            Set page_entry_set = page_vec.entrySet();
            Iterator page_itr = page_entry_set.iterator();
            while(page_itr.hasNext()){
                Map.Entry tmp_entry = (Map.Entry)page_itr.next();
                String keyword = (String)tmp_entry.getKey();
                Double doc_tfidf = (Double)tmp_entry.getValue();
                
                //対応するキーワードがあれば
                if(user_vec.get(keyword)!=null){
                    Double user_tfidf = (Double)user_vec.get(keyword);
                    Double new_tfidf = new Double(user_tfidf.doubleValue() + doc_tfidf.doubleValue());
                    user_vec.put(keyword,new_tfidf);
                }else{//なければ
                    user_vec.put(keyword,doc_tfidf);
                }
            }
            
            //ベクトルをDBに反映
            u_manager.updateTasete(user_id,user_vec);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
