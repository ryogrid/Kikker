/*
 * 作成日: 2006/02/07
 *
 * この生成されたコメントの挿入されるテンプレートを変更するため
 * ウィンドウ > 設定 > Java > コード生成 > コードとコメント
 */
package jp.ryo.informationPump.server.exchange_server.enum;

public class ResponseCode {
    public static final int SUCCESS = 1;
    public static final int ERROR = 0;
    
    public static String getResponceName(int code){
        if(code == SUCCESS){
            return "SUCCESS";
        }else if(code == ERROR){
            return "ERROR";
        }else{
            return "Ellegal Response code!!";
        }
    }
}
