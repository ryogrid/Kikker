package jp.ryo.informationPump.server.crawler;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import jp.ryo.informationPump.server.data.*;
import jp.ryo.informationPump.server.exchange_server.cul.SimilarityCalculater;
import jp.ryo.informationPump.server.persistent.PersistentLinkedHashMap;
import jp.ryo.informationPump.server.persistent.PersistentManager;
import jp.ryo.informationPump.server.util.UserViewCountComp;
import jp.ryo.informationPump.server.util.Util;

public class FileCrawledDataManager implements CrawledDataManager {
    private static final long serialVersionUID = 1L;
    private static CrawledDataManager instance;
    private final int REMOVE_HOURS = 24;
    
    private PersistentLinkedHashMap hatebuCrawledData;     //key=url(String),value=StoreBoxForHatebuEntry
    private PersistentLinkedHashMap hatebuEachKeywordEntries;   //key=�L�[���[�h(String) value=�L�[���[�h�ɑΉ������y�[�W��url������ArrayList
                                                                 //(���̂�hatebuCrawledData��������Ă��邱��)
    
    private FileCrawledDataManager(){ 
        hatebuCrawledData = new PersistentLinkedHashMap(1000,PersistentManager.CRAWLED_DATA_PATH + "hatebuCrawledData.tmp");
        hatebuEachKeywordEntries = new PersistentLinkedHashMap(1000,PersistentManager.CRAWLED_DATA_PATH + "hatebuEachKeywordEntries.tmp");
    }
    
    public static CrawledDataManager getInstance(){
        if(instance==null){
            instance = new FileCrawledDataManager();
        }
        return instance;
    }
    
    public static void setInstanceFromFile(CrawledDataManager manager){
        instance = manager;
    }
    
    public synchronized void margeClawledData(HashMap data,int doc_type,boolean isAsAnalyzed){
        try {
            Set tmp_set = data.entrySet();
            Iterator itr = tmp_set.iterator();
            while(itr.hasNext()){
                Map.Entry entry = (Map.Entry)itr.next();
                
                String key = (String)entry.getKey();
                DocumentEntry value = (DocumentEntry)entry.getValue();
                
                if(hatebuCrawledData.get(key)==null){
                    try {
                        hatebuCrawledData.put(key,new StoreBoxForDocumentEntry(value,new Date()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    
//              �L�[���[�h���Ƃ̃f�[�^���X�V
                    HashMap map = value.getKeywords();
                    Set value_set = map.keySet();
                    Iterator value_itr = value_set.iterator();
                    while(value_itr.hasNext()){
                        String keyword = (String)value_itr.next();
                        ArrayList key_datas = getKeywordList(keyword,doc_type);
                        
                        if(key_datas!=null){
                            key_datas.add(value.getAddress());
                        }else{
                            ArrayList list = new ArrayList();
//                            list.add(new StoreBoxForHatebuEntry(value,new Date()));
                            list.add(value.getAddress());
                            hatebuEachKeywordEntries.put(keyword,list);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
//  ���ꂼ��̃L�[���[�h�ɑ�����y�[�W��ێ����Ă���ArrayList��Ԃ�
    private synchronized ArrayList getKeywordList(String key,int doc_type){
        return (ArrayList)hatebuEachKeywordEntries.get(key);
    }
    
//  ����̃x�N�^�[��n���Ď��Ă���h�L�������g�̔z��Ɨގ��x��������SearchResult�𓾂�
    public synchronized DocumentSearchResult search(HashMap taste_vector,int doc_type){
        ArrayList tmpListForSort = new ArrayList();//�e�v�f��SortBoxForWebData
        
        Iterator itr = hatebuCrawledData.iterator();
        while(itr.hasNext()){
            StoreBoxForDocumentEntry data = (StoreBoxForDocumentEntry)itr.next();
            if(data.data.isPreciseAnalyzed()){
                HashMap marged_data_taste = data.data.getKeywords();
                
                //�L�[���[�h�ƃ^�O���}�[�W
                if(data.data.getTags()!=null){
                    marged_data_taste.putAll(data.data.getTags());    
                }
                
                double simirarity = SimilarityCalculater.calculateSimilarity(marged_data_taste,taste_vector);
                if(simirarity >= SimilarityCalculater.HATEBU_THRESHOLD){
                    tmpListForSort.add(new SortBoxForDocumentEntry(data.data,new Double(String.valueOf(simirarity))));
                }    
            }
        }
        
        //�\�[�g
        ArrayList sorted_list = Util.getBiasSortedList(tmpListForSort);
        
        //�z��֎��o��
        DocumentEntry[] entries = new DocumentEntry[sorted_list.size()];
        double dbl_simirarities[] = new double[sorted_list.size()];
        double eval_points[] = new double[sorted_list.size()];
        for(int i=0;i<entries.length;i++){
          entries[i] = ((SortBoxForDocumentEntry)sorted_list.get(i)).entry;
          dbl_simirarities[i] = ((SortBoxForDocumentEntry)sorted_list.get(i)).simirality.doubleValue();
          eval_points[i] = calEvalPoint(dbl_simirarities[i],entries[i].getView_users());
        }
            
        DocumentSearchResult result = new DocumentSearchResult(entries,dbl_simirarities,eval_points);
        return result;
    }
    
    private double calEvalPoint(double simirality, int view_count) {
        return simirality*Math.log(view_count+1);
    }

    //�^�����L�[���[�h�ɑ���,hour���ԑO���猻�݂̊Ԃɒǉ����ꂽ���̂�Ԃ�
    public synchronized StoreBoxForDocumentEntry[] getTagetKeywordBelongs(String keyword,int hour,int doc_type){
        ArrayList key_master = getKeywordList(keyword,doc_type);
        
        if(key_master==null){
            return new StoreBoxForDocumentEntry[0];
        }
        key_master = refToObj(key_master,doc_type);
        
        int len = key_master.size();
        
        //�o�C�i���T�[�`������hour���ԑO�Ƃ̋���������
        int head_index =0;
        int tail_index = len;
        int now_index = len/2;
        Date now = new Date();
        
        int find_index=0;
        while(true){
            StoreBoxForDocumentEntry tmpBox = (StoreBoxForDocumentEntry)key_master.get(now_index);
            
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
        
        StoreBoxForDocumentEntry results[] = new StoreBoxForDocumentEntry[len-find_index];
        for(int i=find_index;i<len;i++){
            results[i] = (StoreBoxForDocumentEntry)key_master.get(i);
        }
        
        List toSortArr = Arrays.asList(results);
        Collections.sort(toSortArr,new UserViewCountComp());
        int len_ = toSortArr.size();
        for(int i=0;i<len_;i++){
            results[i] = (StoreBoxForDocumentEntry)toSortArr.get(i);
        }
        
        return results;
    }
    
    //�^�����L�[���[�h�ɑ�����h�L�������g�𓾂�
    public synchronized StoreBoxForDocumentEntry[] getTagetKeywordBelongs(String keyword,int doc_type){
        ArrayList key_master = getKeywordList(keyword,doc_type);
        
        if(key_master==null){
            return new StoreBoxForDocumentEntry[0];
        }
        
        key_master = refToObj(key_master,doc_type);//url������Ԃ֕ϊ�;
        
        Collections.sort(key_master,new UserViewCountComp());
        int len = key_master.size();
        
        StoreBoxForDocumentEntry results[] = new StoreBoxForDocumentEntry[len];
        for(int i=0;i<len;i++){
            results[i] = (StoreBoxForDocumentEntry)key_master.get(i);
        }
        
        return results;
    }

    //url���Q�ƂƂ��Ď����Ă���ArrayList�̗v�f�����̂ɕϊ�����
    private synchronized ArrayList refToObj(ArrayList urls,int doc_type) {
        ArrayList result = new ArrayList();
        int len = urls.size();
        for(int i=0;i<len;i++){
            result.add(hatebuCrawledData.get(urls.get(i)));
        }
        
        return result;
    }
    
    public synchronized void setAnalyzedResults(String target_url,HashMap newVecMap,ArrayList sortedKeywords,int doc_type){
        Object already_exist = hatebuCrawledData.get(target_url);
        if(already_exist!=null){
            StoreBoxForDocumentEntry alreadyEntry = (StoreBoxForDocumentEntry)already_exist;
            alreadyEntry.data.setKeywords(newVecMap);
            alreadyEntry.data.setSortedKeywords(sortedKeywords);
            alreadyEntry.data.setIsPreciseAnalyzed(true);
        }else{
            return;
        }
    }
    
    //call�������_����莞�Ԉȏ�O�̃G���g�����폜����
    public synchronized void removeOldEntries(int doc_type){
        Iterator toCeekEntriesItr = hatebuCrawledData.iterator();
        ArrayList shouldRemove = new ArrayList();
        Date date = new Date();
        long now_time = date.getTime();
        //��菜�����̂𒊏o
        while(toCeekEntriesItr.hasNext()){
            StoreBoxForDocumentEntry entry = (StoreBoxForDocumentEntry)toCeekEntriesItr.next();
            if((now_time - entry.data.getClawledDate().getTime())>(3600000*REMOVE_HOURS)){
                shouldRemove.add(entry);
            }
        }
        
        //���ۂ̍폜�Ɗ֘A����L�[���[�h�̎Q�Ƃ̍폜���s��
        int len = shouldRemove.size();
        for(int i=0;i<len;i++){
            //�L�[���[�h�ƃ^�O���}�[�W
            StoreBoxForDocumentEntry target = (StoreBoxForDocumentEntry)shouldRemove.get(i);
            HashMap keyword = target.data.getKeywords();
            String target_url = target.data.getAddress();
            if(target.data.getTags()!=null){
                keyword.putAll(target.data.getTags());
            }
            
            //�L�[���[�h���Ƃɍ폜
            Set entry_set = keyword.entrySet();
            Iterator entry_itr = entry_set.iterator();
            while(entry_itr.hasNext()){
                Map.Entry each_entry = (Map.Entry)entry_itr.next();
                String keyword_str = (String)each_entry.getKey();
                ArrayList list = (ArrayList)hatebuEachKeywordEntries.get(keyword_str);
                ArrayList moved = new ArrayList();
                int list_len = list.size();
                for(int j=0;j<list_len;j++){
                    String url = (String)list.get(j);
                    if(!target_url.equals(url)){
                        moved.add(url);
                    }
                }
                //�ړ��������̂ɍX�V
                try {
                    hatebuEachKeywordEntries.put(keyword_str,moved);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
    }

    public void updateBookmarkCounts(int doc_type) {
    }

    public StoreBoxForDocumentEntry getEntryWithAddress(String address,int doc_type) {
        return null;
    }

    public DocumentSearchResult searchNews(HashMap taste_vector,String category, StoreBoxForDocumentEntry[] entries) {
        return null;
    }

    public StoreBoxForDocumentEntry[] getAllEntry(int doc_type) {
        return null;
    }
}