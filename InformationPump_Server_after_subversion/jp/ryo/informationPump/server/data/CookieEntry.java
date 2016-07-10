package jp.ryo.informationPump.server.data;

import java.io.Serializable;
import java.util.Date;

public class CookieEntry implements Serializable{
    /**
     * <code>serialVersionUID</code> ‚ÌƒRƒƒ“ƒg
     */
    private static final long serialVersionUID = 1L;
    
    public Date logined_date;
    public String id;
    public CookieEntry(String id, Date logined_date) {
        super();
        
        this.id = id;
        this.logined_date = logined_date;
    }
    
    
}
