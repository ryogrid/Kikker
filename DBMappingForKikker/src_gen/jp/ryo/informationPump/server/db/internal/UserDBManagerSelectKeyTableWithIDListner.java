package jp.ryo.informationPump.server.db.internal;

import java.util.List;
import java.util.Hashtable;
import java.util.Iterator;
import jp.ryo.informationPump.server.db.tables.UserKeywordTable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.LinkedList;
import jp.ryo.informationPump.server.db.results.userdbmanager.SelectKeyTableWithIDResult;
import jp.crossfire.framework.database.ISelectResultListener;

public class UserDBManagerSelectKeyTableWithIDListner implements ISelectResultListener {
	public List result = new LinkedList();
	public Hashtable tables = new Hashtable();
	public List userKeywordTableTmpCache = new LinkedList();
	public  UserDBManagerSelectKeyTableWithIDListner() {
		Hashtable hash = null;
		hash = new Hashtable();
		this.tables.put("User_keyword_table", hash);
	}
	public void orMap() {
		Iterator it = this.result.iterator();
		while(it.hasNext()){
			// For each result record
			SelectKeyTableWithIDResult data = (SelectKeyTableWithIDResult)it.next();
			UserKeywordTable userKeywordTable = (UserKeywordTable)data.getUserKeywordTable();
			if(userKeywordTable != null && !this.userKeywordTableTmpCache.contains(userKeywordTable)){
				this.userKeywordTableTmpCache.add(userKeywordTable);
			}
		}
		// CREATE FOREIGN KEY VALUE HASH
		// Setup references
	}
	public void getResult(ResultSet result, ResultSetMetaData meta) throws Exception {
		int maxCols = meta.getColumnCount();
		List tableColSettedList= new LinkedList();
		SelectKeyTableWithIDResult data = new SelectKeyTableWithIDResult();
		for(int i = 0; i < maxCols; i++){
			if(meta.getColumnName(i+1) == null){continue;}
		
			if(result.getObject(i+1) != null){
				data.put(meta.getColumnName(i+1).toUpperCase(), result.getObject(i+1));
			}
			String columnName = meta.getColumnName(i+1);
			if(UserKeywordTable.hasColumn(columnName)){
				if(columnName.toUpperCase().equals("user_id".toUpperCase())
		                   && !tableColSettedList.contains("User_keyword_table.user_id")){
					tableColSettedList.add("User_keyword_table.user_id");
					if(result.getObject(i+1) != null){
						data.getUserKeywordTableWithNew().setUserId(new java.lang.String(result.getString(i+1)));
					}
					continue;
				}
				if(columnName.toUpperCase().equals("keyword".toUpperCase())
		                   && !tableColSettedList.contains("User_keyword_table.keyword")){
					tableColSettedList.add("User_keyword_table.keyword");
					if(result.getObject(i+1) != null){
						data.getUserKeywordTableWithNew().setKeyword(new java.lang.String(result.getString(i+1)));
					}
					continue;
				}
				if(columnName.toUpperCase().equals("tfidf_value".toUpperCase())
		                   && !tableColSettedList.contains("User_keyword_table.tfidf_value")){
					tableColSettedList.add("User_keyword_table.tfidf_value");
					if(result.getObject(i+1) != null){
						data.getUserKeywordTableWithNew().setTfidfValue(new java.lang.Double(result.getString(i+1)));
					}
					continue;
				}
			}
		}
		tableColSettedList.clear();
		this.result.add(data);
	}
}
