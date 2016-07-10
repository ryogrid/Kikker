package jp.ryo.informationPump.server.db.internal;

import java.util.List;
import java.util.Hashtable;
import java.util.Iterator;
import jp.ryo.informationPump.server.db.tables.Userviewedentries;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.LinkedList;
import jp.ryo.informationPump.server.db.results.userdbmanager.SelectUserViewedURLResult;
import jp.crossfire.framework.database.ISelectResultListener;

public class UserDBManagerSelectUserViewedURLListner implements ISelectResultListener {
	public List result = new LinkedList();
	public Hashtable tables = new Hashtable();
	public List userviewedentriesTmpCache = new LinkedList();
	public  UserDBManagerSelectUserViewedURLListner() {
		Hashtable hash = null;
		hash = new Hashtable();
		this.tables.put("UserViewedEntries", hash);
	}
	public void orMap() {
		Iterator it = this.result.iterator();
		while(it.hasNext()){
			// For each result record
			SelectUserViewedURLResult data = (SelectUserViewedURLResult)it.next();
			Userviewedentries userviewedentries = (Userviewedentries)data.getUserviewedentries();
			if(userviewedentries != null && !this.userviewedentriesTmpCache.contains(userviewedentries)){
				this.userviewedentriesTmpCache.add(userviewedentries);
			}
		}
		// CREATE FOREIGN KEY VALUE HASH
		// Setup references
	}
	public void getResult(ResultSet result, ResultSetMetaData meta) throws Exception {
		int maxCols = meta.getColumnCount();
		List tableColSettedList= new LinkedList();
		SelectUserViewedURLResult data = new SelectUserViewedURLResult();
		for(int i = 0; i < maxCols; i++){
			if(meta.getColumnName(i+1) == null){continue;}
		
			if(result.getObject(i+1) != null){
				data.put(meta.getColumnName(i+1).toUpperCase(), result.getObject(i+1));
			}
			String columnName = meta.getColumnName(i+1);
			if(Userviewedentries.hasColumn(columnName)){
				if(columnName.toUpperCase().equals("id".toUpperCase())
		                   && !tableColSettedList.contains("UserViewedEntries.id")){
					tableColSettedList.add("UserViewedEntries.id");
					if(result.getObject(i+1) != null){
						data.getUserviewedentriesWithNew().setId(new java.lang.String(result.getString(i+1)));
					}
					continue;
				}
				if(columnName.toUpperCase().equals("doc_url".toUpperCase())
		                   && !tableColSettedList.contains("UserViewedEntries.doc_url")){
					tableColSettedList.add("UserViewedEntries.doc_url");
					if(result.getObject(i+1) != null){
						data.getUserviewedentriesWithNew().setDocUrl(new java.lang.String(result.getString(i+1)));
					}
					continue;
				}
			}
		}
		tableColSettedList.clear();
		this.result.add(data);
	}
}
