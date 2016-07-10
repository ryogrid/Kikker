package jp.ryo.informationPump.server.db.results.userdbmanager;

import jp.ryo.informationPump.server.db.tables.Userentry;
import java.util.Hashtable;

public class SelectAllResult extends Hashtable {
	private static final long serialVersionUID = 1L;
	private Userentry userentry = null;
	public Userentry getUserentry() {
		return this.userentry;
	}
	public Userentry getUserentryWithNew() {
		if(this.userentry == null){
			this.userentry = new Userentry();
		}
		return this.userentry;
	}
	public void setUserentry(Userentry userentry) {
		this.userentry = userentry;
	}
}
