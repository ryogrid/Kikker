package jp.ryo.informationPump.server.db;

import java.util.List;
import java.util.LinkedList;
import java.util.Hashtable;
import java.lang.StringBuffer;
import jp.crossfire.framework.database.ICondition;
import java.sql.Connection;
import jp.crossfire.framework.database.SQLExecutor;
import jp.crossfire.framework.database.ConditionContainer;
import jp.crossfire.framework.database.Values;
import jp.crossfire.framework.database.Condition;
import java.lang.Integer;
import jp.ryo.informationPump.server.db.internal.UserDBManagerSelectUserViewedURLListner;
import jp.ryo.informationPump.server.db.internal.UserDBManagerSelectKeyTableWithIDListner;
import jp.ryo.informationPump.server.db.internal.UserDBManagerSelectAllListner;
import jp.ryo.informationPump.server.db.internal.UserDBManagerSelectWithIDListner;
import jp.ryo.informationPump.server.db.internal.UserDBManagerInternal;

public class UserDBManager extends UserDBManagerInternal {
	private List userviewedentries = new LinkedList();
	private List userKeywordTable = new LinkedList();
	private List userentry = new LinkedList();
	public List getUserviewedentries() {
		return this.userviewedentries;
	}
	public void setUserviewedentries(List userviewedentries) {
		this.userviewedentries = userviewedentries;
	}
	public List getUserKeywordTable() {
		return this.userKeywordTable;
	}
	public void setUserKeywordTable(List userKeywordTable) {
		this.userKeywordTable = userKeywordTable;
	}
	public List getUserentry() {
		return this.userentry;
	}
	public void setUserentry(List userentry) {
		this.userentry = userentry;
	}
	public String deleteUserViewedURLWithURLSql(Hashtable input) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("DELETE FROM ");
		buf.append("UserViewedEntries");
		
		// Condition
		ICondition condition = deleteUserViewedURLWithURLGetCondition(input);
		if(condition != null && !condition.toString().equals("")){
			buf.append(" WHERE " + condition.toString());
		}
		
		return buf.toString();
	}
	public void deleteUserViewedURLWithURL(Connection con, Hashtable input) throws jp.crossfire.framework.database.HyperDbException {
		String sql = deleteUserViewedURLWithURLSql(input);
		SQLExecutor exec = new SQLExecutor();
		exec.setCon(con);
		
		exec.execUpdateSQL(sql);
	}
	public String deleteUserViewedURLWithUserSql(Hashtable input) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("DELETE FROM ");
		buf.append("UserViewedEntries");
		
		// Condition
		ICondition condition = deleteUserViewedURLWithUserGetCondition(input);
		if(condition != null && !condition.toString().equals("")){
			buf.append(" WHERE " + condition.toString());
		}
		
		return buf.toString();
	}
	public void deleteUserViewedURLWithUser(Connection con, Hashtable input) throws jp.crossfire.framework.database.HyperDbException {
		String sql = deleteUserViewedURLWithUserSql(input);
		SQLExecutor exec = new SQLExecutor();
		exec.setCon(con);
		
		exec.execUpdateSQL(sql);
	}
	public String deleteUserViewedURLAllSql(Hashtable input) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("DELETE FROM ");
		buf.append("UserViewedEntries");
		
		// Condition
		ICondition condition = deleteUserViewedURLAllGetCondition(input);
		if(condition != null && !condition.toString().equals("")){
			buf.append(" WHERE " + condition.toString());
		}
		
		return buf.toString();
	}
	public void deleteUserViewedURLAll(Connection con, Hashtable input) throws jp.crossfire.framework.database.HyperDbException {
		String sql = deleteUserViewedURLAllSql(input);
		SQLExecutor exec = new SQLExecutor();
		exec.setCon(con);
		
		exec.execUpdateSQL(sql);
	}
	public String insertUserViewedURLSql(Hashtable input) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("INSERT INTO ");
		buf.append("UserViewedEntries");
		
		ConditionContainer cnd0= new Values("VALUES");
		ConditionContainer cnd1 = new ConditionContainer(",");
		ConditionContainer cnd2 = new ConditionContainer(",");
		cnd0.addChild(cnd1);
		cnd0.addChild(cnd2);
		Condition cnd3 = new Condition("id");
		cnd1.addChild(cnd3);
		Condition cnd4 = new Condition("'$id'");
		if(input.get("id") != null){
			((Condition)cnd4).getProperties().put("id", input.get("id"));
		}
		cnd2.addChild(cnd4);
		Condition cnd5 = new Condition("doc_url");
		cnd1.addChild(cnd5);
		Condition cnd6 = new Condition("'$doc_url'");
		if(input.get("doc_url") != null){
			((Condition)cnd6).getProperties().put("doc_url", input.get("doc_url"));
		}
		cnd2.addChild(cnd6);
		if(!cnd0.toString().equals("")){
			buf.append(" " + cnd0.toString());
		}
		
		return buf.toString();
	}
	public void insertUserViewedURL(Connection con, Hashtable input) throws jp.crossfire.framework.database.HyperDbException {
		String sql = insertUserViewedURLSql(input);
		SQLExecutor exec = new SQLExecutor();
		exec.setCon(con);
		
		exec.execUpdateSQL(sql);
	}
	public String selectUserViewedURLSql(Hashtable input, Integer limit, Integer offset) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("SELECT ");
		buf.append("*");
		
		buf.append(" FROM ");
		buf.append("UserViewedEntries");
		
		// Condition
		ICondition condition = selectUserViewedURLGetCondition(input);
		if(condition != null && !condition.toString().equals("")){
			buf.append(" WHERE " + condition.toString());
		}
		
		// GROUP BY
		
		// Having Condition
		condition = selectUserViewedURLGetHavingCondition(input);
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
	public List selectUserViewedURL(Connection con, Hashtable input, Integer limit, Integer offset) throws jp.crossfire.framework.database.HyperDbException {
		this.reset();
		String sql = selectUserViewedURLSql(input, limit, offset);
		SQLExecutor exec = new SQLExecutor();
		exec.setCon(con);
		
		// Listners
		List listners = new LinkedList();
		
		UserDBManagerSelectUserViewedURLListner listner0 = (new UserDBManagerSelectUserViewedURLListner());
		listners.add(listner0);
		
		// Execute Query
		exec.executeSelectSQL(sql, listners);
		listner0.orMap();
		
		this.userviewedentries = listner0.userviewedentriesTmpCache;
		return listner0.result;
	}
	public String insertNewKeywordSql(Hashtable input) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("INSERT INTO ");
		buf.append("User_keyword_table");
		
		ConditionContainer cnd0= new Values("VALUES");
		ConditionContainer cnd1 = new ConditionContainer(",");
		ConditionContainer cnd2 = new ConditionContainer(",");
		cnd0.addChild(cnd1);
		cnd0.addChild(cnd2);
		Condition cnd3 = new Condition("user_id");
		cnd1.addChild(cnd3);
		Condition cnd4 = new Condition("'$user_id'");
		if(input.get("user_id") != null){
			((Condition)cnd4).getProperties().put("user_id", input.get("user_id"));
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
	public String selectKeyTableWithIDSql(Hashtable input, Integer limit, Integer offset) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("SELECT ");
		buf.append("*");
		
		buf.append(" FROM ");
		buf.append("User_keyword_table");
		
		// Condition
		ICondition condition = selectKeyTableWithIDGetCondition(input);
		if(condition != null && !condition.toString().equals("")){
			buf.append(" WHERE " + condition.toString());
		}
		
		// GROUP BY
		
		// Having Condition
		condition = selectKeyTableWithIDGetHavingCondition(input);
		if(condition != null && !condition.toString().equals("")){
			buf.append(" HAVING " + condition.toString());
		}
		
		// ORDER
		buf.append(" ORDER BY ");
		buf.append("User_keyword_table.tfidf_value");
		
		// Limit
		if(limit != null && offset != null){
			buf.append(" LIMIT " + offset.toString() +", " + limit.toString());
		}
		
		return buf.toString();
	}
	public List selectKeyTableWithID(Connection con, Hashtable input, Integer limit, Integer offset) throws jp.crossfire.framework.database.HyperDbException {
		this.reset();
		String sql = selectKeyTableWithIDSql(input, limit, offset);
		SQLExecutor exec = new SQLExecutor();
		exec.setCon(con);
		
		// Listners
		List listners = new LinkedList();
		
		UserDBManagerSelectKeyTableWithIDListner listner0 = (new UserDBManagerSelectKeyTableWithIDListner());
		listners.add(listner0);
		
		// Execute Query
		exec.executeSelectSQL(sql, listners);
		listner0.orMap();
		
		this.userKeywordTable = listner0.userKeywordTableTmpCache;
		return listner0.result;
	}
	public String deleteKeywordsWithIDSql(Hashtable input) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("DELETE FROM ");
		buf.append("User_keyword_table");
		
		// Condition
		ICondition condition = deleteKeywordsWithIDGetCondition(input);
		if(condition != null && !condition.toString().equals("")){
			buf.append(" WHERE " + condition.toString());
		}
		
		return buf.toString();
	}
	public void deleteKeywordsWithID(Connection con, Hashtable input) throws jp.crossfire.framework.database.HyperDbException {
		String sql = deleteKeywordsWithIDSql(input);
		SQLExecutor exec = new SQLExecutor();
		exec.setCon(con);
		
		exec.execUpdateSQL(sql);
	}
	public String deleteKeywordAllSql(Hashtable input) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("DELETE FROM ");
		buf.append("User_keyword_table");
		
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
	public String deleteUserAllSql(Hashtable input) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("DELETE FROM ");
		buf.append("UserEntry");
		
		// Condition
		ICondition condition = deleteUserAllGetCondition(input);
		if(condition != null && !condition.toString().equals("")){
			buf.append(" WHERE " + condition.toString());
		}
		
		return buf.toString();
	}
	public void deleteUserAll(Connection con, Hashtable input) throws jp.crossfire.framework.database.HyperDbException {
		String sql = deleteUserAllSql(input);
		SQLExecutor exec = new SQLExecutor();
		exec.setCon(con);
		
		exec.execUpdateSQL(sql);
	}
	public String selectAllSql(Hashtable input, Integer limit, Integer offset) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("SELECT ");
		buf.append("*");
		
		buf.append(" FROM ");
		buf.append("UserEntry");
		
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
		
		UserDBManagerSelectAllListner listner0 = (new UserDBManagerSelectAllListner());
		listners.add(listner0);
		
		// Execute Query
		exec.executeSelectSQL(sql, listners);
		listner0.orMap();
		
		this.userentry = listner0.userentryTmpCache;
		return listner0.result;
	}
	public String insertNewUserSql(Hashtable input) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("INSERT INTO ");
		buf.append("UserEntry");
		
		ConditionContainer cnd0= new Values("VALUES");
		ConditionContainer cnd1 = new ConditionContainer(",");
		ConditionContainer cnd2 = new ConditionContainer(",");
		cnd0.addChild(cnd1);
		cnd0.addChild(cnd2);
		Condition cnd3 = new Condition("id");
		cnd1.addChild(cnd3);
		Condition cnd4 = new Condition("'$id'");
		if(input.get("id") != null){
			((Condition)cnd4).getProperties().put("id", input.get("id"));
		}
		cnd2.addChild(cnd4);
		Condition cnd5 = new Condition("password");
		cnd1.addChild(cnd5);
		Condition cnd6 = new Condition("'$password'");
		if(input.get("password") != null){
			((Condition)cnd6).getProperties().put("password", input.get("password"));
		}
		cnd2.addChild(cnd6);
		Condition cnd7 = new Condition("name");
		cnd1.addChild(cnd7);
		Condition cnd8 = new Condition("'$name'");
		if(input.get("name") != null){
			((Condition)cnd8).getProperties().put("name", input.get("name"));
		}
		cnd2.addChild(cnd8);
		Condition cnd9 = new Condition("mail_address");
		cnd1.addChild(cnd9);
		Condition cnd10 = new Condition("'$mail_address'");
		if(input.get("mail_address") != null){
			((Condition)cnd10).getProperties().put("mail_address", input.get("mail_address"));
		}
		cnd2.addChild(cnd10);
		Condition cnd11 = new Condition("age");
		cnd1.addChild(cnd11);
		Condition cnd12 = new Condition("'$age'");
		if(input.get("age") != null){
			((Condition)cnd12).getProperties().put("age", input.get("age"));
		}
		cnd2.addChild(cnd12);
		Condition cnd13 = new Condition("regist_date");
		cnd1.addChild(cnd13);
		Condition cnd14 = new Condition("'$regist_date'");
		if(input.get("regist_date") != null){
			((Condition)cnd14).getProperties().put("regist_date", input.get("regist_date"));
		}
		cnd2.addChild(cnd14);
		Condition cnd15 = new Condition("cache_date");
		cnd1.addChild(cnd15);
		Condition cnd16 = new Condition("'$cache_date'");
		if(input.get("cache_date") != null){
			((Condition)cnd16).getProperties().put("cache_date", input.get("cache_date"));
		}
		cnd2.addChild(cnd16);
		if(!cnd0.toString().equals("")){
			buf.append(" " + cnd0.toString());
		}
		
		return buf.toString();
	}
	public void insertNewUser(Connection con, Hashtable input) throws jp.crossfire.framework.database.HyperDbException {
		String sql = insertNewUserSql(input);
		SQLExecutor exec = new SQLExecutor();
		exec.setCon(con);
		
		exec.execUpdateSQL(sql);
	}
	public String selectWithIDSql(Hashtable input, Integer limit, Integer offset) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("SELECT ");
		buf.append("*");
		
		buf.append(" FROM ");
		buf.append("UserEntry");
		
		// Condition
		ICondition condition = selectWithIDGetCondition(input);
		if(condition != null && !condition.toString().equals("")){
			buf.append(" WHERE " + condition.toString());
		}
		
		// GROUP BY
		
		// Having Condition
		condition = selectWithIDGetHavingCondition(input);
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
	public List selectWithID(Connection con, Hashtable input, Integer limit, Integer offset) throws jp.crossfire.framework.database.HyperDbException {
		this.reset();
		String sql = selectWithIDSql(input, limit, offset);
		SQLExecutor exec = new SQLExecutor();
		exec.setCon(con);
		
		// Listners
		List listners = new LinkedList();
		
		UserDBManagerSelectWithIDListner listner0 = (new UserDBManagerSelectWithIDListner());
		listners.add(listner0);
		
		// Execute Query
		exec.executeSelectSQL(sql, listners);
		listner0.orMap();
		
		this.userentry = listner0.userentryTmpCache;
		return listner0.result;
	}
	public String deleteUserWithIDSql(Hashtable input) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("DELETE FROM ");
		buf.append("UserEntry");
		
		// Condition
		ICondition condition = deleteUserWithIDGetCondition(input);
		if(condition != null && !condition.toString().equals("")){
			buf.append(" WHERE " + condition.toString());
		}
		
		return buf.toString();
	}
	public void deleteUserWithID(Connection con, Hashtable input) throws jp.crossfire.framework.database.HyperDbException {
		String sql = deleteUserWithIDSql(input);
		SQLExecutor exec = new SQLExecutor();
		exec.setCon(con);
		
		exec.execUpdateSQL(sql);
	}
	public void reset() {
			this.userviewedentries.clear();
			this.userKeywordTable.clear();
			this.userentry.clear();
	}
}
