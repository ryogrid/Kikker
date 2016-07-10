package jp.ryo.informationPump.server.data;

import java.io.Serializable;
import java.util.Date;

public class StoreBoxForDocumentEntry implements Serializable{
    private static final long serialVersionUID = 1L;
    
    public DocumentEntry data;
    public Date date;

    public StoreBoxForDocumentEntry(DocumentEntry data, Date date) {
        this.data = data;
        this.date = date;
    }
}
