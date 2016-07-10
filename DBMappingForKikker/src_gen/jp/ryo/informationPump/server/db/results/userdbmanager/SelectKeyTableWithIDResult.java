package jp.ryo.informationPump.server.db.results.userdbmanager;

import jp.ryo.informationPump.server.db.tables.UserKeywordTable;
import java.util.Hashtable;

public class SelectKeyTableWithIDResult extends Hashtable {
	private static final long serialVersionUID = 1L;
	private UserKeywordTable userKeywordTable = null;
	public UserKeywordTable getUserKeywordTable() {
		return this.userKeywordTable;
	}
	public UserKeywordTable getUserKeywordTableWithNew() {
		if(this.userKeywordTable == null){
			this.userKeywordTable = new UserKeywordTable();
		}
		return this.userKeywordTable;
	}
	public void setUserKeywordTable(UserKeywordTable userKeywordTable) {
		this.userKeywordTable = userKeywordTable;
	}
}
