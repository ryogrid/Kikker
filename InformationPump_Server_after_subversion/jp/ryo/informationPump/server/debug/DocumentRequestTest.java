package jp.ryo.informationPump.server.debug;

import java.io.UnsupportedEncodingException;

import jp.ryo.informationPump.server.data.DoGetResultWithCharset;
import jp.ryo.informationPump.server.util.Util;

public class DocumentRequestTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        DoGetResultWithCharset result =  Util.doGetWithCharset("http://makai.heteml.jp/makai.swf");
        try {
            System.out.println(new String(result.body,result.charset));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        System.out.println(Util.doGet("http://localhost:3005/"));
    }

}
