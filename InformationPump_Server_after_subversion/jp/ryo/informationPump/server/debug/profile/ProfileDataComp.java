/*
 * 作成日: 2006/02/05
 *
 * この生成されたコメントの挿入されるテンプレートを変更するため
 * ウィンドウ > 設定 > Java > コード生成 > コードとコメント
 */
package jp.ryo.informationPump.server.debug.profile;

import java.util.Comparator;
import java.util.Map;

import jp.ryo.informationPump.server.data.*;

//大きい順に指定する Comparator
public class ProfileDataComp implements Comparator {
  
  // コンストラクタ
  public ProfileDataComp() {
    super();
  }
  
  public boolean equals(Object obj) {
    return (super.equals(obj));
  }
  
  // ここで多い順に指定する
  public int compare(Object obj1, Object obj2) {
      
    long v1;
    long v2;
    v1 = ((ProfileTabData)obj1).cpu_time.longValue();
    v2 = ((ProfileTabData)obj2).cpu_time.longValue();
    
    if(v1 == v2){
        return 0;
    }else if(v1 > v2){
        return -1;
    }else{
        return 1;
    }
  }
}
