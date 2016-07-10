package jp.ryo.informationPump.server.log;

import java.util.Date;

public class ServerLogger {
    private final static String LOG_FILE_NAME = "./log/log.txt";
    private static boolean isEnable=true;

    //日付をAppendして書き込む
    public static void writeLog(String str){
        if(isEnable==true){
            LoggerUtil.log(LOG_FILE_NAME,str + "  :" + new Date());    
        }
    }
 
    public static void setEnable(boolean enable){
        isEnable = enable;
    }
  
}
