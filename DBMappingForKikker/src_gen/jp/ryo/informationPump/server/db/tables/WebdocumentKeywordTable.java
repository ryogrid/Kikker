package jp.ryo.informationPump.server.db.tables;

import java.lang.String;
import java.lang.Double;
import java.lang.Object;

public class WebdocumentKeywordTable {
	private String docAddress;
	private String keyword;
	private Double tfidfValue;
	public String getDocAddress() {
		return this.docAddress;
	}
	public void setDocAddress(String docAddress) {
		this.docAddress = docAddress;
	}
	public String getKeyword() {
		return this.keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
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
		if(column.toUpperCase().equals("keyword".toUpperCase())){
		   return true;
		}
		if(column.toUpperCase().equals("tfidf_value".toUpperCase())){
		   return true;
		}
		return false;
	}
	public boolean equals(Object object) {
		if(!(object instanceof WebdocumentKeywordTable)){
			return false;
		}
		
		WebdocumentKeywordTable table = (WebdocumentKeywordTable)object;
		if(this.docAddress == null &&  table.docAddress != null){
		   return false;
		}
		if(this.docAddress != null &&  table.docAddress == null){
		   return false;
		}
		if(this.docAddress != null && !this.docAddress.equals(table.docAddress)){
		   return false;
		}
		if(this.keyword == null &&  table.keyword != null){
		   return false;
		}
		if(this.keyword != null &&  table.keyword == null){
		   return false;
		}
		if(this.keyword != null && !this.keyword.equals(table.keyword)){
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
