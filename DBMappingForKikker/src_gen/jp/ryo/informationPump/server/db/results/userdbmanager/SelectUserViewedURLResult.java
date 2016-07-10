package jp.ryo.informationPump.server.db.results.userdbmanager;

import jp.ryo.informationPump.server.db.tables.Userviewedentries;
import java.util.Hashtable;

public class SelectUserViewedURLResult extends Hashtable {
	private static final long serialVersionUID = 1L;
	private Userviewedentries userviewedentries = null;
	public Userviewedentries getUserviewedentries() {
		return this.userviewedentries;
	}
	public Userviewedentries getUserviewedentriesWithNew() {
		if(this.userviewedentries == null){
			this.userviewedentries = new Userviewedentries();
		}
		return this.userviewedentries;
	}
	public void setUserviewedentries(Userviewedentries userviewedentries) {
		this.userviewedentries = userviewedentries;
	}
}
