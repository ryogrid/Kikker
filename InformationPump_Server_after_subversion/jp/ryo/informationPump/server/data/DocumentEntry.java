package jp.ryo.informationPump.server.data;

import java.io.Serializable;
import java.util.*;

public class DocumentEntry implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String title;
    private String address;
    private HashMap keywords;
    private HashMap tags;
    private int view_users;
    private Date clawledDate;
    private String category;
    private String description;
    private int doc_type;
    
    private boolean isPreciseAnalyzed;//すでに解析結果を得ているか
                                        //sorted_keywordsとかを使うときのための条件判断用か
    private ArrayList sorted_keywords;    //  評価値が大きい順にソートされたキーワード(String)を要素として持つ
                                           //  (実体はkeywordsから引いてくるべし)
    
    public boolean isPreciseAnalyzed() {
        return isPreciseAnalyzed;
    }
    public void setIsPreciseAnalyzed(boolean isPreciseAnalyzed) {
        this.isPreciseAnalyzed = isPreciseAnalyzed;
    }
    public ArrayList getSortedKeywords() {
        return sorted_keywords;
    }
    public void setSortedKeywords(ArrayList sorted_keywords) {
        this.sorted_keywords = sorted_keywords;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String categorie) {
        this.category = categorie;
    }
    public Date getClawledDate() {
        return clawledDate;
    }
    public void setClawledDate(Date clawledDate) {
        this.clawledDate = clawledDate;
    }
    public int getView_users() {
        return view_users;
    }
    public void setView_users(int view_users) {
        this.view_users = view_users;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public HashMap getKeywords() {
        return keywords;
    }
    public void setKeywords(HashMap keywords) {
        this.keywords = keywords;
    }
    public HashMap getTags() {
        return tags;
    }
    public void setTags(HashMap tags) {
        this.tags = tags;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public DocumentEntry(String address, Date date, HashMap keywords, HashMap tags, String title, int view_users,String category,int doc_type) {
        super();
        this.address = address;
        clawledDate = date;
        this.keywords = keywords;
        this.tags = tags;
        this.title = title;
        this.view_users = view_users;
        this.category = category;
        this.doc_type = doc_type;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public ArrayList getSorted_keywords() {
        return sorted_keywords;
    }
    public void setSorted_keywords(ArrayList sorted_keywords) {
        this.sorted_keywords = sorted_keywords;
    }
    public void setPreciseAnalyzed(boolean isPreciseAnalyzed) {
        this.isPreciseAnalyzed = isPreciseAnalyzed;
    }
    public int getDoc_type() {
        return doc_type;
    }
    public void setDoc_type(int doc_type) {
        this.doc_type = doc_type;
    } 
    
}
