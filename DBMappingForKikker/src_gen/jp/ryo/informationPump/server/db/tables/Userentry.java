package jp.ryo.informationPump.server.db.tables;

import java.lang.String;
import java.lang.Integer;
import java.sql.Date;
import java.lang.Object;

public class Userentry {
	private String id;
	private String password;
	private String name;
	private String mailAddress;
	private Integer age;
	private Date registDate;
	private Date cacheDate;
    private UserKeywordTable key_table[];  
    private String pastReadPages[];
    
    public String[] getPastReadPages() {
        return pastReadPages;
    }
    public void setPastReadPages(String[] pastReadPages) {
        this.pastReadPages = pastReadPages;
    }
    public UserKeywordTable[] getKey_table() 
    {  
      return key_table;
    }
    public void setKey_table(UserKeywordTable[] key_table) {
        this.key_table = key_table;
    }

	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPassword() {
		return this.password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMailAddress() {
		return this.mailAddress;
	}
	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}
	public Integer getAge() {
		return this.age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public Date getRegistDate() {
		return this.registDate;
	}
	public void setRegistDate(Date registDate) {
		this.registDate = registDate;
	}
	public Date getCacheDate() {
		return this.cacheDate;
	}
	public void setCacheDate(Date cacheDate) {
		this.cacheDate = cacheDate;
	}
	public static boolean hasColumn(String column) {
		if(column.toUpperCase().equals("id".toUpperCase())){
		   return true;
		}
		if(column.toUpperCase().equals("password".toUpperCase())){
		   return true;
		}
		if(column.toUpperCase().equals("name".toUpperCase())){
		   return true;
		}
		if(column.toUpperCase().equals("mail_address".toUpperCase())){
		   return true;
		}
		if(column.toUpperCase().equals("age".toUpperCase())){
		   return true;
		}
		if(column.toUpperCase().equals("regist_date".toUpperCase())){
		   return true;
		}
		if(column.toUpperCase().equals("cache_date".toUpperCase())){
		   return true;
		}
		return false;
	}
	public boolean equals(Object object) {
		if(!(object instanceof Userentry)){
			return false;
		}
		
		Userentry table = (Userentry)object;
		if(this.id == null &&  table.id != null){
		   return false;
		}
		if(this.id != null &&  table.id == null){
		   return false;
		}
		if(this.id != null && !this.id.equals(table.id)){
		   return false;
		}
		if(this.password == null &&  table.password != null){
		   return false;
		}
		if(this.password != null &&  table.password == null){
		   return false;
		}
		if(this.password != null && !this.password.equals(table.password)){
		   return false;
		}
		if(this.name == null &&  table.name != null){
		   return false;
		}
		if(this.name != null &&  table.name == null){
		   return false;
		}
		if(this.name != null && !this.name.equals(table.name)){
		   return false;
		}
		if(this.mailAddress == null &&  table.mailAddress != null){
		   return false;
		}
		if(this.mailAddress != null &&  table.mailAddress == null){
		   return false;
		}
		if(this.mailAddress != null && !this.mailAddress.equals(table.mailAddress)){
		   return false;
		}
		if(this.age == null &&  table.age != null){
		   return false;
		}
		if(this.age != null &&  table.age == null){
		   return false;
		}
		if(this.age != null && !this.age.equals(table.age)){
		   return false;
		}
		if(this.registDate == null &&  table.registDate != null){
		   return false;
		}
		if(this.registDate != null &&  table.registDate == null){
		   return false;
		}
		if(this.registDate != null && !this.registDate.equals(table.registDate)){
		   return false;
		}
		if(this.cacheDate == null &&  table.cacheDate != null){
		   return false;
		}
		if(this.cacheDate != null &&  table.cacheDate == null){
		   return false;
		}
		if(this.cacheDate != null && !this.cacheDate.equals(table.cacheDate)){
		   return false;
		}
		return true;
	}
}
