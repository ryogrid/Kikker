package jp.ryo.informationPump.server.db.abstractor;

import java.util.*;

import jp.ryo.informationPump.server.data.StoreBoxForDocumentEntry;

//転置ファイル
public class InvertedMap {
    //まずドキュメント番号(Integer)をキー,Valueに対応するHashMapが入っており、
    //そのHashMap内はキーをkeyword(String)でValueがドキュメントが含まれたArrayListである
    private static HashMap inverted_map = new HashMap();
    private static boolean isCanUse;
    
    //全エントリを配列で渡して、それを転置マップ化
    //要ドキュメントタイプ
    public static void makeInvertedMap(StoreBoxForDocumentEntry[] seed,int doc_type){
        isCanUse = false;
        
        Object target_map_candidate = inverted_map.get(new Integer(doc_type));
        HashMap target_doctype_map = null;
        if(target_map_candidate!=null){
            target_doctype_map = (HashMap)target_map_candidate;
        }else{
            target_doctype_map = new HashMap();
            inverted_map.put(new Integer(doc_type),target_doctype_map);
        }
        
        //過去のマッピングを全削除
        target_doctype_map.clear();
        
        int len = seed.length;
        for(int i=0;i<len;i++){
            StoreBoxForDocumentEntry entry = seed[i];
            HashMap key_map = entry.data.getKeywords();
            Iterator itr = key_map.keySet().iterator();
            while(itr.hasNext()){
                String keyword = (String)itr.next();
                Object obj = target_doctype_map.get(keyword);
                if(obj!=null){
                    ArrayList list = (ArrayList)obj;
                    list.add(entry);
                }else{
                    ArrayList list = new ArrayList();
                    list.add(entry);
                    target_doctype_map.put(keyword,list);
                }
            }
        }
    }
    
    public static StoreBoxForDocumentEntry[] getTargetKeywordBelongs(String keyword,int doc_type){ 
        Object target_map_candidate = inverted_map.get(new Integer(doc_type));
        
        if(target_map_candidate!=null){
            HashMap target_doctype_map = (HashMap)target_map_candidate;
            Object obj = target_doctype_map.get(keyword);
            if(obj!=null){
                ArrayList list = (ArrayList)obj;
                int len = list.size();
                StoreBoxForDocumentEntry results[] = new StoreBoxForDocumentEntry[len];
                for(int i=0;i<len;i++){
                    results[i] = (StoreBoxForDocumentEntry)list.get(i);
                }
                
                return results;
            }else{
                return new StoreBoxForDocumentEntry[0];
            }
        }else{
            System.out.println("InvertedMap#getTargetKeywordBelongs recieve unknown doc type");
            return new StoreBoxForDocumentEntry[0];
        }
    }
    
    public static boolean isCanUse(){
        return isCanUse;
    }
    
    public static void setIsCanUse(boolean isCanUse){
        InvertedMap.isCanUse = isCanUse;
    }
}
