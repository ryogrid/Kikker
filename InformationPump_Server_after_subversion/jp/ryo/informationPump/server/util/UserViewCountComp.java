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
public class UserViewCountComp implements Comparator {
    
  // コンストラクタ
  public UserViewCountComp() {
    super();
  }
  
  public boolean equals(Object obj) {
    return (super.equals(obj));
  }
  
  // ここで多い順に指定する
  public int compare(Object obj1, Object obj2) {
      
    double v1;
    double v2;
    if(obj1 instanceof StoreBoxForDocumentEntry){
        StoreBoxForDocumentEntry entry1 = (StoreBoxForDocumentEntry)obj1;
        StoreBoxForDocumentEntry entry2 = (StoreBoxForDocumentEntry)obj2;
        v1 = getEvalPoint(0,entry1.data.getView_users());
        v2 = getEvalPoint(0,entry2.data.getView_users());
    }else if(obj1 instanceof SortBoxForDocumentEntry){
        SortBoxForDocumentEntry entry1 = (SortBoxForDocumentEntry)obj1;
        SortBoxForDocumentEntry entry2 = (SortBoxForDocumentEntry)obj2;
        v1 = getEvalPoint(entry1.simirality.doubleValue(),entry1.entry.getView_users());
        v2 = getEvalPoint(entry2.simirality.doubleValue(),entry2.entry.getView_users());
    }else if(obj1 instanceof DocumentEntry){
        DocumentEntry entry1 = (DocumentEntry)obj1;
        DocumentEntry entry2 = (DocumentEntry)obj2;
        v1 = getEvalPoint(1,entry1.getView_users());
        v2 = getEvalPoint(1,entry2.getView_users());
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
      return view_count;
  }
  
}
