package jp.ryo.informationPump.server.db.internal;

import java.util.List;
import java.util.Hashtable;
import java.util.Iterator;
import jp.ryo.informationPump.server.db.tables.Userentry;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.LinkedList;
import jp.ryo.informationPump.server.db.results.userdbmanager.SelectAllResult;
import jp.crossfire.framework.database.ISelectResultListener;

public class UserDBManagerSelectAllListner implements ISelectResultListener {
	public List result = new LinkedList();
	public Hashtable tables = new Hashtable();
	public List userentryTmpCache = new LinkedList();
	public  UserDBManagerSelectAllListner() {
		Hashtable hash = null;
		hash = new Hashtable();
		this.tables.put("UserEntry", hash);
	}
	public void orMap() {
		Iterator it = this.result.iterator();
		while(it.hasNext()){
			// For each result record
			SelectAllResult data = (SelectAllResult)it.next();
			Userentry userentry = (Userentry)data.getUserentry();
			if(userentry != null && !this.userentryTmpCache.contains(userentry)){
				this.userentryTmpCache.add(userentry);
			}
		}
		// CREATE FOREIGN KEY VALUE HASH
		// Setup references
	}
	public void getResult(ResultSet result, ResultSetMetaData meta) throws Exception {
		int maxCols = meta.getColumnCount();
		List tableColSettedList= new LinkedList();
		SelectAllResult data = new SelectAllResult();
		for(int i = 0; i < maxCols; i++){
			if(meta.getColumnName(i+1) == null){continue;}
		
			if(result.getObject(i+1) != null){
				data.put(meta.getColumnName(i+1).toUpperCase(), result.getObject(i+1));
			}
			String columnName = meta.getColumnName(i+1);
			if(Userentry.hasColumn(columnName)){
				if(columnName.toUpperCase().equals("id".toUpperCase())
		                   && !tableColSettedList.contains("UserEntry.id")){
					tableColSettedList.add("UserEntry.id");
					if(result.getObject(i+1) != null){
						data.getUserentryWithNew().setId(new java.lang.String(result.getString(i+1)));
					}
					continue;
				}
				if(columnName.toUpperCase().equals("password".toUpperCase())
		                   && !tableColSettedList.contains("UserEntry.password")){
					tableColSettedList.add("UserEntry.password");
					if(result.getObject(i+1) != null){
						data.getUserentryWithNew().setPassword(new java.lang.String(result.getString(i+1)));
					}
					continue;
				}
				if(columnName.toUpperCase().equals("name".toUpperCase())
		                   && !tableColSettedList.contains("UserEntry.name")){
					tableColSettedList.add("UserEntry.name");
					if(result.getObject(i+1) != null){
						data.getUserentryWithNew().setName(new java.lang.String(result.getString(i+1)));
					}
					continue;
				}
				if(columnName.toUpperCase().equals("mail_address".toUpperCase())
		                   && !tableColSettedList.contains("UserEntry.mail_address")){
					tableColSettedList.add("UserEntry.mail_address");
					if(result.getObject(i+1) != null){
						data.getUserentryWithNew().setMailAddress(new java.lang.String(result.getString(i+1)));
					}
					continue;
				}
				if(columnName.toUpperCase().equals("age".toUpperCase())
		                   && !tableColSettedList.contains("UserEntry.age")){
					tableColSettedList.add("UserEntry.age");
					if(result.getObject(i+1) != null){
						data.getUserentryWithNew().setAge(new java.lang.Integer(result.getString(i+1)));
					}
					continue;
				}
				if(columnName.toUpperCase().equals("regist_date".toUpperCase())
		                   && !tableColSettedList.contains("UserEntry.regist_date")){
					tableColSettedList.add("UserEntry.regist_date");
					if(result.getObject(i+1) != null){
						data.getUserentryWithNew().setRegistDate(result.getDate(i+1));
					}
					continue;
				}
				if(columnName.toUpperCase().equals("cache_date".toUpperCase())
		                   && !tableColSettedList.contains("UserEntry.cache_date")){
					tableColSettedList.add("UserEntry.cache_date");
					if(result.getObject(i+1) != null){
						data.getUserentryWithNew().setCacheDate(result.getDate(i+1));
					}
					continue;
				}
			}
		}
		tableColSettedList.clear();
		this.result.add(data);
	}
}
