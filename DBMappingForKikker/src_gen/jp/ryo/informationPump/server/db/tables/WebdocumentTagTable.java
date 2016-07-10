package jp.ryo.informationPump.server.db.tables;

import java.lang.String;
import java.lang.Double;
import java.lang.Object;

public class WebdocumentTagTable {
	private String docAddress;
	private String tag;
	private Double tfidfValue;
	public String getDocAddress() {
		return this.docAddress;
	}
	public void setDocAddress(String docAddress) {
		this.docAddress = docAddress;
	}
	public String getTag() {
		return this.tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public Double getTfidfValue() {
		return this.tfidfValue;
	}
	public void setTfidfValue(Double tfidfValue) {
		this.tfidfValue = tfidfValue;
	}
	public static boolean hasColumn(String column) {
		if(column.toUpperCase().equals("doc_address".toUpperCase())){
		   return true;
		}
		if(column.toUpperCase().equals("tag".toUpperCase())){
		   return true;
		}
		if(column.toUpperCase().equals("tfidf_value".toUpperCase())){
		   return true;
		}
		return false;
	}
	public boolean equals(Object object) {
		if(!(object instanceof WebdocumentTagTable)){
			return false;
		}
		
		WebdocumentTagTable table = (WebdocumentTagTable)object;
		if(this.docAddress == null &&  table.docAddress != null){
		   return false;
		}
		if(this.docAddress != null &&  table.docAddress == null){
		   return false;
		}
		if(this.docAddress != null && !this.docAddress.equals(table.docAddress)){
		   return false;
		}
		if(this.tag == null &&  table.tag != null){
		   return false;
		}
		if(this.tag != null &&  table.tag == null){
		   return false;
		}
		if(this.tag != null && !this.tag.equals(table.tag)){
		   return false;
		}
		if(this.tfidfValue == null &&  table.tfidfValue != null){
		   return false;
		}
		if(this.tfidfValue != null &&  table.tfidfValue == null){
		   return false;
		}
		if(this.tfidfValue != null && !this.tfidfValue.equals(table.tfidfValue)){
		   return false;
		}
		return true;
	}
}
