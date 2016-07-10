package jp.ryo.informationPump.server.db.results.webdatadbmanager;

import jp.ryo.informationPump.server.db.tables.WebdocumentTagTable;
import java.util.Hashtable;

public class SelectTagTableWithAddressResult extends Hashtable {
	private static final long serialVersionUID = 1L;
	private WebdocumentTagTable webdocumentTagTable = null;
	public WebdocumentTagTable getWebdocumentTagTable() {
		return this.webdocumentTagTable;
	}
	public WebdocumentTagTable getWebdocumentTagTableWithNew() {
		if(this.webdocumentTagTable == null){
			this.webdocumentTagTable = new WebdocumentTagTable();
		}
		return this.webdocumentTagTable;
	}
	public void setWebdocumentTagTable(WebdocumentTagTable webdocumentTagTable) {
		this.webdocumentTagTable = webdocumentTagTable;
	}
}
