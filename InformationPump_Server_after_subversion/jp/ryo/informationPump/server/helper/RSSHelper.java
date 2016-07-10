package jp.ryo.informationPump.server.helper;

import java.io.IOException;
import java.util.*;

import jp.ryo.informationPump.server.data.CeekJPNewsEntry;
import jp.ryo.informationPump.server.data.RSSGetResult;
import jp.ryo.informationPump.server.util.RequestCounter;

import de.nava.informa.core.ChannelIF;
import de.nava.informa.core.ParseException;
import de.nava.informa.impl.basic.ChannelBuilder;
import de.nava.informa.impl.basic.Item;
import de.nava.informa.parsers.FeedParser;

public class RSSHelper {
    
    //last_updateÇÊÇËå„Ç…çXêVÇ≥ÇÍÇΩÇÊÇ§Ç≈Ç†ÇÍÇŒì«Ç›çûÇ›ï‘Ç∑
    //last_update==nullÇÃéûÇÕñ≥èåèÇ…ì«Ç›çûÇ›ï‘Ç∑
    public static RSSGetResult getRSSItems(String url,Date last_update){
        
//        RequestCounter.checkCanDoCommunicate();
        
        try {
            ChannelIF channel = FeedParser.parse(new ChannelBuilder(),url);

            Date pub_date = channel.getPubDate();
            
            if((last_update==null)||(pub_date.after(last_update))){
                Collection items = channel.getItems();
                Item item_arr[] = new Item[items.size()];
                Iterator itemItr = items.iterator();
                int counter=0;
                while(itemItr.hasNext()){
                    item_arr[counter++] = (Item)itemItr.next();
                }
                return new RSSGetResult(item_arr,pub_date);    
            }else{
                return new RSSGetResult(new Item[0],pub_date); 
            }
        } catch (IOException e) {
            return null;
        } catch (ParseException e) {
            return null;
        }
    }
    
    
}
