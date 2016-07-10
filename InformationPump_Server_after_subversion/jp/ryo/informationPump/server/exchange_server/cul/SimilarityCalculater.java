//２つの文書ベクトルの類似度を計算するクラス
package jp.ryo.informationPump.server.exchange_server.cul;

import java.util.*;

import jp.ryo.informationPump.server.data.UserProfile;
import jp.ryo.informationPump.server.data.WebData;

public class SimilarityCalculater {
    public static final double SIMIRARITY_THRESHOLD = 0.8;
    public static final double HATEBU_THRESHOLD=0.2;
    public static final double USER_THRESHOLD=0.7;
    
    //0～1に正規化された類似度を返す
    //与えるHashMapはkey=String,Value=Doubleでなければならない
    public static double calculateSimilarity(HashMap map_a,HashMap map_b){
        //cos_distance = Σ(ai*bi)/√((Σ(ai)^2)*(Σ(bi)^2))とコサイン距離を求める
        Set map_a_set = map_a.entrySet();
        Iterator map_a_itr = map_a_set.iterator();
        
        double innerProduct = 0;
        double to_a_abs = 0;   //aベクトルの絶対値を計算するための一時変数
        double to_b_abs = 0;   //bベクトルの絶対値を計算するための一時変数
        
        HashMap map_b_clone = (HashMap)map_b.clone();
        
        while(map_a_itr.hasNext()){
            Map.Entry entry  = (Map.Entry)map_a_itr.next();
            String keyword = (String)entry.getKey();
            double tfidf_a = ((Double)entry.getValue()).doubleValue();
            
            Object obj = map_b.get(keyword);
            if(obj!=null){//対応するキーワードがbベクトルにもあった場合
                double tfidf_b = ((Double)obj).doubleValue();
                
                innerProduct += tfidf_a*tfidf_b;
                to_a_abs += tfidf_a*tfidf_a;
                to_b_abs += tfidf_b*tfidf_b;
                
                map_b_clone.remove(keyword);
            }else{  //対応するキーワードがなかった場合
                to_a_abs += tfidf_a*tfidf_a;
                
                //innerProductとto_b_absは計算しても値が0なので計算しなくてよい
            }
        }
        
        //bベクトルにあってaベクトルになかったキーワードについて計算する
        Set map_b_set = map_b_clone.entrySet();
        Iterator map_b_itr = map_b_set.iterator();
        while(map_b_itr.hasNext()){
            Map.Entry entry  = (Map.Entry)map_b_itr.next();
            double tfidf_b = ((Double)entry.getValue()).doubleValue();
            
            to_b_abs += tfidf_b*tfidf_b;
        }
        
        //後の計算でNaNを出してしまうのを避けるため、ここで計算結果を算出
        if((innerProduct==0)||(to_a_abs==0)||(to_b_abs==0)){
            return 0;
        }
        
        //実際にコサイン距離を求める
        double cos_distance = innerProduct/Math.sqrt(to_a_abs*to_b_abs);
        
        //コサイン距離から角度(0～pi)を得る
        double angle = Math.acos(cos_distance);
        
        //正規化を行い0～1の値にする
        double similarity = (Math.PI -angle) / Math.PI;
        
        return similarity;
    }
    
    //指定したWebとユーザーの嗜好は似通っているのかどうかを返す
    public static boolean isSimilar(WebData web,UserProfile user){
        double simirarity = getSimirarityBetweenWebUser(web, user);
        
        if(simirarity>=SIMIRARITY_THRESHOLD){
            return true;
        }else{
            return false;
        }
    }

    private static double getSimirarityBetweenWebUser(WebData web, UserProfile user) {
        double simirarity = calculateSimilarity(web.getKeyword_vector(),user.getTasteVector());
        return simirarity;
    }
    
//  指定したWebとユーザーの嗜好は似通っているのかどうかを返す
    public static boolean isSimilar(WebData web_a,WebData web_b){
        double simirarity = getSimirarityBetween2Web(web_a, web_b);
        
        if(simirarity>=SIMIRARITY_THRESHOLD){
            return true;
        }else{
            return false;
        }
    }
    
//  指定したWebと与えた嗜好は似通っているのかどうかを返す
    public static boolean isSimilar(WebData web_a,HashMap taste_vector){
        double simirarity = calculateSimilarity(web_a.getKeyword_vector(),taste_vector);
        debugOutput(web_a.getAddress(),simirarity);
        
        if(simirarity>=SIMIRARITY_THRESHOLD){
            return true;
        }else{
            return false;
        }
    }

    private static double getSimirarityBetween2Web(WebData web_a, WebData web_b) {
        double simirarity = calculateSimilarity(web_a.getKeyword_vector(),web_b.getKeyword_vector());
        return simirarity;
    }
    
    private static void debugOutput(String file_name,double similarity){
        System.out.println(file_name + ":" + similarity);
    }
}
