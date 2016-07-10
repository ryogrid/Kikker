package jp.ryo.informationPump.server.db.results.webdatadbmanager;

import jp.ryo.informationPump.server.db.tables.WebdocumentKeywordTable;
import java.util.Hashtable;

public class SelectKeyTableWithAddressResult extends Hashtable {
	private static final long serialVersionUID = 1L;
	private WebdocumentKeywordTable webdocumentKeywordTable = null;
	public WebdocumentKeywordTable getWebdocumentKeywordTable() {
		return this.webdocumentKeywordTable;
	}
	public WebdocumentKeywordTable getWebdocumentKeywordTableWithNew() {
		if(this.webdocumentKeywordTable == null){
			this.webdocumentKeywordTable = new WebdocumentKeywordTable();
		}
		return this.webdocumentKeywordTable;
	}
	public void setWebdocumentKeywordTable(WebdocumentKeywordTable webdocumentKeywordTable) {
		this.webdocumentKeywordTable = webdocumentKeywordTable;
	}
}
