package jp.ryo.informationPump.server.db;

import java.sql.*;
import java.sql.Date;
import java.util.*;

import jp.crossfire.framework.database.HyperDbException;
import jp.ryo.informationPump.server.db.tables.*;

public class UserDBManagerWrapper extends DBManager {
    private static final long serialVersionUID = 1L;
    private static UserDBManagerWrapper instance;

    private UserDBManagerWrapper() {
        super();
    }
    
    public static UserDBManagerWrapper getInstance(){
        if(instance==null){
            instance = new UserDBManagerWrapper();
        }
        return instance;
    }
    
    public void deleteAllUserViewedEntry(){
        UserDBManager userManager = new UserDBManager();
        Hashtable input0 = new Hashtable();
        try {
            Connection con = getConnection(true);
            try{
                userManager.deleteUserViewedURLAll(con,input0);    
            }finally{
                returnConnection(con,true);    
            }
        } catch (HyperDbException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteUserViewedEntryWithURL(String url){
        
        UserDBManager userManager = new UserDBManager();
        Hashtable input0 = new Hashtable();
        input0.put("url",url);
        try {
            Connection con = getConnection(true);
            try{
                userManager.deleteUserViewedURLWithURL(con,input0);    
            }finally{
                returnConnection(con,true);    
            }
        } catch (HyperDbException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteUserViewedEntryWithID(String id){
        UserDBManager userManager = new UserDBManager();
        Hashtable input0 = new Hashtable();
        input0.put("id",id);
        try {
            Connection con = getConnection(true);
            try{
                userManager.deleteUserViewedURLWithURL(con,input0);    
            }finally{
                returnConnection(con,true);    
            }
        } catch (HyperDbException e) {
            e.printStackTrace();
        }
    }
    
//    public Userviewedentries[] selectDocumentsWithID(String id){
//        try {
//            UserDBManager userManeger = new UserDBManager();
//            Hashtable input0 = new Hashtable();
//            input0.put("id",id);
//            Connection con = getConnection(true);
//            List list = userManeger.selectUserViewedURL(con,input0,null,null);
//            returnConnection(con,true);
//            
//            Iterator itr = list.iterator();
//            Userviewedentries entries[] = new Userviewedentries[list.size()];
//            int count=0;
//            while(itr.hasNext()){
//                Hashtable tmp_entry = (Hashtable)itr.next();
//                Userviewedentries new_entry = new Userviewedentries();
//                new_entry.setDocUrl((String)tmp_entry.get("URL"));
//                new_entry.setId((String)tmp_entry.get("ID"));
//                entries[count++] = new_entry;
//            }
//            return entries;
//        } catch (HyperDbException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
    
    public void updateUser(Userentry user){
//      まずは削除
        deleteUser(user.getId());
        //配列からHashMapへ
        UserKeywordTable key_tables[] =  user.getKey_table();
        HashMap key_table_map = new HashMap();
        int len = key_tables.length;
        for(int i=0;i<len;i++){
            key_table_map.put(key_tables[i].getKeyword(),key_tables[i].getTfidfValue());
        }
        
        //新しいエントリとして追加
        createUser(user.getId(),user.getPassword(),user.getName(),user.getMailAddress(),user.getAge().intValue(),user.getRegistDate(),key_table_map);
    }
    
    public UserKeywordTable[] getKeywordTableWithID(String id)  {
        UserKeywordTable results[];
        try {
            UserDBManager userManeger = new UserDBManager();
            Hashtable input0 = new Hashtable();
            input0.put("id",id);
            Connection con = getConnection(true);
            List list = null;
            try{
                list = userManeger.selectKeyTableWithID(con,input0,null,null);    
            }finally{
                returnConnection(con,true);    
            }
            
            Iterator itr = list.iterator();
            results = new UserKeywordTable[list.size()];
            int count=0;
            while(itr.hasNext()){
                Hashtable entry = (Hashtable)itr.next();
                UserKeywordTable new_entry = new UserKeywordTable();
                new_entry.setUserId((String)entry.get("USER_ID"));
                new_entry.setKeyword((String)entry.get("KEYWORD"));
                new_entry.setTfidfValue((Double)entry.get("TFIDF_VALUE"));
                results[count++] = new_entry;
            }
            
            return results; 
        } catch (HyperDbException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public String[] getViewedURLsWithID(String id){
        UserDBManager user_manager = new UserDBManager();
        Hashtable input0 = new Hashtable();
        input0.put("id",id);
        
        try {
            Connection con = getConnection(true);
            List result_list = null;
            try{
                user_manager.selectUserViewedURL(con,input0,null,null);
                result_list = user_manager.getUserviewedentries();
            }finally{
                returnConnection(con,true);                
            }
            
            int len = result_list.size();
            String result_urls[] = new String[len];
            Iterator itr = result_list.iterator();
            int counter = 0;
            while(itr.hasNext()){
                Userviewedentries tmp_entry = (Userviewedentries)itr.next();
                result_urls[counter++] = tmp_entry.getDocUrl();
            }
            
            return result_urls;
        } catch (HyperDbException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public void deleteAll()  {
        UserDBManager userManeger = new UserDBManager();
        Hashtable input0 = new Hashtable();
        try {
            Connection con = getConnection(true);
            try{
                userManeger.deleteKeywordAll(con,input0);
            }finally{
                returnConnection(con,true);    
            }
            
            
            con = getConnection(true);
            try{
                userManeger.deleteUserAll(con,input0);    
            }finally{
                returnConnection(con,true);    
            }
            
            con = getConnection(true);
            try{
                userManeger.deleteUserViewedURLAll(con,input0);    
            }finally{
                returnConnection(con,true);    
            }
        } catch (HyperDbException e) {
            e.printStackTrace();
        }
    }
    
    public void insertUserViewdEntry(String id,String url){

        Hashtable input0 = new Hashtable();
        input0.put("id",id);
        input0.put("doc_url",url);
        
        UserDBManager u_manager = new UserDBManager();
        Connection con = getConnection(true);
        try {
            u_manager.insertUserViewedURL(con,input0);
        } catch (HyperDbException e) {
            e.printStackTrace();
        }finally{
            returnConnection(con,true);
        }
    }
    
    public Userentry[] selectAll()  {
        try {
            UserDBManager userManeger = new UserDBManager();
            Hashtable input0 = new Hashtable();
            
            Connection con = getConnection(true);
            List list = null;
            try{
                list = userManeger.selectAll(con,input0,null,null);
            }finally{
                returnConnection(con,true);    
            }
            
            Iterator itr = list.iterator();
            Userentry entries[] = new Userentry[list.size()];
            int count=0;
            while(itr.hasNext()){
                Hashtable tmp_entry = (Hashtable)itr.next();
                Userentry new_entry = new Userentry();
                new_entry.setAge((Integer)tmp_entry.get("AGE"));
                new_entry.setId((String)tmp_entry.get("ID"));
                new_entry.setPassword((String)tmp_entry.get("PASSWORD"));
                new_entry.setMailAddress((String)tmp_entry.get("MAIL_ADDRESS"));
                new_entry.setName((String)tmp_entry.get("NAME"));
                new_entry.setRegistDate((Date)tmp_entry.get("REGIST_DATE"));
                new_entry.setCacheDate((Date)tmp_entry.get("CACHE_DATE"));
                
                new_entry.setKey_table(getKeywordTableWithID(new_entry.getId()));
                new_entry.setPastReadPages(getViewedURLsWithID(new_entry.getId()));
                
                entries[count++] = new_entry;
            }
            return entries;
        } catch (HyperDbException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    public Userentry[] selectWithID(String id)  {
        try {
            UserDBManager userManeger = new UserDBManager();
            Hashtable input0 = new Hashtable();
            input0.put("id",id);
            
            Connection con = getConnection(true);
            List list = null;
            try{
                list = userManeger.selectWithID(con,input0,null,null);    
            }finally{
                returnConnection(con,true);    
            }

            Iterator itr = list.iterator();
            Userentry entries[] = new Userentry[list.size()];
            int count=0;
            while(itr.hasNext()){
                Hashtable tmp_entry = (Hashtable)itr.next();
                Userentry new_entry = new Userentry();
                new_entry.setAge((Integer)tmp_entry.get("AGE"));
                new_entry.setId((String)tmp_entry.get("ID"));
                new_entry.setPassword((String)tmp_entry.get("PASSWORD"));
                new_entry.setMailAddress((String)tmp_entry.get("MAIL_ADDRESS"));
                new_entry.setName((String)tmp_entry.get("NAME"));
                new_entry.setRegistDate((Date)tmp_entry.get("REGIST_DATE"));
                new_entry.setCacheDate((Date)tmp_entry.get("CACHE_DATE"));
                
                new_entry.setKey_table(getKeywordTableWithID(id));
                new_entry.setPastReadPages(getViewedURLsWithID(id));
                
                entries[count++] = new_entry;
            }
            return entries;
        } catch (HyperDbException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public void deleteUser(String id)  {
        try {
            UserDBManager userManager = new UserDBManager();
            Hashtable input0 = new Hashtable();
            input0.put("id",id);
            Connection con = getConnection(true);
            try{
                userManager.deleteKeywordsWithID(con,input0);    
            }finally{
                returnConnection(con,true);    
            }
            
            con = getConnection(true);
            try{
                userManager.deleteUserViewedURLWithUser(con,input0);    
            }finally{
                returnConnection(con,true);    
            }
            
            con = getConnection(true);
            try{
                userManager.deleteUserWithID(con,input0);    
            }finally{
                returnConnection(con,true);    
            }
        } catch (HyperDbException e) {
            e.printStackTrace();
        }
    }
    
    public void createUser(String id,String password,String name,String mail_address,int age,Date registDate,HashMap key_table) {
        try {

//          キーワードの配列に対応するデータをDBへ
            UserDBManager userManager = new UserDBManager();
            if(key_table!=null){
                Set entry_set = key_table.entrySet();
                Iterator itr = entry_set.iterator();
                while(itr.hasNext()){
                    Map.Entry entry = (Map.Entry)itr.next();
                    String keyword = (String)entry.getKey();
                    Double tfidf = (Double)entry.getValue();
                    
                    Hashtable input0 = new Hashtable();
                    input0.put("user_id",id);
                    input0.put("keyword",keyword.toLowerCase());
                    input0.put("tfidf_value",tfidf);
                    
                    Connection con = getConnection(true);
                    try{
                        userManager.insertNewKeyword(con,input0);    
                    }finally{
                        returnConnection(con,true);    
                    }
                }
            }
            
            
            Hashtable input0 = new Hashtable();
            input0.put("id", id);
            input0.put("password",password);
            input0.put("name", name);
            input0.put("mail_address", mail_address);
            input0.put("age",new Integer(age));
            input0.put("regist_date",registDate);
            input0.put("cache_date",registDate);
            Connection con = getConnection(true);
            userManager.insertNewUser(con, input0);
            returnConnection(con,true);
        } catch (HyperDbException e) {
            e.printStackTrace();
        }
    }
    
    

}
