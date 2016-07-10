package jp.ryo.informationPump.server.rpc;

import java.util.*;

import jp.ryo.informationPump.server.collab.CollaborativeInformatoinManager;
import jp.ryo.informationPump.server.crawler.DBCrawledDataManager;
import jp.ryo.informationPump.server.crawler.analyze.InDependentDocumentAnalyzer;
import jp.ryo.informationPump.server.data.*;
import jp.ryo.informationPump.server.db.DBManager;
import jp.ryo.informationPump.server.util.*;

import org.apache.xmlrpc.*;

public class KikkerAPIHandller implements XmlRpcHandler {
private final int MAX_KEYWORD_COUNT = 100;
private final int MAX_RESULT_COUNT = 100;
    
    public Object execute(XmlRpcRequest arg0) throws XmlRpcException {
        //カウンタを回す
        CounterManager.getInstance().incAPIUsedCount();
        
        if (arg0.getMethodName().equals("KikkerWebAPI.search")) {
            return search(arg0);
        }else if(arg0.getMethodName().equals("KikkerWebAPI.searchByKeyword")){
            return searchByKeyword(arg0);
        }else if(arg0.getMethodName().equals("KikkerWebAPI.getTasteByURL")){
            return getTasteByURL(arg0); 
        }else if(arg0.getMethodName().equals("KikkerWebAPI.searchWithCollaborative")){
            return searchWithCollaborative(arg0);
        }else{
            throw new XmlRpcException("I do not have the method:" + arg0.getMethodName());
        }
    }

    private Object searchWithCollaborative(XmlRpcRequest arg0) {
        synchronized(CollaborativeInformatoinManager.getInstance()){
            String user_id = (String) arg0.getParameter(0);
            
            CollaborativeInformatoinManager collab_manager = CollaborativeInformatoinManager.getInstance();
            ArrayList result = collab_manager.getSuggestedDocumentes(user_id); 

            DBCrawledDataManager c_manager = (DBCrawledDataManager)DBCrawledDataManager.getInstance();
            
            ArrayList doc_title_list = new ArrayList();
            ArrayList doc_url_list = new ArrayList();
            ArrayList eval_point_list = new ArrayList();
            ArrayList crawled_data_list = new ArrayList();
            ArrayList tags = new ArrayList();
            ArrayList categories = new ArrayList();
            ArrayList view_counts = new ArrayList();
            
            int len = result.size();
            int result_count = 0;
            for(int i=0;i<len;i++){
                if(result_count > MAX_RESULT_COUNT){
                    break;
                }
                
                StoreBoxForDocumentEntry a_suggested = c_manager.getEntryWithAddress(((CollabSortBox)result.get(i)).url,DBUtil.HATEBU_TYPE);

                if(a_suggested!=null){
                    result_count++;
                    
                    doc_title_list.add(a_suggested.data.getTitle());
                    doc_url_list.add(a_suggested.data.getAddress());
                    eval_point_list.add(new Double(((CollabSortBox)result.get(i)).eval_point.doubleValue()));
                    crawled_data_list.add(a_suggested.data.getClawledDate().toString());
                    view_counts.add(new Integer(a_suggested.data.getView_users()));
                    
                    //キーワードをソートしてキーワードとその値のセットで突っ込む
                    ArrayList tmp_tags = new ArrayList();
                    Iterator itr = a_suggested.data.getKeywords().entrySet().iterator();
                    while(itr.hasNext()){
                        Map.Entry entry = (Map.Entry)itr.next();
                        
                        tmp_tags.add(new KeyAndDoubleTFIDF((String)entry.getKey(),(Double)entry.getValue()));
                    }
                    Collections.sort(tmp_tags,new DoubleComp());
                    
                    int key_len = tmp_tags.size();
                    ArrayList to_add = new ArrayList();
                    for(int j=0;j<key_len;j++){
                        KeyAndDoubleTFIDF ka_tfidf = (KeyAndDoubleTFIDF)tmp_tags.get(j);
                        ArrayList each_keys = new ArrayList();
                        each_keys.add(ka_tfidf.keyword);
                        each_keys.add(ka_tfidf.tfidf);
                        to_add.add(each_keys);
                    }
                    tags.add(to_add);
                    
                    //カテゴリを追加
                    categories.add(a_suggested.data.getCategory());
                }
//                else{
//                    doc_title_list.add("");
//                    doc_url_list.add(((CollabSortBox)result.get(i)).url);
//                    eval_point_list.add(new Double(((CollabSortBox)result.get(i)).eval_point.doubleValue()));
//                    crawled_data_list.add(((CollabSortBox)result.get(i)).crawled_date.toGMTString());
//                    view_counts.add(new Integer(((CollabSortBox)result.get(i)).view_counts));
//                    tags.add(new ArrayList());
//                    result_count++;
//                }
            }
            
            Hashtable table = new Hashtable();
            table.put("titles", doc_title_list);
            table.put("urls",doc_url_list);
            table.put("eval_points",eval_point_list);
            table.put("crawled_dates",crawled_data_list);
            table.put("tags",tags);
            table.put("categories",categories);
            table.put("view_counts",view_counts);
                    
            return table;
        }
    }

    private Hashtable getTasteByURL(XmlRpcRequest arg0) throws XmlRpcException {
        int doc_type = ((Integer) arg0.getParameter(0)).intValue();
        String url = (String) arg0.getParameter(1);
        
        DBCrawledDataManager c_manager = (DBCrawledDataManager)DBCrawledDataManager.getInstance();
        StoreBoxForDocumentEntry result = c_manager.getEntryWithAddress(url,doc_type);
        
        Hashtable table = new Hashtable();
        HashMap taste_vec = null;
        if(result == null){
            System.out.println("start analyzing a page in KikkerAPIHandller#getTasteByURL");
            InDependentDocumentAnalyzer analyzer = new InDependentDocumentAnalyzer();
            taste_vec = analyzer.analyzeByURLNotAsThread(url);
            table.put("title","");
            table.put("crawled_date",new Date().toString());
//            throw new XmlRpcException("Kikker doesn't know given url.");
//          カテゴリを追加
            table.put("category","General");
        }else{
            taste_vec = result.data.getKeywords();
            table.put("title",result.data.getTitle());
            table.put("crawled_date",result.data.getClawledDate().toString());
            
            //カテゴリを追加
            table.put("category",result.data.getCategory());
        }
        
        //キーワードをソートしてキーワードとその値のセットで突っ込む
        ArrayList tmp_tags = new ArrayList();
        Iterator itr = taste_vec.entrySet().iterator();
        while(itr.hasNext()){
            Map.Entry entry = (Map.Entry)itr.next();
            
            tmp_tags.add(new KeyAndDoubleTFIDF((String)entry.getKey(),(Double)entry.getValue()));
        }
        Collections.sort(tmp_tags,new DoubleComp());
        
        int key_len = tmp_tags.size();
        ArrayList to_add = new ArrayList();
        for(int j=0;j<key_len;j++){
            KeyAndDoubleTFIDF ka_tfidf = (KeyAndDoubleTFIDF)tmp_tags.get(j);
            ArrayList each_keys = new ArrayList();
            each_keys.add(ka_tfidf.keyword);
            each_keys.add(ka_tfidf.tfidf);
            to_add.add(each_keys);
        }
        table.put("tags",to_add);

        return table;
    }

    private Hashtable searchByKeyword(XmlRpcRequest arg0) {
        int doc_type = ((Integer) arg0.getParameter(0)).intValue();
        String keyword = (String) arg0.getParameter(1);
        
        DBCrawledDataManager c_manager = (DBCrawledDataManager)DBCrawledDataManager.getInstance();
        StoreBoxForDocumentEntry results[] = c_manager.getTagetKeywordBelongs(keyword, doc_type);
        
        ArrayList doc_title_list = new ArrayList();
        ArrayList doc_url_list = new ArrayList();
        ArrayList crawled_data_list = new ArrayList();
        ArrayList tags = new ArrayList();
        ArrayList categories = new ArrayList();
        ArrayList view_counts = new ArrayList();
        
        int len = results.length;
        for(int i=0;i<len;i++){
            doc_title_list.add(results[i].data.getTitle());
            doc_url_list.add(results[i].data.getAddress());
            crawled_data_list.add(results[i].data.getClawledDate().toString());
            view_counts.add(new Integer(results[i].data.getView_users()));
            
            //キーワードをソートしてキーワードとその値のセットで突っ込む
            ArrayList tmp_tags = new ArrayList();
            Iterator itr = results[i].data.getKeywords().entrySet().iterator();
            while(itr.hasNext()){
                Map.Entry entry = (Map.Entry)itr.next();
                
                tmp_tags.add(new KeyAndDoubleTFIDF((String)entry.getKey(),(Double)entry.getValue()));
            }
            Collections.sort(tmp_tags,new DoubleComp());
            
            int key_len = tmp_tags.size();
            ArrayList to_add = new ArrayList();
            for(int j=0;j<key_len;j++){
                KeyAndDoubleTFIDF ka_tfidf = (KeyAndDoubleTFIDF)tmp_tags.get(j);
                ArrayList each_keys = new ArrayList();
                each_keys.add(ka_tfidf.keyword);
                each_keys.add(ka_tfidf.tfidf);
                to_add.add(each_keys);
            }
            tags.add(to_add);
            
            //カテゴリを追加
            categories.add(results[i].data.getCategory());
        }
        
        Hashtable table = new Hashtable();
        table.put("titles", doc_title_list);
        table.put("urls",doc_url_list);
        table.put("crawled_dates",crawled_data_list);
        table.put("tags",tags);
        table.put("categories",categories);
        table.put("view_counts",view_counts);
        
        return table;
    }

    private Hashtable search(XmlRpcRequest arg0) throws XmlRpcException {
        Integer doc_type = (Integer) arg0.getParameter(0);
        Object[] keyword_list = (Object[]) arg0.getParameter(1);
        Object[] taste_value_list = (Object[]) arg0.getParameter(2);
        
//        if((keyword_list.length > MAX_KEYWORD_COUNT)||(taste_value_list.length > MAX_KEYWORD_COUNT)){
//            throw new XmlRpcException("too many keywords!! please use keywords under " + MAX_KEYWORD_COUNT + " keywords.");
//        }else 
            
        if(!((doc_type.intValue() == 2)||(doc_type.intValue() == 3)||(doc_type.intValue() == 4))){
            throw new XmlRpcException("please use 2 or 3 or 4 as doc_type. " + doc_type + " is illegal.");
        }
        
        HashMap taste_map = new HashMap();
        int len = keyword_list.length;
        for(int i=0;i<len;i++){
            taste_map.put(keyword_list[i],taste_value_list[i]);
        }
        
        DBCrawledDataManager c_manager = (DBCrawledDataManager)DBCrawledDataManager.getInstance();
        DocumentSearchResult result =  c_manager.search(taste_map,doc_type.intValue());

        ArrayList doc_title_list = new ArrayList();
        ArrayList doc_url_list = new ArrayList();
        ArrayList eval_point_list = new ArrayList();
        ArrayList crawled_data_list = new ArrayList();
        ArrayList tags = new ArrayList();
        ArrayList categories = new ArrayList();
        ArrayList view_counts = new ArrayList();
        
        len = result.results.length;
        for(int i=0;i<((len > MAX_RESULT_COUNT)?MAX_RESULT_COUNT:len);i++){
            doc_title_list.add(result.results[i].getTitle());
            doc_url_list.add(result.results[i].getAddress());
            eval_point_list.add(new Double(result.eval_points[i]*100.0));
            crawled_data_list.add(result.results[i].getClawledDate().toString());
            view_counts.add(new Integer(result.results[i].getView_users()));
            
            //キーワードをソートしてキーワードとその値のセットで突っ込む
            ArrayList tmp_tags = new ArrayList();
            Iterator itr = result.results[i].getKeywords().entrySet().iterator();
            while(itr.hasNext()){
                Map.Entry entry = (Map.Entry)itr.next();
                
                tmp_tags.add(new KeyAndDoubleTFIDF((String)entry.getKey(),(Double)entry.getValue()));
            }
            Collections.sort(tmp_tags,new DoubleComp());
            
            int key_len = tmp_tags.size();
            ArrayList to_add = new ArrayList();
            for(int j=0;j<key_len;j++){
                KeyAndDoubleTFIDF ka_tfidf = (KeyAndDoubleTFIDF)tmp_tags.get(j);
                ArrayList each_keys = new ArrayList();
                each_keys.add(ka_tfidf.keyword);
                each_keys.add(ka_tfidf.tfidf);
                to_add.add(each_keys);
            }
            tags.add(to_add);
            
            //カテゴリを追加
            categories.add(result.results[i].getCategory());
        }
        
        Hashtable table = new Hashtable();
        table.put("titles", doc_title_list);
        table.put("urls",doc_url_list);
        table.put("eval_points",eval_point_list);
        table.put("crawled_dates",crawled_data_list);
        table.put("tags",tags);
        table.put("categories",categories);
        table.put("view_counts",view_counts);
                
        return table;
    }
}
