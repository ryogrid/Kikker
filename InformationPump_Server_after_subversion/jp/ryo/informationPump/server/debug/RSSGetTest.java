package jp.ryo.informationPump.server.debug;

import de.nava.informa.impl.basic.Item;
import jp.ryo.informationPump.server.data.CeekJPNewsEntry;
import jp.ryo.informationPump.server.helper.CeekNewsHelper;
import jp.ryo.informationPump.server.helper.RSSHelper;

public class RSSGetTest {

    public static void main(String[] args) {
        System.out.println("1‰ñ–Ú");
        CeekJPNewsEntry entries[] = CeekNewsHelper.getAllCeekNewsEntries();
        
        for(int i=0;i<entries.length;i++){
            System.out.println(entries[i]);
        }
        
        System.out.println("2‰ñ–Ú");
        entries = CeekNewsHelper.getAllCeekNewsEntries();
        
        for(int i=0;i<entries.length;i++){
            System.out.println(entries[i]);
        }
    }

}
