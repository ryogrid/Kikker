package jp.ryo.informationPump.server.db.internal;

import java.util.List;
import java.util.Hashtable;
import java.util.Iterator;
import jp.ryo.informationPump.server.db.tables.WebdocumentKeywordTable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.LinkedList;
import jp.ryo.informationPump.server.db.results.webdatadbmanager.SelectKeyTableWithAddressResult;
import jp.crossfire.framework.database.ISelectResultListener;

public class WebDataDBManagerSelectKeyTableWithAddressListner implements ISelectResultListener {
	public List result = new LinkedList();
	public Hashtable tables = new Hashtable();
	public List webdocumentKeywordTableTmpCache = new LinkedList();
	public  WebDataDBManagerSelectKeyTableWithAddressListner() {
		Hashtable hash = null;
		hash = new Hashtable();
		this.tables.put("WebDocument_keyword_table", hash);
	}
	public void orMap() {
		Iterator it = this.result.iterator();
		while(it.hasNext()){
			// For each result record
			SelectKeyTableWithAddressResult data = (SelectKeyTableWithAddressResult)it.next();
			WebdocumentKeywordTable webdocumentKeywordTable = (WebdocumentKeywordTable)data.getWebdocumentKeywordTable();
			if(webdocumentKeywordTable != null && !this.webdocumentKeywordTableTmpCache.contains(webdocumentKeywordTable)){
				this.webdocumentKeywordTableTmpCache.add(webdocumentKeywordTable);
			}
		}
		// CREATE FOREIGN KEY VALUE HASH
		// Setup references
	}
	public void getResult(ResultSet result, ResultSetMetaData meta) throws Exception {
		int maxCols = meta.getColumnCount();
		List tableColSettedList= new LinkedList();
		SelectKeyTableWithAddressResult data = new SelectKeyTableWithAddressResult();
		for(int i = 0; i < maxCols; i++){
			if(meta.getColumnName(i+1) == null){continue;}
		
			if(result.getObject(i+1) != null){
				data.put(meta.getColumnName(i+1).toUpperCase(), result.getObject(i+1));
			}
			String columnName = meta.getColumnName(i+1);
			if(WebdocumentKeywordTable.hasColumn(columnName)){
				if(columnName.toUpperCase().equals("doc_address".toUpperCase())
		                   && !tableColSettedList.contains("WebDocument_keyword_table.doc_address")){
					tableColSettedList.add("WebDocument_keyword_table.doc_address");
					if(result.getObject(i+1) != null){
						data.getWebdocumentKeywordTableWithNew().setDocAddress(new java.lang.String(result.getString(i+1)));
					}
					continue;
				}
				if(columnName.toUpperCase().equals("keyword".toUpperCase())
		                   && !tableColSettedList.contains("WebDocument_keyword_table.keyword")){
					tableColSettedList.add("WebDocument_keyword_table.keyword");
					if(result.getObject(i+1) != null){
						data.getWebdocumentKeywordTableWithNew().setKeyword(new java.lang.String(result.getString(i+1)));
					}
					continue;
				}
				if(columnName.toUpperCase().equals("tfidf_value".toUpperCase())
		                   && !tableColSettedList.contains("WebDocument_keyword_table.tfidf_value")){
					tableColSettedList.add("WebDocument_keyword_table.tfidf_value");
					if(result.getObject(i+1) != null){
						data.getWebdocumentKeywordTableWithNew().setTfidfValue(new java.lang.Double(result.getString(i+1)));
					}
					continue;
				}
			}
		}
		tableColSettedList.clear();
		this.result.add(data);
	}
}
