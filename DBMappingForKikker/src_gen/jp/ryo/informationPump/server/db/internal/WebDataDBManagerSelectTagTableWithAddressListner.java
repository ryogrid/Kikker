package jp.ryo.informationPump.server.db.internal;

import java.util.List;
import java.util.Hashtable;
import java.util.Iterator;
import jp.ryo.informationPump.server.db.tables.WebdocumentTagTable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.LinkedList;
import jp.ryo.informationPump.server.db.results.webdatadbmanager.SelectTagTableWithAddressResult;
import jp.crossfire.framework.database.ISelectResultListener;

public class WebDataDBManagerSelectTagTableWithAddressListner implements ISelectResultListener {
	public List result = new LinkedList();
	public Hashtable tables = new Hashtable();
	public List webdocumentTagTableTmpCache = new LinkedList();
	public  WebDataDBManagerSelectTagTableWithAddressListner() {
		Hashtable hash = null;
		hash = new Hashtable();
		this.tables.put("WebDocument_tag_table", hash);
	}
	public void orMap() {
		Iterator it = this.result.iterator();
		while(it.hasNext()){
			// For each result record
			SelectTagTableWithAddressResult data = (SelectTagTableWithAddressResult)it.next();
			WebdocumentTagTable webdocumentTagTable = (WebdocumentTagTable)data.getWebdocumentTagTable();
			if(webdocumentTagTable != null && !this.webdocumentTagTableTmpCache.contains(webdocumentTagTable)){
				this.webdocumentTagTableTmpCache.add(webdocumentTagTable);
			}
		}
		// CREATE FOREIGN KEY VALUE HASH
		// Setup references
	}
	public void getResult(ResultSet result, ResultSetMetaData meta) throws Exception {
		int maxCols = meta.getColumnCount();
		List tableColSettedList= new LinkedList();
		SelectTagTableWithAddressResult data = new SelectTagTableWithAddressResult();
		for(int i = 0; i < maxCols; i++){
			if(meta.getColumnName(i+1) == null){continue;}
		
			if(result.getObject(i+1) != null){
				data.put(meta.getColumnName(i+1).toUpperCase(), result.getObject(i+1));
			}
			String columnName = meta.getColumnName(i+1);
			if(WebdocumentTagTable.hasColumn(columnName)){
				if(columnName.toUpperCase().equals("doc_address".toUpperCase())
		                   && !tableColSettedList.contains("WebDocument_tag_table.doc_address")){
					tableColSettedList.add("WebDocument_tag_table.doc_address");
					if(result.getObject(i+1) != null){
						data.getWebdocumentTagTableWithNew().setDocAddress(new java.lang.String(result.getString(i+1)));
					}
					continue;
				}
				if(columnName.toUpperCase().equals("tag".toUpperCase())
		                   && !tableColSettedList.contains("WebDocument_tag_table.tag")){
					tableColSettedList.add("WebDocument_tag_table.tag");
					if(result.getObject(i+1) != null){
						data.getWebdocumentTagTableWithNew().setTag(new java.lang.String(result.getString(i+1)));
					}
					continue;
				}
				if(columnName.toUpperCase().equals("tfidf_value".toUpperCase())
		                   && !tableColSettedList.contains("WebDocument_tag_table.tfidf_value")){
					tableColSettedList.add("WebDocument_tag_table.tfidf_value");
					if(result.getObject(i+1) != null){
						data.getWebdocumentTagTableWithNew().setTfidfValue(new java.lang.Double(result.getString(i+1)));
					}
					continue;
				}
			}
		}
		tableColSettedList.clear();
		this.result.add(data);
	}
}
