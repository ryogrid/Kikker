package jp.ryo.informationPump.server.db.tables;

import java.lang.Integer;
import java.lang.String;
import java.sql.Date;
import java.lang.Object;

public class Webdocumententry {
	private Integer docType;
	private String title;
	private String url;
	private Integer bookmarkCount;
	private Integer viewUserCount;
	private Date crawleddate;
	private String category;
	private String description;
	private Integer isanalyzed;
    private WebdocumentKeywordTable key_tables[];    
    private WebdocumentTagTable tag_tables[];
        
    public WebdocumentKeywordTable[] getKey_tables() {
        return key_tables;
    }
    public void setKey_tables(WebdocumentKeywordTable[] key_tables) {
        this.key_tables = key_tables;
    }
    public WebdocumentTagTable[] getTag_tables() {
        return tag_tables;
    }
    public void setTag_tables(WebdocumentTagTable[] tag_tables) {
        this.tag_tables = tag_tables;
    }
	public Integer getDocType() {
		return this.docType;
	}
	public void setDocType(Integer docType) {
		this.docType = docType;
	}
	public String getTitle() {
		return this.title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return this.url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Integer getBookmarkCount() {
		return this.bookmarkCount;
	}
	public void setBookmarkCount(Integer bookmarkCount) {
		this.bookmarkCount = bookmarkCount;
	}
	public Integer getViewUserCount() {
		return this.viewUserCount;
	}
	public void setViewUserCount(Integer viewUserCount) {
		this.viewUserCount = viewUserCount;
	}
	public Date getCrawleddate() {
		return this.crawleddate;
	}
	public void setCrawleddate(Date crawleddate) {
		this.crawleddate = crawleddate;
	}
	public String getCategory() {
		return this.category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getDescription() {
		return this.description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getIsanalyzed() {
		return this.isanalyzed;
	}
	public void setIsanalyzed(Integer isanalyzed) {
		this.isanalyzed = isanalyzed;
	}
	public static boolean hasColumn(String column) {
		if(column.toUpperCase().equals("doc_type".toUpperCase())){
		   return true;
		}
		if(column.toUpperCase().equals("title".toUpperCase())){
		   return true;
		}
		if(column.toUpperCase().equals("url".toUpperCase())){
		   return true;
		}
		if(column.toUpperCase().equals("bookmark_count".toUpperCase())){
		   return true;
		}
		if(column.toUpperCase().equals("view_user_count".toUpperCase())){
		   return true;
		}
		if(column.toUpperCase().equals("crawledDate".toUpperCase())){
		   return true;
		}
		if(column.toUpperCase().equals("category".toUpperCase())){
		   return true;
		}
		if(column.toUpperCase().equals("Description".toUpperCase())){
		   return true;
		}
		if(column.toUpperCase().equals("isAnalyzed".toUpperCase())){
		   return true;
		}
		return false;
	}
	public boolean equals(Object object) {
		if(!(object instanceof Webdocumententry)){
			return false;
		}
		
		Webdocumententry table = (Webdocumententry)object;
		if(this.docType == null &&  table.docType != null){
		   return false;
		}
		if(this.docType != null &&  table.docType == null){
		   return false;
		}
		if(this.docType != null && !this.docType.equals(table.docType)){
		   return false;
		}
		if(this.title == null &&  table.title != null){
		   return false;
		}
		if(this.title != null &&  table.title == null){
		   return false;
		}
		if(this.title != null && !this.title.equals(table.title)){
		   return false;
		}
		if(this.url == null &&  table.url != null){
		   return false;
		}
		if(this.url != null &&  table.url == null){
		   return false;
		}
		if(this.url != null && !this.url.equals(table.url)){
		   return false;
		}
		if(this.bookmarkCount == null &&  table.bookmarkCount != null){
		   return false;
		}
		if(this.bookmarkCount != null &&  table.bookmarkCount == null){
		   return false;
		}
		if(this.bookmarkCount != null && !this.bookmarkCount.equals(table.bookmarkCount)){
		   return false;
		}
		if(this.viewUserCount == null &&  table.viewUserCount != null){
		   return false;
		}
		if(this.viewUserCount != null &&  table.viewUserCount == null){
		   return false;
		}
		if(this.viewUserCount != null && !this.viewUserCount.equals(table.viewUserCount)){
		   return false;
		}
		if(this.crawleddate == null &&  table.crawleddate != null){
		   return false;
		}
		if(this.crawleddate != null &&  table.crawleddate == null){
		   return false;
		}
		if(this.crawleddate != null && !this.crawleddate.equals(table.crawleddate)){
		   return false;
		}
		if(this.category == null &&  table.category != null){
		   return false;
		}
		if(this.category != null &&  table.category == null){
		   return false;
		}
		if(this.category != null && !this.category.equals(table.category)){
		   return false;
		}
		if(this.description == null &&  table.description != null){
		   return false;
		}
		if(this.description != null &&  table.description == null){
		   return false;
		}
		if(this.description != null && !this.description.equals(table.description)){
		   return false;
		}
		if(this.isanalyzed == null &&  table.isanalyzed != null){
		   return false;
		}
		if(this.isanalyzed != null &&  table.isanalyzed == null){
		   return false;
		}
		if(this.isanalyzed != null && !this.isanalyzed.equals(table.isanalyzed)){
		   return false;
		}
		return true;
	}
}
