package jp.ryo.informationPump.server.db;

import java.util.List;
import java.util.LinkedList;
import java.util.Hashtable;
import java.lang.Integer;
import java.lang.StringBuffer;
import jp.crossfire.framework.database.ICondition;
import java.sql.Connection;
import jp.crossfire.framework.database.SQLExecutor;
import jp.ryo.informationPump.server.db.internal.WebDataDBManagerSelectDocumentsWithKeyAndDateListner;
import jp.ryo.informationPump.server.db.internal.WebDataDBManagerSelectEntriesBeforeADateListner;
import jp.ryo.informationPump.server.db.internal.WebDataDBManagerSelectEntriesAfterADateListner;
import jp.ryo.informationPump.server.db.internal.WebDataDBManagerSelectDocumentsWithKeyListner;
import jp.crossfire.framework.database.ConditionContainer;
import jp.crossfire.framework.database.Values;
import jp.crossfire.framework.database.Condition;
import jp.ryo.informationPump.server.db.internal.WebDataDBManagerSelectTagTableWithAddressListner;
import jp.ryo.informationPump.server.db.internal.WebDataDBManagerSelectKeyTableWithAddressListner;
import jp.ryo.informationPump.server.db.internal.WebDataDBManagerSelectAllListner;
import jp.ryo.informationPump.server.db.internal.WebDataDBManagerSelectWithAddressListner;
import jp.ryo.informationPump.server.db.internal.WebDataDBManagerInternal;

public class WebDataDBManager extends WebDataDBManagerInternal {
	private List webdocumententry = new LinkedList();
	private List webdocumentTagTable = new LinkedList();
	private List webdocumentKeywordTable = new LinkedList();
	public List getWebdocumententry() {
		return this.webdocumententry;
	}
	public void setWebdocumententry(List webdocumententry) {
		this.webdocumententry = webdocumententry;
	}
	public List getWebdocumentTagTable() {
		return this.webdocumentTagTable;
	}
	public void setWebdocumentTagTable(List webdocumentTagTable) {
		this.webdocumentTagTable = webdocumentTagTable;
	}
	public List getWebdocumentKeywordTable() {
		return this.webdocumentKeywordTable;
	}
	public void setWebdocumentKeywordTable(List webdocumentKeywordTable) {
		this.webdocumentKeywordTable = webdocumentKeywordTable;
	}
	public String selectDocumentsWithKeyAndDateSql(Hashtable input, Integer limit, Integer offset) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("SELECT ");
		buf.append("*");
		
		buf.append(" FROM ");
		buf.append("WebDocumentEntry");
		
		// Condition
		ICondition condition = selectDocumentsWithKeyAndDateGetCondition(input);
		if(condition != null && !condition.toString().equals("")){
			buf.append(" WHERE " + condition.toString());
		}
		
		// GROUP BY
		
		// Having Condition
		condition = selectDocumentsWithKeyAndDateGetHavingCondition(input);
		if(condition != null && !condition.toString().equals("")){
			buf.append(" HAVING " + condition.toString());
		}
		
		// ORDER
		
		// Limit
		if(limit != null && offset != null){
			buf.append(" LIMIT " + offset.toString() +", " + limit.toString());
		}
		
		return buf.toString();
	}
	public List selectDocumentsWithKeyAndDate(Connection con, Hashtable input, Integer limit, Integer offset) throws jp.crossfire.framework.database.HyperDbException {
		this.reset();
		String sql = selectDocumentsWithKeyAndDateSql(input, limit, offset);
		SQLExecutor exec = new SQLExecutor();
		exec.setCon(con);
		
		// Listners
		List listners = new LinkedList();
		
		WebDataDBManagerSelectDocumentsWithKeyAndDateListner listner0 = (new WebDataDBManagerSelectDocumentsWithKeyAndDateListner());
		listners.add(listner0);
		
		// Execute Query
		exec.executeSelectSQL(sql, listners);
		listner0.orMap();
		
		this.webdocumententry = listner0.webdocumententryTmpCache;
		return listner0.result;
	}
	public String selectEntriesBeforeADateSql(Hashtable input, Integer limit, Integer offset) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("SELECT ");
		buf.append("*");
		
		buf.append(" FROM ");
		buf.append("WebDocumentEntry");
		
		// Condition
		ICondition condition = selectEntriesBeforeADateGetCondition(input);
		if(condition != null && !condition.toString().equals("")){
			buf.append(" WHERE " + condition.toString());
		}
		
		// GROUP BY
		
		// Having Condition
		condition = selectEntriesBeforeADateGetHavingCondition(input);
		if(condition != null && !condition.toString().equals("")){
			buf.append(" HAVING " + condition.toString());
		}
		
		// ORDER
		
		// Limit
		if(limit != null && offset != null){
			buf.append(" LIMIT " + offset.toString() +", " + limit.toString());
		}
		
		return buf.toString();
	}
	public List selectEntriesBeforeADate(Connection con, Hashtable input, Integer limit, Integer offset) throws jp.crossfire.framework.database.HyperDbException {
		this.reset();
		String sql = selectEntriesBeforeADateSql(input, limit, offset);
		SQLExecutor exec = new SQLExecutor();
		exec.setCon(con);
		
		// Listners
		List listners = new LinkedList();
		
		WebDataDBManagerSelectEntriesBeforeADateListner listner0 = (new WebDataDBManagerSelectEntriesBeforeADateListner());
		listners.add(listner0);
		
		// Execute Query
		exec.executeSelectSQL(sql, listners);
		listner0.orMap();
		
		this.webdocumententry = listner0.webdocumententryTmpCache;
		return listner0.result;
	}
	public String deleteEntriesBeforeADateSql(Hashtable input) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("DELETE FROM ");
		buf.append("WebDocumentEntry");
		
		// Condition
		ICondition condition = deleteEntriesBeforeADateGetCondition(input);
		if(condition != null && !condition.toString().equals("")){
			buf.append(" WHERE " + condition.toString());
		}
		
		return buf.toString();
	}
	public void deleteEntriesBeforeADate(Connection con, Hashtable input) throws jp.crossfire.framework.database.HyperDbException {
		String sql = deleteEntriesBeforeADateSql(input);
		SQLExecutor exec = new SQLExecutor();
		exec.setCon(con);
		
		exec.execUpdateSQL(sql);
	}
	public String selectEntriesAfterADateSql(Hashtable input, Integer limit, Integer offset) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("SELECT ");
		buf.append("*");
		
		buf.append(" FROM ");
		buf.append("WebDocumentEntry");
		
		// Condition
		ICondition condition = selectEntriesAfterADateGetCondition(input);
		if(condition != null && !condition.toString().equals("")){
			buf.append(" WHERE " + condition.toString());
		}
		
		// GROUP BY
		
		// Having Condition
		condition = selectEntriesAfterADateGetHavingCondition(input);
		if(condition != null && !condition.toString().equals("")){
			buf.append(" HAVING " + condition.toString());
		}
		
		// ORDER
		
		// Limit
		if(limit != null && offset != null){
			buf.append(" LIMIT " + offset.toString() +", " + limit.toString());
		}
		
		return buf.toString();
	}
	public List selectEntriesAfterADate(Connection con, Hashtable input, Integer limit, Integer offset) throws jp.crossfire.framework.database.HyperDbException {
		this.reset();
		String sql = selectEntriesAfterADateSql(input, limit, offset);
		SQLExecutor exec = new SQLExecutor();
		exec.setCon(con);
		
		// Listners
		List listners = new LinkedList();
		
		WebDataDBManagerSelectEntriesAfterADateListner listner0 = (new WebDataDBManagerSelectEntriesAfterADateListner());
		listners.add(listner0);
		
		// Execute Query
		exec.executeSelectSQL(sql, listners);
		listner0.orMap();
		
		this.webdocumententry = listner0.webdocumententryTmpCache;
		return listner0.result;
	}
	public String selectDocumentsWithKeySql(Hashtable input, Integer limit, Integer offset) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("SELECT ");
		buf.append("*");
		
		buf.append(" FROM ");
		buf.append("WebDocumentEntry");
		
		// Condition
		ICondition condition = selectDocumentsWithKeyGetCondition(input);
		if(condition != null && !condition.toString().equals("")){
			buf.append(" WHERE " + condition.toString());
		}
		
		// GROUP BY
		
		// Having Condition
		condition = selectDocumentsWithKeyGetHavingCondition(input);
		if(condition != null && !condition.toString().equals("")){
			buf.append(" HAVING " + condition.toString());
		}
		
		// ORDER
		
		// Limit
		if(limit != null && offset != null){
			buf.append(" LIMIT " + offset.toString() +", " + limit.toString());
		}
		
		return buf.toString();
	}
	public List selectDocumentsWithKey(Connection con, Hashtable input, Integer limit, Integer offset) throws jp.crossfire.framework.database.HyperDbException {
		this.reset();
		String sql = selectDocumentsWithKeySql(input, limit, offset);
		SQLExecutor exec = new SQLExecutor();
		exec.setCon(con);
		
		// Listners
		List listners = new LinkedList();
		
		WebDataDBManagerSelectDocumentsWithKeyListner listner0 = (new WebDataDBManagerSelectDocumentsWithKeyListner());
		listners.add(listner0);
		
		// Execute Query
		exec.executeSelectSQL(sql, listners);
		listner0.orMap();
		
		this.webdocumententry = listner0.webdocumententryTmpCache;
		return listner0.result;
	}
	public String insertNewTagSql(Hashtable input) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("INSERT INTO ");
		buf.append("WebDocument_tag_table");
		
		ConditionContainer cnd0= new Values("VALUES");
		ConditionContainer cnd1 = new ConditionContainer(",");
		ConditionContainer cnd2 = new ConditionContainer(",");
		cnd0.addChild(cnd1);
		cnd0.addChild(cnd2);
		Condition cnd3 = new Condition("doc_address");
		cnd1.addChild(cnd3);
		Condition cnd4 = new Condition("'$doc_address'");
		if(input.get("doc_address") != null){
			((Condition)cnd4).getProperties().put("doc_address", input.get("doc_address"));
		}
		cnd2.addChild(cnd4);
		Condition cnd5 = new Condition("tag");
		cnd1.addChild(cnd5);
		Condition cnd6 = new Condition("'$tag'");
		if(input.get("tag") != null){
			((Condition)cnd6).getProperties().put("tag", input.get("tag"));
		}
		cnd2.addChild(cnd6);
		Condition cnd7 = new Condition("tfidf_value");
		cnd1.addChild(cnd7);
		Condition cnd8 = new Condition("'$tfidf_value'");
		if(input.get("tfidf_value") != null){
			((Condition)cnd8).getProperties().put("tfidf_value", input.get("tfidf_value"));
		}
		cnd2.addChild(cnd8);
		if(!cnd0.toString().equals("")){
			buf.append(" " + cnd0.toString());
		}
		
		return buf.toString();
	}
	public void insertNewTag(Connection con, Hashtable input) throws jp.crossfire.framework.database.HyperDbException {
		String sql = insertNewTagSql(input);
		SQLExecutor exec = new SQLExecutor();
		exec.setCon(con);
		
		exec.execUpdateSQL(sql);
	}
	public String insertNewKeywordSql(Hashtable input) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("INSERT INTO ");
		buf.append("WebDocument_keyword_table");
		
		ConditionContainer cnd0= new Values("VALUES");
		ConditionContainer cnd1 = new ConditionContainer(",");
		ConditionContainer cnd2 = new ConditionContainer(",");
		cnd0.addChild(cnd1);
		cnd0.addChild(cnd2);
		Condition cnd3 = new Condition("doc_address");
		cnd1.addChild(cnd3);
		Condition cnd4 = new Condition("'$doc_address'");
		if(input.get("doc_address") != null){
			((Condition)cnd4).getProperties().put("doc_address", input.get("doc_address"));
		}
		cnd2.addChild(cnd4);
		Condition cnd5 = new Condition("keyword");
		cnd1.addChild(cnd5);
		Condition cnd6 = new Condition("'$keyword'");
		if(input.get("keyword") != null){
			((Condition)cnd6).getProperties().put("keyword", input.get("keyword"));
		}
		cnd2.addChild(cnd6);
		Condition cnd7 = new Condition("tfidf_value");
		cnd1.addChild(cnd7);
		Condition cnd8 = new Condition("'$tfidf_value'");
		if(input.get("tfidf_value") != null){
			((Condition)cnd8).getProperties().put("tfidf_value", input.get("tfidf_value"));
		}
		cnd2.addChild(cnd8);
		if(!cnd0.toString().equals("")){
			buf.append(" " + cnd0.toString());
		}
		
		return buf.toString();
	}
	public void insertNewKeyword(Connection con, Hashtable input) throws jp.crossfire.framework.database.HyperDbException {
		String sql = insertNewKeywordSql(input);
		SQLExecutor exec = new SQLExecutor();
		exec.setCon(con);
		
		exec.execUpdateSQL(sql);
	}
	public String selectTagTableWithAddressSql(Hashtable input, Integer limit, Integer offset) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("SELECT ");
		buf.append("*");
		
		buf.append(" FROM ");
		buf.append("WebDocument_tag_table");
		
		// Condition
		ICondition condition = selectTagTableWithAddressGetCondition(input);
		if(condition != null && !condition.toString().equals("")){
			buf.append(" WHERE " + condition.toString());
		}
		
		// GROUP BY
		
		// Having Condition
		condition = selectTagTableWithAddressGetHavingCondition(input);
		if(condition != null && !condition.toString().equals("")){
			buf.append(" HAVING " + condition.toString());
		}
		
		// ORDER
		buf.append(" ORDER BY ");
		buf.append("WebDocument_tag_table.tfidf_value");
		
		// Limit
		if(limit != null && offset != null){
			buf.append(" LIMIT " + offset.toString() +", " + limit.toString());
		}
		
		return buf.toString();
	}
	public List selectTagTableWithAddress(Connection con, Hashtable input, Integer limit, Integer offset) throws jp.crossfire.framework.database.HyperDbException {
		this.reset();
		String sql = selectTagTableWithAddressSql(input, limit, offset);
		SQLExecutor exec = new SQLExecutor();
		exec.setCon(con);
		
		// Listners
		List listners = new LinkedList();
		
		WebDataDBManagerSelectTagTableWithAddressListner listner0 = (new WebDataDBManagerSelectTagTableWithAddressListner());
		listners.add(listner0);
		
		// Execute Query
		exec.executeSelectSQL(sql, listners);
		listner0.orMap();
		
		this.webdocumentTagTable = listner0.webdocumentTagTableTmpCache;
		return listner0.result;
	}
	public String selectKeyTableWithAddressSql(Hashtable input, Integer limit, Integer offset) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("SELECT ");
		buf.append("*");
		
		buf.append(" FROM ");
		buf.append("WebDocument_keyword_table");
		
		// Condition
		ICondition condition = selectKeyTableWithAddressGetCondition(input);
		if(condition != null && !condition.toString().equals("")){
			buf.append(" WHERE " + condition.toString());
		}
		
		// GROUP BY
		
		// Having Condition
		condition = selectKeyTableWithAddressGetHavingCondition(input);
		if(condition != null && !condition.toString().equals("")){
			buf.append(" HAVING " + condition.toString());
		}
		
		// ORDER
		buf.append(" ORDER BY ");
		buf.append("WebDocument_keyword_table.tfidf_value");
		
		// Limit
		if(limit != null && offset != null){
			buf.append(" LIMIT " + offset.toString() +", " + limit.toString());
		}
		
		return buf.toString();
	}
	public List selectKeyTableWithAddress(Connection con, Hashtable input, Integer limit, Integer offset) throws jp.crossfire.framework.database.HyperDbException {
		this.reset();
		String sql = selectKeyTableWithAddressSql(input, limit, offset);
		SQLExecutor exec = new SQLExecutor();
		exec.setCon(con);
		
		// Listners
		List listners = new LinkedList();
		
		WebDataDBManagerSelectKeyTableWithAddressListner listner0 = (new WebDataDBManagerSelectKeyTableWithAddressListner());
		listners.add(listner0);
		
		// Execute Query
		exec.executeSelectSQL(sql, listners);
		listner0.orMap();
		
		this.webdocumentKeywordTable = listner0.webdocumentKeywordTableTmpCache;
		return listner0.result;
	}
	public String deleteTagWithAddressSql(Hashtable input) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("DELETE FROM ");
		buf.append("WebDocument_tag_table");
		
		// Condition
		ICondition condition = deleteTagWithAddressGetCondition(input);
		if(condition != null && !condition.toString().equals("")){
			buf.append(" WHERE " + condition.toString());
		}
		
		return buf.toString();
	}
	public void deleteTagWithAddress(Connection con, Hashtable input) throws jp.crossfire.framework.database.HyperDbException {
		String sql = deleteTagWithAddressSql(input);
		SQLExecutor exec = new SQLExecutor();
		exec.setCon(con);
		
		exec.execUpdateSQL(sql);
	}
	public String deleteKeywordsWithAddressSql(Hashtable input) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("DELETE FROM ");
		buf.append("WebDocument_keyword_table");
		
		// Condition
		ICondition condition = deleteKeywordsWithAddressGetCondition(input);
		if(condition != null && !condition.toString().equals("")){
			buf.append(" WHERE " + condition.toString());
		}
		
		return buf.toString();
	}
	public void deleteKeywordsWithAddress(Connection con, Hashtable input) throws jp.crossfire.framework.database.HyperDbException {
		String sql = deleteKeywordsWithAddressSql(input);
		SQLExecutor exec = new SQLExecutor();
		exec.setCon(con);
		
		exec.execUpdateSQL(sql);
	}
	public String deleteTagAllSql(Hashtable input) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("DELETE FROM ");
		buf.append("WebDocument_tag_table");
		
		// Condition
		ICondition condition = deleteTagAllGetCondition(input);
		if(condition != null && !condition.toString().equals("")){
			buf.append(" WHERE " + condition.toString());
		}
		
		return buf.toString();
	}
	public void deleteTagAll(Connection con, Hashtable input) throws jp.crossfire.framework.database.HyperDbException {
		String sql = deleteTagAllSql(input);
		SQLExecutor exec = new SQLExecutor();
		exec.setCon(con);
		
		exec.execUpdateSQL(sql);
	}
	public String deleteKeywordAllSql(Hashtable input) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("DELETE FROM ");
		buf.append("WebDocument_keyword_table");
		
		// Condition
		ICondition condition = deleteKeywordAllGetCondition(input);
		if(condition != null && !condition.toString().equals("")){
			buf.append(" WHERE " + condition.toString());
		}
		
		return buf.toString();
	}
	public void deleteKeywordAll(Connection con, Hashtable input) throws jp.crossfire.framework.database.HyperDbException {
		String sql = deleteKeywordAllSql(input);
		SQLExecutor exec = new SQLExecutor();
		exec.setCon(con);
		
		exec.execUpdateSQL(sql);
	}
	public String deleteDocumentAllSql(Hashtable input) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("DELETE FROM ");
		buf.append("WebDocumentEntry");
		
		// Condition
		ICondition condition = deleteDocumentAllGetCondition(input);
		if(condition != null && !condition.toString().equals("")){
			buf.append(" WHERE " + condition.toString());
		}
		
		return buf.toString();
	}
	public void deleteDocumentAll(Connection con, Hashtable input) throws jp.crossfire.framework.database.HyperDbException {
		String sql = deleteDocumentAllSql(input);
		SQLExecutor exec = new SQLExecutor();
		exec.setCon(con);
		
		exec.execUpdateSQL(sql);
	}
	public String selectAllSql(Hashtable input, Integer limit, Integer offset) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("SELECT ");
		buf.append("*");
		
		buf.append(" FROM ");
		buf.append("WebDocumentEntry");
		
		// Condition
		ICondition condition = selectAllGetCondition(input);
		if(condition != null && !condition.toString().equals("")){
			buf.append(" WHERE " + condition.toString());
		}
		
		// GROUP BY
		
		// Having Condition
		condition = selectAllGetHavingCondition(input);
		if(condition != null && !condition.toString().equals("")){
			buf.append(" HAVING " + condition.toString());
		}
		
		// ORDER
		
		// Limit
		if(limit != null && offset != null){
			buf.append(" LIMIT " + offset.toString() +", " + limit.toString());
		}
		
		return buf.toString();
	}
	public List selectAll(Connection con, Hashtable input, Integer limit, Integer offset) throws jp.crossfire.framework.database.HyperDbException {
		this.reset();
		String sql = selectAllSql(input, limit, offset);
		SQLExecutor exec = new SQLExecutor();
		exec.setCon(con);
		
		// Listners
		List listners = new LinkedList();
		
		WebDataDBManagerSelectAllListner listner0 = (new WebDataDBManagerSelectAllListner());
		listners.add(listner0);
		
		// Execute Query
		exec.executeSelectSQL(sql, listners);
		listner0.orMap();
		
		this.webdocumententry = listner0.webdocumententryTmpCache;
		return listner0.result;
	}
	public String insertNewEntrySql(Hashtable input) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("INSERT INTO ");
		buf.append("WebDocumentEntry");
		
		ConditionContainer cnd0= new Values("VALUES");
		ConditionContainer cnd1 = new ConditionContainer(",");
		ConditionContainer cnd2 = new ConditionContainer(",");
		cnd0.addChild(cnd1);
		cnd0.addChild(cnd2);
		Condition cnd3 = new Condition("doc_type");
		cnd1.addChild(cnd3);
		Condition cnd4 = new Condition("'$doc_type'");
		if(input.get("doc_type") != null){
			((Condition)cnd4).getProperties().put("doc_type", input.get("doc_type"));
		}
		cnd2.addChild(cnd4);
		Condition cnd5 = new Condition("title");
		cnd1.addChild(cnd5);
		Condition cnd6 = new Condition("'$title'");
		if(input.get("title") != null){
			((Condition)cnd6).getProperties().put("title", input.get("title"));
		}
		cnd2.addChild(cnd6);
		Condition cnd7 = new Condition("url");
		cnd1.addChild(cnd7);
		Condition cnd8 = new Condition("'$url'");
		if(input.get("url") != null){
			((Condition)cnd8).getProperties().put("url", input.get("url"));
		}
		cnd2.addChild(cnd8);
		Condition cnd9 = new Condition("bookmark_count");
		cnd1.addChild(cnd9);
		Condition cnd10 = new Condition("'$bookmark_count'");
		if(input.get("bookmark_count") != null){
			((Condition)cnd10).getProperties().put("bookmark_count", input.get("bookmark_count"));
		}
		cnd2.addChild(cnd10);
		Condition cnd11 = new Condition("view_user_count");
		cnd1.addChild(cnd11);
		Condition cnd12 = new Condition("'$view_user_count'");
		if(input.get("view_user_count") != null){
			((Condition)cnd12).getProperties().put("view_user_count", input.get("view_user_count"));
		}
		cnd2.addChild(cnd12);
		Condition cnd13 = new Condition("crawledDate");
		cnd1.addChild(cnd13);
		Condition cnd14 = new Condition("'$crawledDate'");
		if(input.get("crawledDate") != null){
			((Condition)cnd14).getProperties().put("crawledDate", input.get("crawledDate"));
		}
		cnd2.addChild(cnd14);
		Condition cnd15 = new Condition("category");
		cnd1.addChild(cnd15);
		Condition cnd16 = new Condition("'$category'");
		if(input.get("category") != null){
			((Condition)cnd16).getProperties().put("category", input.get("category"));
		}
		cnd2.addChild(cnd16);
		Condition cnd17 = new Condition("Description");
		cnd1.addChild(cnd17);
		Condition cnd18 = new Condition("'$Description'");
		if(input.get("Description") != null){
			((Condition)cnd18).getProperties().put("Description", input.get("Description"));
		}
		cnd2.addChild(cnd18);
		Condition cnd19 = new Condition("isAnalyzed");
		cnd1.addChild(cnd19);
		Condition cnd20 = new Condition("'$isAnalyzed'");
		if(input.get("isAnalyzed") != null){
			((Condition)cnd20).getProperties().put("isAnalyzed", input.get("isAnalyzed"));
		}
		cnd2.addChild(cnd20);
		if(!cnd0.toString().equals("")){
			buf.append(" " + cnd0.toString());
		}
		
		return buf.toString();
	}
	public void insertNewEntry(Connection con, Hashtable input) throws jp.crossfire.framework.database.HyperDbException {
		String sql = insertNewEntrySql(input);
		SQLExecutor exec = new SQLExecutor();
		exec.setCon(con);
		
		exec.execUpdateSQL(sql);
	}
	public String deleteEntryWithAddressSql(Hashtable input) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("DELETE FROM ");
		buf.append("WebDocumentEntry");
		
		// Condition
		ICondition condition = deleteEntryWithAddressGetCondition(input);
		if(condition != null && !condition.toString().equals("")){
			buf.append(" WHERE " + condition.toString());
		}
		
		return buf.toString();
	}
	public void deleteEntryWithAddress(Connection con, Hashtable input) throws jp.crossfire.framework.database.HyperDbException {
		String sql = deleteEntryWithAddressSql(input);
		SQLExecutor exec = new SQLExecutor();
		exec.setCon(con);
		
		exec.execUpdateSQL(sql);
	}
	public String selectWithAddressSql(Hashtable input, Integer limit, Integer offset) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("SELECT ");
		buf.append("*");
		
		buf.append(" FROM ");
		buf.append("WebDocumentEntry");
		
		// Condition
		ICondition condition = selectWithAddressGetCondition(input);
		if(condition != null && !condition.toString().equals("")){
			buf.append(" WHERE " + condition.toString());
		}
		
		// GROUP BY
		
		// Having Condition
		condition = selectWithAddressGetHavingCondition(input);
		if(condition != null && !condition.toString().equals("")){
			buf.append(" HAVING " + condition.toString());
		}
		
		// ORDER
		
		// Limit
		if(limit != null && offset != null){
			buf.append(" LIMIT " + offset.toString() +", " + limit.toString());
		}
		
		return buf.toString();
	}
	public List selectWithAddress(Connection con, Hashtable input, Integer limit, Integer offset) throws jp.crossfire.framework.database.HyperDbException {
		this.reset();
		String sql = selectWithAddressSql(input, limit, offset);
		SQLExecutor exec = new SQLExecutor();
		exec.setCon(con);
		
		// Listners
		List listners = new LinkedList();
		
		WebDataDBManagerSelectWithAddressListner listner0 = (new WebDataDBManagerSelectWithAddressListner());
		listners.add(listner0);
		
		// Execute Query
		exec.executeSelectSQL(sql, listners);
		listner0.orMap();
		
		this.webdocumententry = listner0.webdocumententryTmpCache;
		return listner0.result;
	}
	public void reset() {
			this.webdocumententry.clear();
			this.webdocumentTagTable.clear();
			this.webdocumentKeywordTable.clear();
	}
}
