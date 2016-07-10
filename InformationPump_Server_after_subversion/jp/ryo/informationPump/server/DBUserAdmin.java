package jp.ryo.informationPump.server;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import jp.ryo.informationPump.server.data.*;
import jp.ryo.informationPump.server.db.abstractor.UserDBAbstractor;
import jp.ryo.informationPump.server.exchange_server.cul.SimilarityCalculater;
import jp.ryo.informationPump.server.persistent.PersistentManager;
import jp.ryo.informationPump.server.persistent.PersistentVector;
import jp.ryo.informationPump.server.util.CounterManager;
import jp.ryo.informationPump.server.util.Util;

public class DBUserAdmin implements UserAdmin{
    private static final long serialVersionUID = 1L;
    
    private static UserAdmin admin;
    private static UserDBAbstractor u_abstractor;
            
    private DBUserAdmin(){
        u_abstractor = UserDBAbstractor.getInstace();
    }
    
    public static UserAdmin getInstance(){
        if(admin==null){
            admin = new DBUserAdmin();
            return admin;
        }else{
            return admin;
        }
    }
    
    public UserProfile getUser(String id){

        return u_abstractor.getUser(id);
    }
    
    public UserProfile[] getAllUser(){
        return u_abstractor.getAllUser();
    }
    
    //返り値がtrueだと成功、そうじゃない場合は失敗
    public boolean createNewUser(UserProfile profile){
        Object obj = u_abstractor.getUser(profile.getId());
        if(obj==null){
            u_abstractor.addNewUser(profile);
            return true;
        }else{
            return false;
        }
    }
    
    public static void setInstanceFromFile(UserAdmin admin){
        DBUserAdmin.admin = admin;
        u_abstractor = UserDBAbstractor.getInstace();
    }
    
    public void addReadPage(String user_name,String page_name){
        UserProfile profile = getUser(user_name);
        profile.addPage(page_name);
        u_abstractor.updateUser(profile);
    }
    
    public void addReadPages(String user_name,String page_names[]){
        UserProfile profile = getUser(user_name);
        for(int i=0;i<page_names.length;i++){
            profile.addPage(page_names[i]);    
        }
    }
    
    public void updateTasete(String user_name,HashMap taste_vector){
        UserProfile profile = getUser(user_name);
        profile.updateTaste(taste_vector);
        u_abstractor.updateUser(profile);
    }
    
//  趣向のベクターを渡して似ているドキュメントの配列と類似度を持ったUserSearchResultを得る
    public UserSearchResult search(HashMap taste_vector){
        ArrayList tmpListForSort = new ArrayList();//各要素はSortBoxForWebData
        
        UserProfile profiles[] = u_abstractor.getAllUser();
        int len = profiles.length;
        for(int i=0;i<len;i++){
            UserProfile user = profiles[i];
            
            double simirarity = SimilarityCalculater.calculateSimilarity(user.getTasteVector(),taste_vector);
            if(simirarity >= SimilarityCalculater.USER_THRESHOLD){
                tmpListForSort.add(new SortBoxForUserProfile(user,new Double(String.valueOf(simirarity))));
            }
        }
        
        //ソート
        ArrayList sorted_list = Util.getSortedList(tmpListForSort);
        
        //配列へ取り出す
        UserProfile results[] = new UserProfile[sorted_list.size()];
        double dbl_simirarities[] = new double[sorted_list.size()];
        for(int i=0;i<results.length;i++){
          results[i] = ((SortBoxForUserProfile)sorted_list.get(i)).profile;
          dbl_simirarities[i] = ((SortBoxForUserProfile)sorted_list.get(i)).simirality.doubleValue();
        }
            
        UserSearchResult result = new UserSearchResult(results,dbl_simirarities);
        return result;
    }
}
