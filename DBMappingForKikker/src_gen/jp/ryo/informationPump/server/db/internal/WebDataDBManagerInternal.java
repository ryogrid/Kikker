package jp.ryo.informationPump.server.db.internal;

import java.lang.String;
import java.util.Hashtable;
import java.lang.Integer;
import jp.crossfire.framework.database.ICondition;
import jp.crossfire.framework.database.ConditionContainer;
import jp.crossfire.framework.database.Condition;

public class WebDataDBManagerInternal {
	public String selectDocumentsWithKeyAndDateSql(Hashtable input, Integer limit, Integer offset) {
		return null;
	}
	protected ICondition selectDocumentsWithKeyAndDateGetCondition(Hashtable input) {
		// Condition
		ICondition cnd0= new ConditionContainer("AND");
		ICondition cnd1= new Condition("WebDocumentEntry.url in (select WebDocument_keyword_table.doc_address from WebDocument_keyword_table where WebDocument_keyword_table.keyword = '$keyword')");
		if(input.get("keyword") != null){
			((Condition)cnd1).getProperties().put("keyword", input.get("keyword"));
		}
		((ConditionContainer)cnd0).addChild(cnd1);
		ICondition cnd2= new Condition("WebDocumentEntry.crawledDate >= '$date'");
		if(input.get("date") != null){
			((Condition)cnd2).getProperties().put("date", input.get("date"));
		}
		((ConditionContainer)cnd0).addChild(cnd2);
		ICondition cnd3= new Condition("WebDocumentEntry.doc_type = '$doc_type'");
		if(input.get("doc_type") != null){
			((Condition)cnd3).getProperties().put("doc_type", input.get("doc_type"));
		}
		((ConditionContainer)cnd0).addChild(cnd3);
		return cnd0;
	}
	protected ICondition selectDocumentsWithKeyAndDateGetHavingCondition(Hashtable input) {
		// Condition
		return null;
	}
	public String selectEntriesBeforeADateSql(Hashtable input, Integer limit, Integer offset) {
		return null;
	}
	protected ICondition selectEntriesBeforeADateGetCondition(Hashtable input) {
		// Condition
		ICondition cnd0= new ConditionContainer("AND");
		ICondition cnd1= new Condition("WebDocumentEntry.crawledDate < '$date'");
		if(input.get("date") != null){
			((Condition)cnd1).getProperties().put("date", input.get("date"));
		}
		((ConditionContainer)cnd0).addChild(cnd1);
		ICondition cnd2= new Condition("WebDocumentEntry.doc_type = '$doc_type'");
		if(input.get("doc_type") != null){
			((Condition)cnd2).getProperties().put("doc_type", input.get("doc_type"));
		}
		((ConditionContainer)cnd0).addChild(cnd2);
		return cnd0;
	}
	protected ICondition selectEntriesBeforeADateGetHavingCondition(Hashtable input) {
		// Condition
		return null;
	}
	protected ICondition deleteEntriesBeforeADateGetCondition(Hashtable input) {
		// Condition
		ICondition cnd0= new ConditionContainer("AND");
		ICondition cnd1= new Condition("WebDocumentEntry.crawledDate < '$date'");
		if(input.get("date") != null){
			((Condition)cnd1).getProperties().put("date", input.get("date"));
		}
		((ConditionContainer)cnd0).addChild(cnd1);
		ICondition cnd2= new Condition("WebDocumentEntry.doc_type = '$doc_type'");
		if(input.get("doc_type") != null){
			((Condition)cnd2).getProperties().put("doc_type", input.get("doc_type"));
		}
		((ConditionContainer)cnd0).addChild(cnd2);
		return cnd0;
	}
	public String selectEntriesAfterADateSql(Hashtable input, Integer limit, Integer offset) {
		return null;
	}
	protected ICondition selectEntriesAfterADateGetCondition(Hashtable input) {
		// Condition
		ICondition cnd0= new ConditionContainer("AND");
		ICondition cnd1= new Condition("WebDocumentEntry.crawledDate >= '$date'");
		if(input.get("date") != null){
			((Condition)cnd1).getProperties().put("date", input.get("date"));
		}
		((ConditionContainer)cnd0).addChild(cnd1);
		ICondition cnd2= new Condition("WebDocumentEntry.doc_type = '$doc_type'");
		if(input.get("doc_type") != null){
			((Condition)cnd2).getProperties().put("doc_type", input.get("doc_type"));
		}
		((ConditionContainer)cnd0).addChild(cnd2);
		return cnd0;
	}
	protected ICondition selectEntriesAfterADateGetHavingCondition(Hashtable input) {
		// Condition
		return null;
	}
	public String selectDocumentsWithKeySql(Hashtable input, Integer limit, Integer offset) {
		return null;
	}
	protected ICondition selectDocumentsWithKeyGetCondition(Hashtable input) {
		// Condition
		ICondition cnd0= new ConditionContainer("AND");
		ICondition cnd1= new Condition("WebDocumentEntry.url in (select WebDocument_keyword_table.doc_address from WebDocument_keyword_table where WebDocument_keyword_table.keyword = '$keyword')");
		if(input.get("keyword") != null){
			((Condition)cnd1).getProperties().put("keyword", input.get("keyword"));
		}
		((ConditionContainer)cnd0).addChild(cnd1);
		ICondition cnd2= new Condition("WebDocumentEntry.doc_type = '$doc_type'");
		if(input.get("doc_type") != null){
			((Condition)cnd2).getProperties().put("doc_type", input.get("doc_type"));
		}
		((ConditionContainer)cnd0).addChild(cnd2);
		return cnd0;
	}
	protected ICondition selectDocumentsWithKeyGetHavingCondition(Hashtable input) {
		// Condition
		return null;
	}
	public String selectTagTableWithAddressSql(Hashtable input, Integer limit, Integer offset) {
		return null;
	}
	protected ICondition selectTagTableWithAddressGetCondition(Hashtable input) {
		// Condition
		ICondition cnd0= new ConditionContainer("AND");
		ICondition cnd1= new Condition("WebDocument_tag_table.doc_address = '$address'");
		if(input.get("address") != null){
			((Condition)cnd1).getProperties().put("address", input.get("address"));
		}
		((ConditionContainer)cnd0).addChild(cnd1);
		return cnd0;
	}
	protected ICondition selectTagTableWithAddressGetHavingCondition(Hashtable input) {
		// Condition
		return null;
	}
	public String selectKeyTableWithAddressSql(Hashtable input, Integer limit, Integer offset) {
		return null;
	}
	protected ICondition selectKeyTableWithAddressGetCondition(Hashtable input) {
		// Condition
		ICondition cnd0= new ConditionContainer("AND");
		ICondition cnd1= new Condition("WebDocument_keyword_table.doc_address = '$address'");
		if(input.get("address") != null){
			((Condition)cnd1).getProperties().put("address", input.get("address"));
		}
		((ConditionContainer)cnd0).addChild(cnd1);
		return cnd0;
	}
	protected ICondition selectKeyTableWithAddressGetHavingCondition(Hashtable input) {
		// Condition
		return null;
	}
	protected ICondition deleteTagWithAddressGetCondition(Hashtable input) {
		// Condition
		ICondition cnd0= new ConditionContainer("AND");
		ICondition cnd1= new Condition("WebDocument_tag_table.doc_address = '$address'");
		if(input.get("address") != null){
			((Condition)cnd1).getProperties().put("address", input.get("address"));
		}
		((ConditionContainer)cnd0).addChild(cnd1);
		return cnd0;
	}
	protected ICondition deleteKeywordsWithAddressGetCondition(Hashtable input) {
		// Condition
		ICondition cnd0= new ConditionContainer("AND");
		ICondition cnd1= new Condition("WebDocument_keyword_table.doc_address = '$address'");
		if(input.get("address") != null){
			((Condition)cnd1).getProperties().put("address", input.get("address"));
		}
		((ConditionContainer)cnd0).addChild(cnd1);
		return cnd0;
	}
	protected ICondition deleteTagAllGetCondition(Hashtable input) {
		// Condition
		return null;
	}
	protected ICondition deleteKeywordAllGetCondition(Hashtable input) {
		// Condition
		return null;
	}
	protected ICondition deleteDocumentAllGetCondition(Hashtable input) {
		// Condition
		return null;
	}
	public String selectAllSql(Hashtable input, Integer limit, Integer offset) {
		return null;
	}
	protected ICondition selectAllGetCondition(Hashtable input) {
		// Condition
		ICondition cnd0= new ConditionContainer("AND");
		ICondition cnd1= new Condition("WebDocumentEntry.doc_type = '$doc_type'");
		if(input.get("doc_type") != null){
			((Condition)cnd1).getProperties().put("doc_type", input.get("doc_type"));
		}
		((ConditionContainer)cnd0).addChild(cnd1);
		return cnd0;
	}
	protected ICondition selectAllGetHavingCondition(Hashtable input) {
		// Condition
		return null;
	}
	protected ICondition deleteEntryWithAddressGetCondition(Hashtable input) {
		// Condition
		ICondition cnd0= new ConditionContainer("AND");
		ICondition cnd1= new Condition("WebDocumentEntry.url = '$address'");
		if(input.get("address") != null){
			((Condition)cnd1).getProperties().put("address", input.get("address"));
		}
		((ConditionContainer)cnd0).addChild(cnd1);
		ICondition cnd2= new Condition("WebDocumentEntry.doc_type = '$doc_type'");
		if(input.get("doc_type") != null){
			((Condition)cnd2).getProperties().put("doc_type", input.get("doc_type"));
		}
		((ConditionContainer)cnd0).addChild(cnd2);
		return cnd0;
	}
	public String selectWithAddressSql(Hashtable input, Integer limit, Integer offset) {
		return null;
	}
	protected ICondition selectWithAddressGetCondition(Hashtable input) {
		// Condition
		ICondition cnd0= new ConditionContainer("AND");
		ICondition cnd1= new Condition("WebDocumentEntry.url = '$address'");
		if(input.get("address") != null){
			((Condition)cnd1).getProperties().put("address", input.get("address"));
		}
		((ConditionContainer)cnd0).addChild(cnd1);
		ICondition cnd2= new Condition("WebDocumentEntry.doc_type = '$doc_type'");
		if(input.get("doc_type") != null){
			((Condition)cnd2).getProperties().put("doc_type", input.get("doc_type"));
		}
		((ConditionContainer)cnd0).addChild(cnd2);
		return cnd0;
	}
	protected ICondition selectWithAddressGetHavingCondition(Hashtable input) {
		// Condition
		return null;
	}
}
