package jp.ryo.informationPump.server.db.internal;

import java.util.List;
import java.util.Hashtable;
import java.util.Iterator;
import jp.ryo.informationPump.server.db.tables.Webdocumententry;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.LinkedList;
import jp.ryo.informationPump.server.db.results.webdatadbmanager.SelectAllResult;
import jp.crossfire.framework.database.ISelectResultListener;

public class WebDataDBManagerSelectAllListner implements ISelectResultListener {
	public List result = new LinkedList();
	public Hashtable tables = new Hashtable();
	public List webdocumententryTmpCache = new LinkedList();
	public  WebDataDBManagerSelectAllListner() {
		Hashtable hash = null;
		hash = new Hashtable();
		this.tables.put("WebDocumentEntry", hash);
	}
	public void orMap() {
		Iterator it = this.result.iterator();
		while(it.hasNext()){
			// For each result record
			SelectAllResult data = (SelectAllResult)it.next();
			Webdocumententry webdocumententry = (Webdocumententry)data.getWebdocumententry();
			if(webdocumententry != null && !this.webdocumententryTmpCache.contains(webdocumententry)){
				this.webdocumententryTmpCache.add(webdocumententry);
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
			if(Webdocumententry.hasColumn(columnName)){
				if(columnName.toUpperCase().equals("doc_type".toUpperCase())
		                   && !tableColSettedList.contains("WebDocumentEntry.doc_type")){
					tableColSettedList.add("WebDocumentEntry.doc_type");
					if(result.getObject(i+1) != null){
						data.getWebdocumententryWithNew().setDocType(new java.lang.Integer(result.getString(i+1)));
					}
					continue;
				}
				if(columnName.toUpperCase().equals("title".toUpperCase())
		                   && !tableColSettedList.contains("WebDocumentEntry.title")){
					tableColSettedList.add("WebDocumentEntry.title");
					if(result.getObject(i+1) != null){
						data.getWebdocumententryWithNew().setTitle(new java.lang.String(result.getString(i+1)));
					}
					continue;
				}
				if(columnName.toUpperCase().equals("url".toUpperCase())
		                   && !tableColSettedList.contains("WebDocumentEntry.url")){
					tableColSettedList.add("WebDocumentEntry.url");
					if(result.getObject(i+1) != null){
						data.getWebdocumententryWithNew().setUrl(new java.lang.String(result.getString(i+1)));
					}
					continue;
				}
				if(columnName.toUpperCase().equals("bookmark_count".toUpperCase())
		                   && !tableColSettedList.contains("WebDocumentEntry.bookmark_count")){
					tableColSettedList.add("WebDocumentEntry.bookmark_count");
					if(result.getObject(i+1) != null){
						data.getWebdocumententryWithNew().setBookmarkCount(new java.lang.Integer(result.getString(i+1)));
					}
					continue;
				}
				if(columnName.toUpperCase().equals("view_user_count".toUpperCase())
		                   && !tableColSettedList.contains("WebDocumentEntry.view_user_count")){
					tableColSettedList.add("WebDocumentEntry.view_user_count");
					if(result.getObject(i+1) != null){
						data.getWebdocumententryWithNew().setViewUserCount(new java.lang.Integer(result.getString(i+1)));
					}
					continue;
				}
				if(columnName.toUpperCase().equals("crawledDate".toUpperCase())
		                   && !tableColSettedList.contains("WebDocumentEntry.crawledDate")){
					tableColSettedList.add("WebDocumentEntry.crawledDate");
					if(result.getObject(i+1) != null){
						data.getWebdocumententryWithNew().setCrawleddate(result.getDate(i+1));
					}
					continue;
				}
				if(columnName.toUpperCase().equals("category".toUpperCase())
		                   && !tableColSettedList.contains("WebDocumentEntry.category")){
					tableColSettedList.add("WebDocumentEntry.category");
					if(result.getObject(i+1) != null){
						data.getWebdocumententryWithNew().setCategory(new java.lang.String(result.getString(i+1)));
					}
					continue;
				}
				if(columnName.toUpperCase().equals("Description".toUpperCase())
		                   && !tableColSettedList.contains("WebDocumentEntry.Description")){
					tableColSettedList.add("WebDocumentEntry.Description");
					if(result.getObject(i+1) != null){
						data.getWebdocumententryWithNew().setDescription(new java.lang.String(result.getString(i+1)));
					}
					continue;
				}
				if(columnName.toUpperCase().equals("isAnalyzed".toUpperCase())
		                   && !tableColSettedList.contains("WebDocumentEntry.isAnalyzed")){
					tableColSettedList.add("WebDocumentEntry.isAnalyzed");
					if(result.getObject(i+1) != null){
						data.getWebdocumententryWithNew().setIsanalyzed(new java.lang.Integer(result.getString(i+1)));
					}
					continue;
				}
			}
		}
		tableColSettedList.clear();
		this.result.add(data);
	}
}
