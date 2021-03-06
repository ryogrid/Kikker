/*
 * 作成日: 2006/02/07
 *
 * この生成されたコメントの挿入されるテンプレートを変更するため
 * ウィンドウ > 設定 > Java > コード生成 > コードとコメント
 */
package jp.ryo.informationPump.server.exchange_server.enum;

public class OperationCode {
    //  オペレーションコード
    public static final int CREATE_NEW_USER=1;
    public static final int ADD_A_PAGE=2;
    public static final int ADD_SOME_PAGE = 3;
    public static final int UPDATE_USER_TASTE = 4;
    
    public static String getOperationName(int code){
        if(code == CREATE_NEW_USER){
            return "CREATE_NEW_USER";
        }else if(code == CREATE_NEW_USER){
            return "CREATE_NEW_USER";
        }else if(code == ADD_SOME_PAGE){
            return "ADD_SOME_PAGE";
        }else if(code == UPDATE_USER_TASTE){
            return "UPDATE_USER_TASTE";
        }else{
            return "Ellegal Operation code!!";
        }
    }
}
