package jp.ryo.informationPump.server;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import jp.ryo.informationPump.server.data.*;
import jp.ryo.informationPump.server.exchange_server.cul.SimilarityCalculater;
import jp.ryo.informationPump.server.persistent.PersistentLinkedHashMap;
import jp.ryo.informationPump.server.persistent.PersistentManager;
import jp.ryo.informationPump.server.util.Util;

//�������̗\��
public class DBWebMetadataAdmin implements WebMetadataAdmin{
    private static final long serialVersionUID = 1L;
    
    private static WebMetadataAdmin wadmin;
    private PersistentLinkedHashMap web_data; //key=�A�h���X(String), value=StoreBoxForWebData
    private PersistentLinkedHashMap each_keywords;  //key=�L�[���[�h(String) value=�L�[���[�h�ɑΉ������y�[�W������ArrayList
    
    public static WebMetadataAdmin getInstance(){
        if(wadmin==null){
            wadmin = new DBWebMetadataAdmin();
        }
        return wadmin;
    }
    
    private DBWebMetadataAdmin(){
        web_data = new PersistentLinkedHashMap(1000,PersistentManager.WEB_DATA_PATH + "web_data.tmp");
        each_keywords = new PersistentLinkedHashMap(1000,PersistentManager.WEB_DATA_PATH + "each_keywords.tmp");
    }
    
    public synchronized void addNewPage(WebData data){
        try {
            Object obj = web_data.get(data.getAddress());
            
            if(obj!=null){
                int now_count = (((StoreBoxForWebData)obj).data).getReadCount();
                data.setReadCount(now_count + 1);
            }
            
            //�y�[�W���{�ɓo�^
            if(obj!=null){
                web_data.put(data.getAddress(),new StoreBoxForWebData(data,((StoreBoxForWebData)obj).date));
            }else{
                web_data.put(data.getAddress(),new StoreBoxForWebData(data,new Date()));    
            }
            
            
            //�L�[���[�h���Ƃ̃f�[�^���X�V
            HashMap map = data.getKeyword_vector();
            Set value_set = map.keySet();
            Iterator value_itr = value_set.iterator();
            while(value_itr.hasNext()){
                String keyword = (String)value_itr.next();
                ArrayList key_datas = getKeywordList(keyword);
                
                if(key_datas!=null){
                    key_datas.add(new StoreBoxForWebData(data,new Date()));    
                }else{
                    ArrayList list = new ArrayList();
                    list.add(new StoreBoxForWebData(data,new Date()));
                    each_keywords.put(keyword,list);
                }
            }
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //���ꂼ��̃L�[���[�h�ɑ�����y�[�W��ێ����Ă���ArrayList��Ԃ�
    private synchronized ArrayList getKeywordList(String key){
        return (ArrayList)each_keywords.get(key);
    }
    
    //�^�����L�[���[�h�ɑ���,hour���ԑO���猻�݂̊Ԃɒǉ����ꂽ���̂�Ԃ�
    public synchronized StoreBoxForWebData[] getTagetKeywordBelongs(String keyword,int hour){
        ArrayList key_master = getKeywordList(keyword);
        
        if(key_master==null){
            return new StoreBoxForWebData[0];
        }
        
        int len = key_master.size();
        
        //�o�C�i���T�[�`������hour���ԑO�Ƃ̋���������
        int head_index =0;
        int tail_index = len;
        int now_index = len/2;
        Date now = new Date();
        
        int find_index=0;
        while(true){
            StoreBoxForWebData tmpBox = (StoreBoxForWebData)key_master.get(now_index);
            
            if((tmpBox.date.getTime()) == (now.getTime()-(3600*hour))){
                find_index = now_index;
                break;
            }else if((tmpBox.date.getTime()) > (now.getTime()-(3600*hour))){
//                ���s�����Ώۂ��w��̎��Ԃ��O�Ȃ��
                int tmpIndex = now_index;
                now_index = (tail_index - now_index)/2;
                head_index = tmpIndex;
            }else{//���s�����Ώۂ��w��̎��Ԃ���Ȃ��
                int tmpIndex = now_index;
                now_index = (now_index -head_index)/2;
                tail_index = tmpIndex;
            }
            
            if(head_index == (tail_index-1)){
                find_index = tail_index;
                break;
            }
        }
        
        StoreBoxForWebData results[] = new StoreBoxForWebData[len-find_index];
        for(int i=find_index;i<len;i++){
            results[i] = (StoreBoxForWebData)key_master.get(i);
        }
        
        return results;
    }
    
    //�^�����L�[���[�h�ɑ�����h�L�������g�𓾂�
    public synchronized StoreBoxForWebData[] getTagetKeywordBelongs(String keyword){
        ArrayList key_master = getKeywordList(keyword);
        
        if(key_master==null){
            return new StoreBoxForWebData[0];
        }
        
        int len = key_master.size();
        
        StoreBoxForWebData results[] = new StoreBoxForWebData[len];
        for(int i=0;i<len;i++){
            results[i] = (StoreBoxForWebData)key_master.get(i);
        }
        
        return results;
    }
    
    public synchronized StoreBoxForWebData getPage(String name){
        Object obj = web_data.get(name);
        if(obj!=null){
            return (StoreBoxForWebData)obj; 
        }
        return null;
    }
    
//  ����̃x�N�^�[��n���Ď��Ă���h�L�������g�̔z��Ɨގ��x��������SearchResult�𓾂�
    public synchronized SearchResult search(HashMap taste_vector){
        ArrayList tmpListForSort = new ArrayList();//�e�v�f��SortBoxForWebData
        
        Iterator itr = web_data.iterator();
        while(itr.hasNext()){
            StoreBoxForWebData box = (StoreBoxForWebData)itr.next();
            
            WebData data = box.data;
            
            double simirarity = SimilarityCalculater.calculateSimilarity(data.getKeyword_vector(),taste_vector);
            if(simirarity >= SimilarityCalculater.SIMIRARITY_THRESHOLD){
                tmpListForSort.add(new SortBoxForWebData(box,new Double(String.valueOf(simirarity))));
            }
        }
        
        //�\�[�g
        ArrayList sorted_list = Util.getSortedList(tmpListForSort);
        
        //�z��֎��o��
        StoreBoxForWebData results[] = new StoreBoxForWebData[sorted_list.size()];
        double dbl_simirarities[] = new double[sorted_list.size()];
        for(int i=0;i<results.length;i++){
          results[i] = ((SortBoxForWebData)sorted_list.get(i)).webdata;
          dbl_simirarities[i] = ((SortBoxForWebData)sorted_list.get(i)).simirality.doubleValue();
        }
            
        SearchResult result = new SearchResult(results,dbl_simirarities);
        return result;
    }
    
    //�t�@�C������f�V���A���C�Y���ē����C���X�^���X���Z�b�g����
    public synchronized static void setInstanceFromFile(WebMetadataAdmin admin){
        DBWebMetadataAdmin.wadmin = admin;
    }
}