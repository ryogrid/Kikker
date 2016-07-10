package jp.ryo.informationPump.server.debug;

import java.text.DateFormat;
import java.util.*;

import jp.ryo.informationPump.server.util.Util;

public class PrintDate {

    /**
     * @param args
     */
    public static void main(String[] args) {
//        System.out.println((new Date()).toGMTString());
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
//        
//        System.out.println(calendar.get);
//        
//        Date date = new Date();
//        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG,Locale.US);
//        df.setTimeZone(TimeZone.getTimeZone("Etc/GMT"));
//        System.out.println(df.format(date)); 
//        
//        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT"),Locale.US);
//        calendar.
        
        System.out.println(Util.getDateStrForCookie(5));
    }

}
