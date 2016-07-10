package jp.ryo.informationPump.server.data;

import java.io.Serializable;


public class KeyAndDoubleTFIDF implements Serializable{
    private static final long serialVersionUID = 1L;

    public String keyword;
    public Double tfidf;
    
    public KeyAndDoubleTFIDF(String keyword,Double tfidf){
        this.keyword = keyword;
        this.tfidf = tfidf;
    }
}