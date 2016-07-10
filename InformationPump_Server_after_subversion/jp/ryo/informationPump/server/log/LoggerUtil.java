package jp.ryo.informationPump.server.log;
import java.io.*;
import java.util.HashMap;

public class LoggerUtil {
    
    private static HashMap fileHash = new HashMap();
    
    public static void log(String fileName,String str){
        BufferedWriter writer = (BufferedWriter)fileHash.get(fileName);
        
        if(writer==null){
            File file=null;
            try {
                file = new File(fileName);
                writer = new BufferedWriter(new FileWriter(file,true));
                fileHash.put(fileName,writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        try {
            writer.write(str + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
