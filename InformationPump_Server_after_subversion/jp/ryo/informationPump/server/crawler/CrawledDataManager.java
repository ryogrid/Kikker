package jp.ryo.informationPump.server.crawler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import jp.ryo.informationPump.server.data.DocumentSearchResult;
import jp.ryo.informationPump.server.data.StoreBoxForDocumentEntry;

public interface CrawledDataManager extends Serializable{
    
    public abstract void margeClawledData(HashMap data,int doc_type,boolean isAsAnalyzede);

    //  趣向のベクターを渡して似ているドキュメントの配列と類似度を持ったSearchResultを得る
    public abstract DocumentSearchResult search(HashMap taste_vector,int doc_type);
    
    //趣向のベクターを渡して似ているドキュメントの配列と類似度を持ったSearchResultを得る
    //DBにアクセスする量を減らすために,事前に得ておいたエントリを渡す。カテゴリ指定可能。
    public abstract DocumentSearchResult searchNews(HashMap taste_vector,String category,StoreBoxForDocumentEntry entries[]);
    
    //与えたキーワードに属し,hour時間前から現在の間に追加されたものを返す
    public abstract StoreBoxForDocumentEntry[] getTagetKeywordBelongs(
            String keyword, int hour,int doc_type);

    //与えたキーワードに属するドキュメントを得る
    public abstract StoreBoxForDocumentEntry[] getTagetKeywordBelongs(
            String keyword,int doc_type);

    public abstract void setAnalyzedResults(String target_url,
            HashMap newVecMap, ArrayList sortedKeywords,int doc_type);

    //callした時点より一定時間以上前のエントリを削除する
    public abstract void removeOldEntries(int doc_type);
    
    //全エントリのブックマーク数を更新する
    public abstract void updateBookmarkCounts(int doc_type);
    
    //対象のアドレスのエントリを得る
    public abstract StoreBoxForDocumentEntry getEntryWithAddress(String address,int doc_type);
    
    //全てのエントリを得る
    public abstract StoreBoxForDocumentEntry[] getAllEntry(int doc_type);
}