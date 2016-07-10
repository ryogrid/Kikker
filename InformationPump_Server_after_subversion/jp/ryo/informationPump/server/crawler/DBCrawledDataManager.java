package jp.ryo.informationPump.server.crawler;


import java.util.*;

import jp.ryo.informationPump.server.data.*;
import jp.ryo.informationPump.server.db.abstractor.CrawledDataDBAbstractor;
import jp.ryo.informationPump.server.db.abstractor.InvertedMap;
import jp.ryo.informationPump.server.exchange_server.cul.SimilarityCalculater;
import jp.ryo.informationPump.server.helper.CeekNewsHelper;
import jp.ryo.informationPump.server.helper.HatebuHelper;
import jp.ryo.informationPump.server.util.*;

public class DBCrawledDataManager implements CrawledDataManager {
    private static final long serialVersionUID = 1L;
    private static CrawledDataManager instance;
    private final int REMOVE_HOURS = 24;
    private final int QUERY_LIMIT_WHEN_USE_INVERSE = 50; //転置ファイルを使った時のクエリーの上限
    
//    private PersistentLinkedHashMap hatebuCrawledData;     //key=url(String),value=StoreBoxForHatebuEntry
//    private PersistentLinkedHashMap hatebuEachKeywordEntries;   //key=キーワード(String) value=キーワードに対応したページのurlを持つArrayList
                                                                 //(実体はhatebuCrawledDataから引いてくること)
    private static CrawledDataDBAbstractor h_abstractor;
    
    private DBCrawledDataManager(){ 
        h_abstractor = CrawledDataDBAbstractor.getInstance();
    }
    
    public static CrawledDataManager getInstance(){
        if(instance==null){
            instance = new DBCrawledDataManager();
        }
        return instance;
    }
    
    public static void setInstanceFromFile(CrawledDataManager manager){
        instance = manager;
        h_abstractor = CrawledDataDBAbstractor.getInstance();
    }
    
    public void margeClawledData(HashMap data,int doc_type,boolean isAsAnalyzed){
        Set tmp_set = data.entrySet();
        Iterator itr = tmp_set.iterator();
        while(itr.hasNext()){
            Map.Entry entry = (Map.Entry)itr.next();
            
            String key = (String)entry.getKey();
            DocumentEntry hatebu_value = (DocumentEntry)entry.getValue();
            
            if(h_abstractor.getEntryWithURL(key,doc_type)==null){
                StoreBoxForDocumentEntry newEntry = new StoreBoxForDocumentEntry(hatebu_value,new Date());
                newEntry.data.setIsPreciseAnalyzed(isAsAnalyzed);
                h_abstractor.updateNewEntry(newEntry);
            }    
        }
    }
    
//  それぞれのキーワードに属するページを保持しているArrayListを返す
    private ArrayList getKeywordList(String key,int doc_type){
        StoreBoxForDocumentEntry[] entries = getTagetKeywordBelongs(key,doc_type);
        int len = entries.length;
        ArrayList result_vec = new ArrayList();
        for(int i=0;i<len;i++){
            result_vec.add(entries[i]);
        }
        return result_vec;
    }
    
    public ArrayList getSimilarEntriesByAllCeek(HashMap taste_vector,int doc_type){
        ArrayList tmpListForSort = new ArrayList();//各要素はSortBoxForWebData
        
        long start = System.currentTimeMillis();
        StoreBoxForDocumentEntry all_entries[] = h_abstractor.getAllEntries(doc_type);
        System.out.println("elapsed " + (System.currentTimeMillis()-start) + " msec for getting all Entry when search.");
        
        start = System.currentTimeMillis();
        int len = all_entries.length;
        for(int i=0;i<len;i++){
            StoreBoxForDocumentEntry data = all_entries[i];
            
            if(data.data.isPreciseAnalyzed()){
                HashMap marged_data_taste = data.data.getKeywords();
                
                //キーワードとタグをマージ
                if(data.data.getTags()!=null){
                    marged_data_taste.putAll(data.data.getTags());    
                }
                
                double simirarity = SimilarityCalculater.calculateSimilarity(marged_data_taste,taste_vector);
                if(simirarity >= SimilarityCalculater.HATEBU_THRESHOLD){
                    tmpListForSort.add(new SortBoxForDocumentEntry(data.data,new Double(String.valueOf(simirarity))));
                }    
            }
        }

        //ソート
        ArrayList sorted_list = Util.getBiasSortedList(tmpListForSort);
        
        System.out.println("elapsed " + (System.currentTimeMillis()-start) + " msec for calculating simirality when search.");
        return sorted_list;
    }
    
    public ArrayList getSimilarEntriesUsingInvertedMap(HashMap taste_vector,int doc_type){
        if(InvertedMap.isCanUse()){
            System.out.println("Search with InvertedMap!!");
            Iterator entry_itr = taste_vector.entrySet().iterator();
            
            ArrayList to_sort = new ArrayList();
            while(entry_itr.hasNext()){
                Map.Entry tmp_entry = (Map.Entry)entry_itr.next();
                to_sort.add(new KeyAndDoubleTFIDF((String)tmp_entry.getKey(),(Double)tmp_entry.getValue()));
            }
            Collections.sort(to_sort,new DoubleComp());
            
            HashMap candidate_map = new HashMap();
            int query_count =  0;
            int tmp_len = to_sort.size();
            for(int i=0;i<((QUERY_LIMIT_WHEN_USE_INVERSE > tmp_len)?tmp_len:QUERY_LIMIT_WHEN_USE_INVERSE);i++){
                String tmp_word = ((KeyAndDoubleTFIDF)to_sort.get(i)).keyword;
                StoreBoxForDocumentEntry tmp_arr[] = InvertedMap.getTargetKeywordBelongs(tmp_word,doc_type);
                int len = tmp_arr.length;
                for(int j=0;j<len;j++){
                    candidate_map.put(tmp_arr[j].data.getAddress(),tmp_arr[j]);
                }
                query_count++;
            }
            
            int len = candidate_map.size();
            StoreBoxForDocumentEntry candidates[] = new StoreBoxForDocumentEntry[len];
            Iterator value_itr = candidate_map.values().iterator();

            int counter = 0;
            while(value_itr.hasNext()){
                candidates[counter] = (StoreBoxForDocumentEntry)value_itr.next();
                counter++;
            }
            
            ArrayList tmpListForSort = new ArrayList();//各要素はSortBoxForWebData
            for(int i=0;i<len;i++){
                StoreBoxForDocumentEntry data = candidates[i];

                if(data.data.isPreciseAnalyzed()){
                    HashMap marged_data_taste = data.data.getKeywords();
                    
                    //キーワードとタグをマージ
                    if(data.data.getTags()!=null){
                        marged_data_taste.putAll(data.data.getTags());    
                    }
                    
                    double simirarity = SimilarityCalculater.calculateSimilarity(marged_data_taste,taste_vector);
                    if(simirarity >= SimilarityCalculater.HATEBU_THRESHOLD){
                        tmpListForSort.add(new SortBoxForDocumentEntry(data.data,new Double(String.valueOf(simirarity))));
                    }    
                }
            }

            //ソート
            ArrayList sorted_list = Util.getBiasSortedList(tmpListForSort);
            return sorted_list;
        }else{
            return getSimilarEntriesByAllCeek(taste_vector,doc_type);
        }
    }
    
//  趣向のベクターを渡して似ているドキュメントの配列と類似度を持ったSearchResultを得る
    public DocumentSearchResult search(HashMap taste_vector,int doc_type){
        //探索を行う
        ArrayList sorted_list = null;
        sorted_list = getSimilarEntriesUsingInvertedMap(taste_vector,doc_type);
//        sorted_list = getSimilarEntriesByAllCeek(taste_vector, doc_type);
        
        //配列へ取り出す
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
        return simirality*Math.log(view_count+2);
    }

    //与えたキーワードに属し,hour時間前から現在の間に追加されたものを返す
    public StoreBoxForDocumentEntry[] getTagetKeywordBelongs(String keyword,int hour,int doc_type){
        return h_abstractor.getTargetsKeywordBelongAfterADate(keyword,new java.sql.Date((new java.util.Date()).getTime()-60*hour),doc_type);
    }
    
    //与えたキーワードに属するドキュメントを得る
    public StoreBoxForDocumentEntry[] getTagetKeywordBelongs(String keyword,int doc_type){
        return h_abstractor.getTargetsKeywordBelong(keyword,doc_type);
    }

    //urlを参照として持っているArrayListの要素を実体に変換する
    private ArrayList refToObj(ArrayList urls,int doc_type) {
        ArrayList result = new ArrayList();
        int len = urls.size();
        for(int i=0;i<len;i++){
            result.add(h_abstractor.getEntryWithURL((String)urls.get(i),doc_type));
        }
        
        return result;
    }
    
    public void setAnalyzedResults(String target_url,HashMap newVecMap,ArrayList sortedKeywords,int doc_type){
        Object already_exist = h_abstractor.getEntryWithURL(target_url,doc_type);
        if(already_exist!=null){
            StoreBoxForDocumentEntry alreadyEntry[] = (StoreBoxForDocumentEntry[])already_exist;
            if(alreadyEntry.length>=1){
                alreadyEntry[0].data.setKeywords(newVecMap);
                alreadyEntry[0].data.setSortedKeywords(sortedKeywords);
                alreadyEntry[0].data.setIsPreciseAnalyzed(true);
                h_abstractor.updateNewEntry(alreadyEntry[0]);
            }else{
                return;
            }
        }else{
            return;
        }
    }
    
    //callした時点より一定時間以上前のエントリを削除する
    public void removeOldEntries(int doc_type){
        if(doc_type==DBUtil.HATEBU_TYPE){
            h_abstractor.removeEntriesBeforeADate(new Date((new Date()).getTime()-3600000*REMOVE_HOURS),doc_type);
        }else{
            h_abstractor.removeEntriesBeforeADate(new Date((new Date()).getTime()-3600000*REMOVE_HOURS),doc_type);        
        }
    }

    public void updateBookmarkCounts(int doc_type) {
        StoreBoxForDocumentEntry entries[] = h_abstractor.getAllEntries(doc_type);
        int len = entries.length;
        int bookmark_counts[] = HatebuHelper.getBookmarkedCount(entries);
        
        for(int i=0;i<len;i++){
            entries[i].data.setView_users(bookmark_counts[i]);
            h_abstractor.updateNewEntry(entries[i]);
        }
    }

    public StoreBoxForDocumentEntry getEntryWithAddress(String address,int doc_type) {
        StoreBoxForDocumentEntry entries[] =  h_abstractor.getEntryWithURL(address,doc_type);
        if(entries!=null){
            if(entries.length==1){
                return entries[0];
            }else{
                return null;
            }    
        }else{
            return null;
        }
    }
    
    //CEEK.JP NEWSの検索をやる時用
    public DocumentSearchResult searchNews(HashMap taste_vector,String category, StoreBoxForDocumentEntry[] all_entries) {
        ArrayList tmpListForSort = new ArrayList();//各要素はSortBoxForWebData
        
        int len = all_entries.length;
        for(int i=0;i<len;i++){
            StoreBoxForDocumentEntry data = all_entries[i];
            
            if((data.data.isPreciseAnalyzed())&&(category.equals(data.data.getCategory()))){
                HashMap marged_data_taste = data.data.getKeywords();
                
                double simirarity = SimilarityCalculater.calculateSimilarity(marged_data_taste,taste_vector);
                if(simirarity >= SimilarityCalculater.HATEBU_THRESHOLD){
                    tmpListForSort.add(new SortBoxForDocumentEntry(data.data,new Double(String.valueOf(simirarity))));
                }    
            }
        }

        //ソート
        ArrayList sorted_list = Util.getBiasSortedList(tmpListForSort);
        
        //配列へ取り出す
        DocumentEntry[] entries = new DocumentEntry[sorted_list.size()];
        double dbl_simirarities[] = new double[sorted_list.size()];
        double eval_points[] = new double[sorted_list.size()];
        for(int i=0;i<entries.length;i++){
          entries[i] = ((SortBoxForDocumentEntry)sorted_list.get(i)).entry;
          dbl_simirarities[i] = ((SortBoxForDocumentEntry)sorted_list.get(i)).simirality.doubleValue();
          eval_points[i] = dbl_simirarities[i];
        }
            
        DocumentSearchResult result = new DocumentSearchResult(entries,dbl_simirarities,eval_points);
        return result;
    }

    public StoreBoxForDocumentEntry[] getAllEntry(int doc_type) {
        return h_abstractor.getAllEntries(doc_type);
    }
}
