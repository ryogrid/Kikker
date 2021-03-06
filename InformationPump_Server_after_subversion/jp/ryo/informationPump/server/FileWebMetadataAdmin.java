package jp.ryo.informationPump.server;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import jp.ryo.informationPump.server.data.*;
import jp.ryo.informationPump.server.exchange_server.cul.SimilarityCalculater;
import jp.ryo.informationPump.server.persistent.PersistentLinkedHashMap;
import jp.ryo.informationPump.server.persistent.PersistentManager;
import jp.ryo.informationPump.server.util.Util;

public class FileWebMetadataAdmin implements WebMetadataAdmin{
    private static final long serialVersionUID = 1L;
    
    private static WebMetadataAdmin wadmin;
    private PersistentLinkedHashMap web_data; //key=アドレス(String), value=StoreBoxForWebData
    private PersistentLinkedHashMap each_keywords;  //key=キーワード(String) value=キーワードに対応したページを持つArrayList
    
    public static WebMetadataAdmin getInstance(){
        if(wadmin==null){
            wadmin = new FileWebMetadataAdmin();
        }
        return wadmin;
    }
    
    private FileWebMetadataAdmin(){
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
            
            //ページを大本に登録
            if(obj!=null){
                web_data.put(data.getAddress(),new StoreBoxForWebData(data,((StoreBoxForWebData)obj).date));
            }else{
                web_data.put(data.getAddress(),new StoreBoxForWebData(data,new Date()));    
            }
            
            
            //キーワードごとのデータを更新
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
    
    //それぞれのキーワードに属するページを保持しているArrayListを返す
    private synchronized ArrayList getKeywordList(String key){
        return (ArrayList)each_keywords.get(key);
    }
    
    //与えたキーワードに属し,hour時間前から現在の間に追加されたものを返す
    public synchronized StoreBoxForWebData[] getTagetKeywordBelongs(String keyword,int hour){
        ArrayList key_master = getKeywordList(keyword);
        
        if(key_master==null){
            return new StoreBoxForWebData[0];
        }
        
        int len = key_master.size();
        
        //バイナリサーチをしてhour時間前との境を見つける
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
//                試行した対象が指定の時間より前ならば
                int tmpIndex = now_index;
                now_index = (tail_index - now_index)/2;
                head_index = tmpIndex;
            }else{//試行した対象が指定の時間より後ならば
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
    
    //与えたキーワードに属するドキュメントを得る
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
    
//  趣向のベクターを渡して似ているドキュメントの配列と類似度を持ったSearchResultを得る
    public synchronized SearchResult search(HashMap taste_vector){
        ArrayList tmpListForSort = new ArrayList();//各要素はSortBoxForWebData
        
        Iterator itr = web_data.iterator();
        while(itr.hasNext()){
            StoreBoxForWebData box = (StoreBoxForWebData)itr.next();
            
            WebData data = box.data;
            
            double simirarity = SimilarityCalculater.calculateSimilarity(data.getKeyword_vector(),taste_vector);
            if(simirarity >= SimilarityCalculater.SIMIRARITY_THRESHOLD){
                tmpListForSort.add(new SortBoxForWebData(box,new Double(String.valueOf(simirarity))));
            }
        }
        
        //ソート
        ArrayList sorted_list = Util.getSortedList(tmpListForSort);
        
        //配列へ取り出す
        StoreBoxForWebData results[] = new StoreBoxForWebData[sorted_list.size()];
        double dbl_simirarities[] = new double[sorted_list.size()];
        for(int i=0;i<results.length;i++){
          results[i] = ((SortBoxForWebData)sorted_list.get(i)).webdata;
          dbl_simirarities[i] = ((SortBoxForWebData)sorted_list.get(i)).simirality.doubleValue();
        }
            
        SearchResult result = new SearchResult(results,dbl_simirarities);
        return result;
    }
    
    //ファイルからデシリアライズして得たインスタンスをセットする
    public synchronized static void setInstanceFromFile(WebMetadataAdmin admin){
        FileWebMetadataAdmin.wadmin = admin;
    }
}