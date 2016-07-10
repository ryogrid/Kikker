package jp.ryo.informationPump.server.db.tables;

import java.lang.String;
import java.lang.Double;
import java.lang.Object;

public class UserKeywordTable {
	private String userId;
	private String keyword;
	private Double tfidfValue;
	public String getUserId() {
		return this.userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
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
		if(column.toUpperCase().equals("user_id".toUpperCase())){
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
		if(!(object instanceof UserKeywordTable)){
			return false;
		}
		
		UserKeywordTable table = (UserKeywordTable)object;
		if(this.userId == null &&  table.userId != null){
		   return false;
		}
		if(this.userId != null &&  table.userId == null){
		   return false;
		}
		if(this.userId != null && !this.userId.equals(table.userId)){
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
