package jp.ryo.informationPump.server.data;

import java.io.Serializable;
import java.util.Date;

public class StoreBoxForWebData implements Serializable{
    private static final long serialVersionUID = 1L;
    
    public WebData data;
    public Date date;

    public StoreBoxForWebData(WebData data, Date date) {
        this.data = data;
        this.date = date;
    }
}
