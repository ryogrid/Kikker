package jp.ryo.informationPump.server.db.results.webdatadbmanager;

import jp.ryo.informationPump.server.db.tables.Webdocumententry;
import java.util.Hashtable;

public class SelectDocumentsWithKeyResult extends Hashtable {
	private static final long serialVersionUID = 1L;
	private Webdocumententry webdocumententry = null;
	public Webdocumententry getWebdocumententry() {
		return this.webdocumententry;
	}
	public Webdocumententry getWebdocumententryWithNew() {
		if(this.webdocumententry == null){
			this.webdocumententry = new Webdocumententry();
		}
		return this.webdocumententry;
	}
	public void setWebdocumententry(Webdocumententry webdocumententry) {
		this.webdocumententry = webdocumententry;
	}
}
