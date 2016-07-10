package jp.crossfire.sample;

import java.sql.*;
import java.sql.Date;
import java.util.*;

import org.omg.PortableInterceptor.USER_EXCEPTION;

import jp.crossfire.framework.database.HyperDbException;
import jp.crossfire.framework.database.SQLExecutor;
import jp.ryo.informationPump.server.db.*;
import jp.ryo.informationPump.server.db.tables.*;

public class SampleMain extends DBManager{

//	public static void sampleMain(Connection con) throws HyperDbException{
//		// Init Data
//        createEntry(con,"神林の冒険","http://ryogrid.myhome.cx",3,7,new Date((new java.util.Date()).getTime()),"sns","いいねRyo");
//        createEntry(con,"タコの冒険","http://hage.myhome.cx",4,1,new Date((new java.util.Date()).getTime()),"sbm","いいねぶた");
//        createEntry(con,"ブタの冒険","http://butagrid.myhome.cx",143,56,new Date((new java.util.Date()).getTime()),"ajax","いいねタコ");
//
//        createUser(con,"ryo","ryoryo","亮","hpcs@docmo.ne.jp",12,new Date((new java.util.Date()).getTime()));
//        createUser(con,"you","youyou","優","you@coins.ne.jp",12,new Date((new java.util.Date()).getTime()));
//        createUser(con,"tyou","tyoutyou","超","tyou@chinese.ne.jp",12,new Date((new java.util.Date()).getTime()));
//        createUser(con,"maiko","maikomaiko","麻衣子","hpcs@house.ne.jp",12,new Date((new java.util.Date()).getTime()));
//
//        Webdocumententry entries[] = selectWithAddress(con,"http://ryogrid.myhome.cx");
//        for(int i=0;i < entries.length;i++){
//            System.out.println(entries[i].getTitle());
//            System.out.println(entries[i].getUrl());
//            System.out.println(entries[i].getCategory());
//            System.out.println(entries[i].getDescription());
//        }
//
//        Userentry usr_entries[] = selectWithID(con,"ryo");
//        for(int i=0;i<usr_entries.length;i++){
//            System.out.println(usr_entries[i].getId());
//            System.out.println(usr_entries[i].getName());
//            System.out.println(usr_entries[i].getPassword());
//        }
//
//
//        deleteEntry(con,"http://butagrid.myhome.cx");
//		deleteUser(con,"ryo");
//
//        WebDataDBManager web_manager = new WebDataDBManager();
//        List web_list =  web_manager.selectAll(con,new Hashtable(),null,null);
//        Iterator web_itr = web_list.iterator();
//        while(web_itr.hasNext()){
//            Hashtable each_entry = (Hashtable)web_itr.next();
//            System.out.println(each_entry.get("TITLE"));
//            System.out.println(each_entry.get("URL"));
//        }
//
//        UserDBManager user_manager = new UserDBManager();
//        List user_list =  user_manager.selectAll(con,new Hashtable(),null,null);
//        Iterator user_itr = user_list.iterator();
//        while(user_itr.hasNext()){
//            Hashtable each_entry = (Hashtable)user_itr.next();
//            System.out.println(each_entry.get("ID"));
//            System.out.println(each_entry.get("PASSWORD"));
//        }
//
//
//    }

    public static void showFetchSize() throws HyperDbException{
        DBManager.initMySQL();

        DBManager db_manager = new DBManager();
        Connection con = getConnection();
        try {
            Statement state = con.createStatement();
            state.execute("select * from webdocumententry");
            ResultSet set = state.getResultSet();
            System.out.println(set.getFetchSize());
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void sampleMain2() throws HyperDbException{
//        try {
            DBManager.initMySQL();

            HashMap key_vec = new HashMap();
            key_vec.put("hage",new Double(12.245));
            key_vec.put("boke",new Double(12.245));
            key_vec.put("suko",new Double(12.245));


            UserDBManagerWrapper u_wrapper = UserDBManagerWrapper.getInstance();
//            u_wrapper.deleteAll();
            u_wrapper.createUser("ryo","ryoryo","亮","hpcs@docmo.ne.jp",12,new Date((new java.util.Date()).getTime()),key_vec);
            u_wrapper.createUser("you","youyou","優","you@coins.ne.jp",12,new Date((new java.util.Date()).getTime()),key_vec);
            u_wrapper.createUser("tyou","tyoutyou","超","tyou@chinese.ne.jp",12,new Date((new java.util.Date()).getTime()),key_vec);
            u_wrapper.createUser("maiko","maikomaiko","麻衣子","hpcs@house.ne.jp",12,new Date((new java.util.Date()).getTime()),key_vec);

            Userentry usr_entries[] = u_wrapper.selectWithID("ryo");
            for(int i=0;i<usr_entries.length;i++){
                System.out.println(usr_entries[i].getId());
                System.out.println(usr_entries[i].getName());
                System.out.println(usr_entries[i].getPassword());
                UserKeywordTable table[] =  usr_entries[i].getKey_table();
                for(int j=0;j<table.length;j++){
                    System.out.println(table[j].getKeyword());
                }
            }

            u_wrapper.deleteUser("ryo");

            Userentry user_list[] = u_wrapper.selectAll();
            for(int i=0;i<user_list.length;i++){
                System.out.println(user_list[i].getId());
                System.out.println(user_list[i].getPassword());
            }

            u_wrapper.insertUserViewdEntry("obe","http://google.com");
            u_wrapper.deleteUserViewedEntryWithID("obe");
            u_wrapper.insertUserViewdEntry("obe","http://google.com");
            u_wrapper.deleteUserViewedEntryWithID("http://google.com");

//          Init Data
            WebDataDBManagerWrapper w_wrapper = WebDataDBManagerWrapper.getInstance();
            w_wrapper.deleteAll();

//          Init Data
            WebDataDBManagerWrapper w_create_wrapper = WebDataDBManagerWrapper.getInstance();
            w_create_wrapper.insertEntry("神林の冒険","http://ryogrid.myhome.cx",3,7,new Date((new java.util.Date()).getTime()),"sns","いいねRyo",2,key_vec,key_vec,1);
            w_create_wrapper.insertEntry("タコの冒険","http://hage.myhome.cx",4,1,new Date((new java.util.Date()).getTime()),"sbm","いいねぶた",2,key_vec,key_vec,0);
            w_create_wrapper.insertEntry("ブタの冒険","http://butagrid.myhome.cx",143,56,new Date((new java.util.Date()).getTime()),"ajax","いいねタコ",3,key_vec,key_vec,1);

            System.out.println("キーワードで選択");
            Webdocumententry key_selected_entries[] = w_wrapper.selectEntryWithKeyword("hage",2);
            for(int i=0;i < key_selected_entries.length;i++){
                System.out.println(key_selected_entries[i].getTitle());
                System.out.println(key_selected_entries[i].getUrl());
                System.out.println(key_selected_entries[i].getCategory());
                System.out.println(key_selected_entries[i].getDescription());
                WebdocumentKeywordTable table[] =  key_selected_entries[i].getKey_tables();
                for(int j=0;j<table.length;j++){
                    System.out.println(table[j].getKeyword());
                }
            }

            System.out.println("日付とキーワードで選択");
            java.util.Date date = new java.util.Date();
            date.setDate(date.getDate()-1);
            Webdocumententry date_selected_entries[] = w_wrapper.selectWithKeywordAfterADate("hage",date,2);
            for(int i=0;i < date_selected_entries.length;i++){
                System.out.println(date_selected_entries[i].getTitle());
                System.out.println(date_selected_entries[i].getUrl());
                System.out.println(date_selected_entries[i].getCategory());
                System.out.println(date_selected_entries[i].getDescription());
                WebdocumentKeywordTable table[] =  date_selected_entries[i].getKey_tables();
                for(int j=0;j<table.length;j++){
                    System.out.println(table[j].getKeyword());
                }
            }


            Webdocumententry entries[] = w_wrapper.selectWithAddress("http://ryogrid.myhome.cx",2);
            for(int i=0;i < entries.length;i++){
                System.out.println(entries[i].getTitle());
                System.out.println(entries[i].getUrl());
                System.out.println(entries[i].getCategory());
                System.out.println(entries[i].getDescription());
                WebdocumentKeywordTable table[] =  entries[i].getKey_tables();
                for(int j=0;j<table.length;j++){
                    System.out.println(table[j].getKeyword());
                }
            }
//
            long start = System.currentTimeMillis();
            Webdocumententry tmp[] =  w_wrapper.selectWithAddress("http://ryogrid.myhome.cx",2);
            System.out.println("selecting with address have needed " + (System.currentTimeMillis() - start) + "msec");
            System.out.println(tmp[0]);

            w_wrapper.deleteEntry("http://butagrid.myhome.cx",2);



            Webdocumententry web_list[] = w_wrapper.selectAll(2);
            for(int i=0;i<web_list.length;i++){
                System.out.println(web_list[i].getTitle());
                System.out.println(web_list[i].getUrl());
                WebdocumentKeywordTable table[] =  web_list[i].getKey_tables();
                for(int j=0;j<table.length;j++){
                    System.out.println(table[j].getKeyword());
                }
            }

            int REMOVE_HOURS = 48;
            long now_time = (new java.util.Date()).getTime();
            w_wrapper.deleteBeforeADateEntries(new java.sql.Date( now_time - REMOVE_HOURS*3600000),2);

            Webdocumententry entries2[] = w_wrapper.selectBeforeADateEntries(new java.util.Date(),2);
            for(int i=0;i<entries2.length;i++){
                System.out.println(entries2[i].getTitle());
            }


//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

//    private static Userentry[] selectWithID(Connection con,String id){
//        try {
//            UserDBManager userManeger = new UserDBManager();
//            Hashtable input0 = new Hashtable();
//            input0.put("id",id);
//            List list = userManeger.selectWithID(con,input0,null,null);
//            Iterator itr = list.iterator();
//            Userentry entries[] = new Userentry[list.size()];
//            int count=0;
//            while(itr.hasNext()){
//                Hashtable tmp_entry = (Hashtable)itr.next();
//                Userentry new_entry = new Userentry();
//                new_entry.setAge((Integer)tmp_entry.get("AGE"));
//                new_entry.setId((String)tmp_entry.get("ID"));
//                new_entry.setPassword((String)tmp_entry.get("PASSWORD"));
//                new_entry.setMailAddress((String)tmp_entry.get("MAIL_ADDRESS"));
//                new_entry.setName((String)tmp_entry.get("NAME"));
//                new_entry.setRegistDate((Date)tmp_entry.get("REGIST_DATE"));
//                new_entry.setRegistDate((Date)tmp_entry.get("CACHE_DATE"));
//                new_entry.setKeywordTable((String)tmp_entry.get("KEYWORD_TABLE"));
//                entries[count++] = new_entry;
//            }
//            return entries;
//        } catch (HyperDbException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    private static Webdocumententry[] selectWithAddress(Connection con,String url){
//        try {
//            WebDataDBManager webManager = new WebDataDBManager();
//            Hashtable input0 = new Hashtable();
//            input0.put("address",url);
//            List list = webManager.selectWithAddress(con,input0,null,null);
//            Iterator itr = list.iterator();
//            Webdocumententry entries[] = new Webdocumententry[list.size()];
//            int count=0;
//            while(itr.hasNext()){
//                Hashtable tmp_entry = (Hashtable)itr.next();
//                Webdocumententry new_entry = new Webdocumententry();
//                new_entry.setTitle((String)tmp_entry.get("TITLE"));
//                new_entry.setUrl((String)tmp_entry.get("URL"));
//                new_entry.setKeywordTable((String)tmp_entry.get("KEYWORD_TABLE"));
//                new_entry.setCategory((String)tmp_entry.get("CATEGORY"));
//                new_entry.setBookmarkCount((Integer)tmp_entry.get("BOOKMARK_COUNT"));
//                new_entry.setCrawleddate((Date)tmp_entry.get("CRAWLED_DATE"));
//                new_entry.setDescription((String)tmp_entry.get("DESCRIPTION"));
//                new_entry.setViewUserCount((Integer)tmp_entry.get("VIEW_USER_COUNT"));
//                new_entry.setTagTable((String)tmp_entry.get("TAG_TABLE"));
//                entries[count++] = new_entry;
//            }
//
//            return entries;
//        } catch (HyperDbException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    private static void deleteUser(Connection con ,String id){
        try {
            UserDBManager userManager = new UserDBManager();
            Hashtable input0 = new Hashtable();
            input0.put("id",id);
            userManager.deleteUserWithID(con,input0);
        } catch (HyperDbException e) {
            e.printStackTrace();
        }
    }

    private static void deleteEntry(Connection con,String url){
        try {
            WebDataDBManager webManager = new WebDataDBManager();
            Hashtable input0 = new Hashtable();
            input0.put("address",url);
            webManager.deleteEntryWithAddress(con,input0);
        } catch (HyperDbException e) {
            e.printStackTrace();
        }
    }

    private static void createEntry(Connection con,String title,String url,int bookmark_count,int view_user_count,Date crawled_date,String category,String description){
        try {
            WebDataDBManager webDataManager = new WebDataDBManager();
            Hashtable input0 = new Hashtable();
            input0.put("title",title);
            input0.put("url",url);
            input0.put("bookmark_count",new Integer(bookmark_count));
            input0.put("view_user_count",new Integer(view_user_count));
            input0.put("crawledDate",crawled_date);
            input0.put("category",category);
            input0.put("Description",description);
            input0.put("keyword_table",url);
            input0.put("tag_table",url);
            webDataManager.insertNewEntry(con, input0);
        } catch (HyperDbException e) {
            e.printStackTrace();
        }
    }

    private static void createUser(Connection con,String id,String password,String name,String mail_address,int age,Date registDate){
        try {
            UserDBManager userManager = new UserDBManager();
            Hashtable input0 = new Hashtable();
            input0.put("id", id);
            input0.put("password",password);
            input0.put("name", name);
            input0.put("mail_address", mail_address);
            input0.put("age",new Integer(age));
            input0.put("regist_date",registDate);
            input0.put("cache_date",registDate);

            input0.put("keyword_table",id);
            userManager.insertNewUser(con, input0);
        } catch (HyperDbException e) {
            e.printStackTrace();
        }
    }

	public static void main(String[] args) {

        try {
            sampleMain2();
//            showFetchSize();

        } catch (HyperDbException e) {
            e.printStackTrace();
        }
//		Connection con = null;
//		try {
//			con = getConnection();
//			sampleMain2(con);
//			con.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

    private static Connection getConnection() throws HyperDbException{
		SQLExecutor exec = new SQLExecutor();
		configPostgreSQL(exec);
		return exec.getCon();
	}

	protected static void configPostgreSQL(SQLExecutor exec) throws HyperDbException{
		//exec.configConnection("org.postgresql.Driver", // Driver Class Name
		//		"jdbc:postgresql://localhost/TEST",    // URL
		//		"postgres", "password");                // USER AND PASSWORD

	}
}
