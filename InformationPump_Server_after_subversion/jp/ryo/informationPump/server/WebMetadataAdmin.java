package jp.ryo.informationPump.server;

import java.io.Serializable;
import java.util.HashMap;

import jp.ryo.informationPump.server.data.*;

public interface WebMetadataAdmin extends Serializable {

    public abstract void addNewPage(WebData data);

    //与えたキーワードに属し,hour時間前から現在の間に追加されたものを返す
    public abstract StoreBoxForWebData[] getTagetKeywordBelongs(String keyword,
            int hour);

    //与えたキーワードに属するドキュメントを得る
    public abstract StoreBoxForWebData[] getTagetKeywordBelongs(String keyword);

    public abstract StoreBoxForWebData getPage(String name);

    //  趣向のベクターを渡して似ているドキュメントの配列と類似度を持ったSearchResultを得る
    public abstract SearchResult search(HashMap taste_vector);

}