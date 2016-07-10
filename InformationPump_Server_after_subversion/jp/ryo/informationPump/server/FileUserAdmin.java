package jp.ryo.informationPump.server;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import jp.ryo.informationPump.server.data.*;
import jp.ryo.informationPump.server.exchange_server.cul.SimilarityCalculater;
import jp.ryo.informationPump.server.persistent.PersistentManager;
import jp.ryo.informationPump.server.persistent.PersistentVector;
import jp.ryo.informationPump.server.util.Util;

public class FileUserAdmin implements UserAdmin{
    private static final long serialVersionUID = 1L;
    
    private static UserAdmin admin;
    private PersistentVector users;
    
    private FileUserAdmin(){
        users = new PersistentVector(1000,PersistentManager.USER_DATA_PATH + "users.tmp"); 
    }
    
    public synchronized static UserAdmin getInstance(){
        if(admin==null){
            admin = new FileUserAdmin();
            return admin;
        }else{
            return admin;
        }
    }
    
    public synchronized UserProfile getUser(String id){
        int len = users.size();
        for(int i=0;i<len;i++){
            UserProfile prof = (UserProfile)users.get(i);
            if(id.equals(prof.getId())){
                return prof;
            }
        }
        return null;
    }
    
    public synchronized UserProfile[] getAllUser(){
        int len = users.size();
        UserProfile profiles[] = new UserProfile[len];
        for(int i=0;i<len;i++){
            UserProfile prof = (UserProfile)users.get(i);
            profiles[i] = prof;
        }
        return profiles;
    }
    
    //�Ԃ�l��true���Ɛ����A��������Ȃ��ꍇ�͎��s
    public synchronized boolean createNewUser(UserProfile profile){
        int len = users.size();
        for(int i=0;i<len;i++){
            UserProfile prof = (UserProfile)users.get(i);
            if(profile.getId().equals(prof.getId())){
                return false;
            }
        }
        
        try {
            users.add(profile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return true;
    }
    
    public static synchronized void setInstanceFromFile(UserAdmin admin){
        FileUserAdmin.admin = admin;
    }
    
    public synchronized void addReadPage(String user_name,String page_name){
        UserProfile profile = getUser(user_name);
        profile.addPage(page_name);
    }
    
    public synchronized void addReadPages(String user_name,String page_names[]){
        UserProfile profile = getUser(user_name);
        for(int i=0;i<page_names.length;i++){
            profile.addPage(page_names[i]);    
        }
    }
    
    public synchronized void updateTasete(String user_name,HashMap taste_vector){
        UserProfile profile = getUser(user_name);
        profile.updateTaste(taste_vector);
    }
    
//  ����̃x�N�^�[��n���Ď��Ă���h�L�������g�̔z��Ɨގ��x��������SearchResult�𓾂�
    public synchronized UserSearchResult search(HashMap taste_vector){
        ArrayList tmpListForSort = new ArrayList();//�e�v�f��SortBoxForWebData
        
        Iterator itr = users.iterator();
        while(itr.hasNext()){
            UserProfile user = (UserProfile)itr.next();
            
            double simirarity = SimilarityCalculater.calculateSimilarity(user.getTasteVector(),taste_vector);
            if(simirarity >= SimilarityCalculater.USER_THRESHOLD){
                tmpListForSort.add(new SortBoxForUserProfile(user,new Double(String.valueOf(simirarity))));
            }
        }
        
        //�\�[�g
        ArrayList sorted_list = Util.getSortedList(tmpListForSort);
        
        //�z��֎��o��
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
