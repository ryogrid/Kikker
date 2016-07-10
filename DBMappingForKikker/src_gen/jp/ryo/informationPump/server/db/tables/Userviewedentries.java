package jp.ryo.informationPump.server.db.tables;

import java.lang.String;
import java.lang.Object;

public class Userviewedentries {
	private String id;
	private String docUrl;
	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDocUrl() {
		return this.docUrl;
	}
	public void setDocUrl(String docUrl) {
		this.docUrl = docUrl;
	}
	public static boolean hasColumn(String column) {
		if(column.toUpperCase().equals("id".toUpperCase())){
		   return true;
		}
		if(column.toUpperCase().equals("doc_url".toUpperCase())){
		   return true;
		}
		return false;
	}
	public boolean equals(Object object) {
		if(!(object instanceof Userviewedentries)){
			return false;
		}
		
		Userviewedentries table = (Userviewedentries)object;
		if(this.id == null &&  table.id != null){
		   return false;
		}
		if(this.id != null &&  table.id == null){
		   return false;
		}
		if(this.id != null && !this.id.equals(table.id)){
		   return false;
		}
		if(this.docUrl == null &&  table.docUrl != null){
		   return false;
		}
		if(this.docUrl != null &&  table.docUrl == null){
		   return false;
		}
		if(this.docUrl != null && !this.docUrl.equals(table.docUrl)){
		   return false;
		}
		return true;
	}
}
