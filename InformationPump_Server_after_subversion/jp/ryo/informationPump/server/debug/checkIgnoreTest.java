package jp.ryo.informationPump.server.debug;

import jp.ryo.informationPump.server.util.Util;

public class checkIgnoreTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println(Util.checkIgnoreKeyword("�_��"));
        System.out.println(Util.checkIgnoreKeyword("alt"));
        System.out.println(Util.checkIgnoreKeyword("TABLE ".trim()));
        System.out.println((Util.check("table")==true)&&(Util.checkIgnoreKeyword("table")==false));
        System.out.println((Util.check("�_��")==true)&&(Util.checkIgnoreKeyword("�_��")==false));
    }

}
