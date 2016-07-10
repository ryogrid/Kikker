/*
 * 作成日: 2006/02/05
 *
 * この生成されたコメントの挿入されるテンプレートを変更するため
 * ウィンドウ > 設定 > Java > コード生成 > コードとコメント
 */
package jp.ryo.informationPump.server.util;

import java.util.Comparator;
import java.util.Map;

import jp.ryo.informationPump.server.data.*;

//大きい順に指定する Comparator
public class BiasEntryComp implements Comparator {
    
  // コンストラクタ
  public BiasEntryComp() {
    super();
  }
  
  public boolean equals(Object obj) {
    return (super.equals(obj));
  }
  
  // ここで多い順に指定する
  public int compare(Object obj1, Object obj2) {
      
    double v1;
    double v2;
    if(obj1 instanceof SortBoxForDocumentEntry){
        SortBoxForDocumentEntry entry1 = (SortBoxForDocumentEntry)obj1;
        SortBoxForDocumentEntry entry2 = (SortBoxForDocumentEntry)obj2;
        if(entry1.entry.getDoc_type()==DBUtil.HATEBU_TYPE){
            v1 = getEvalPoint(entry1.simirality.doubleValue(),entry1.entry.getView_users());
            v2 = getEvalPoint(entry2.simirality.doubleValue(),entry2.entry.getView_users());    
        }else if(entry1.entry.getDoc_type()==DBUtil.CEEK_NEWS_TYPE){
            v1 = entry1.simirality.doubleValue();
            v2 = entry2.simirality.doubleValue();
        }else if(entry1.entry.getDoc_type()==DBUtil.YOUTUBE_TYPE){
            v1 = getEvalPoint(entry1.simirality.doubleValue(),entry1.entry.getView_users());
            v2 = getEvalPoint(entry2.simirality.doubleValue(),entry2.entry.getView_users());    
        }else{
            throw new RuntimeException();
        }
    }else{
        throw new RuntimeException();
    }
    
    if(v1 == v2){
        return 0;
    }else if(v1 > v2){
        return -1;
    }else{
        return 1;
    }
  }
  
  private double getEvalPoint(double simirality,int view_count){
      return simirality*Math.log(view_count+1);
  }
}
