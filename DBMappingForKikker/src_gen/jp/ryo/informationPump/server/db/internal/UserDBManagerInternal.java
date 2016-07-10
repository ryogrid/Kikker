package jp.ryo.informationPump.server.db.internal;

import jp.crossfire.framework.database.ICondition;
import java.util.Hashtable;
import jp.crossfire.framework.database.ConditionContainer;
import jp.crossfire.framework.database.Condition;
import java.lang.String;
import java.lang.Integer;

public class UserDBManagerInternal {
	protected ICondition deleteUserViewedURLWithURLGetCondition(Hashtable input) {
		// Condition
		ICondition cnd0= new ConditionContainer("AND");
		ICondition cnd1= new Condition("UserViewedEntries.doc_url = '$url'");
		if(input.get("url") != null){
			((Condition)cnd1).getProperties().put("url", input.get("url"));
		}
		((ConditionContainer)cnd0).addChild(cnd1);
		return cnd0;
	}
	protected ICondition deleteUserViewedURLWithUserGetCondition(Hashtable input) {
		// Condition
		ICondition cnd0= new ConditionContainer("AND");
		ICondition cnd1= new Condition("UserViewedEntries.id = '$id'");
		if(input.get("id") != null){
			((Condition)cnd1).getProperties().put("id", input.get("id"));
		}
		((ConditionContainer)cnd0).addChild(cnd1);
		return cnd0;
	}
	protected ICondition deleteUserViewedURLAllGetCondition(Hashtable input) {
		// Condition
		return null;
	}
	public String selectUserViewedURLSql(Hashtable input, Integer limit, Integer offset) {
		return null;
	}
	protected ICondition selectUserViewedURLGetCondition(Hashtable input) {
		// Condition
		ICondition cnd0= new ConditionContainer("AND");
		ICondition cnd1= new Condition("UserViewedEntries.id = '$id'");
		if(input.get("id") != null){
			((Condition)cnd1).getProperties().put("id", input.get("id"));
		}
		((ConditionContainer)cnd0).addChild(cnd1);
		return cnd0;
	}
	protected ICondition selectUserViewedURLGetHavingCondition(Hashtable input) {
		// Condition
		return null;
	}
	public String selectKeyTableWithIDSql(Hashtable input, Integer limit, Integer offset) {
		return null;
	}
	protected ICondition selectKeyTableWithIDGetCondition(Hashtable input) {
		// Condition
		ICondition cnd0= new ConditionContainer("AND");
		ICondition cnd1= new Condition("User_keyword_table.user_id = '$id'");
		if(input.get("id") != null){
			((Condition)cnd1).getProperties().put("id", input.get("id"));
		}
		((ConditionContainer)cnd0).addChild(cnd1);
		return cnd0;
	}
	protected ICondition selectKeyTableWithIDGetHavingCondition(Hashtable input) {
		// Condition
		return null;
	}
	protected ICondition deleteKeywordsWithIDGetCondition(Hashtable input) {
		// Condition
		ICondition cnd0= new ConditionContainer("AND");
		ICondition cnd1= new Condition("User_keyword_table.user_id= '$id'");
		if(input.get("id") != null){
			((Condition)cnd1).getProperties().put("id", input.get("id"));
		}
		((ConditionContainer)cnd0).addChild(cnd1);
		return cnd0;
	}
	protected ICondition deleteKeywordAllGetCondition(Hashtable input) {
		// Condition
		return null;
	}
	protected ICondition deleteUserAllGetCondition(Hashtable input) {
		// Condition
		return null;
	}
	public String selectAllSql(Hashtable input, Integer limit, Integer offset) {
		return null;
	}
	protected ICondition selectAllGetCondition(Hashtable input) {
		// Condition
		return null;
	}
	protected ICondition selectAllGetHavingCondition(Hashtable input) {
		// Condition
		return null;
	}
	public String selectWithIDSql(Hashtable input, Integer limit, Integer offset) {
		return null;
	}
	protected ICondition selectWithIDGetCondition(Hashtable input) {
		// Condition
		ICondition cnd0= new ConditionContainer("AND");
		ICondition cnd1= new Condition("UserEntry.id = '$id'");
		if(input.get("id") != null){
			((Condition)cnd1).getProperties().put("id", input.get("id"));
		}
		((ConditionContainer)cnd0).addChild(cnd1);
		return cnd0;
	}
	protected ICondition selectWithIDGetHavingCondition(Hashtable input) {
		// Condition
		return null;
	}
	protected ICondition deleteUserWithIDGetCondition(Hashtable input) {
		// Condition
		ICondition cnd0= new ConditionContainer("AND");
		ICondition cnd1= new Condition("UserEntry.id = '$id'");
		if(input.get("id") != null){
			((Condition)cnd1).getProperties().put("id", input.get("id"));
		}
		((ConditionContainer)cnd0).addChild(cnd1);
		return cnd0;
	}
}
