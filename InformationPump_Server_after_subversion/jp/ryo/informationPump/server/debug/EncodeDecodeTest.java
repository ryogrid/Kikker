/*
 * 作成日: 2006/02/22
 *
 * この生成されたコメントの挿入されるテンプレートを変更するため
 * ウィンドウ > 設定 > Java > コード生成 > コードとコメント
 */
package jp.ryo.informationPump.server.debug;

import jp.ryo.informationPump.server.util.Util;

public class EncodeDecodeTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        String encoded = Util.getQueryString("abほげ神林LLY)(=");
        System.out.println(Util.decodeQueryStringUTF8(encoded));
    }

}
