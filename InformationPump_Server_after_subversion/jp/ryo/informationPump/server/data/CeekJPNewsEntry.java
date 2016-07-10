package jp.ryo.informationPump.server.data;

public class CeekJPNewsEntry {
    public String title;
    public String url;
    public String description;
    public String publisher;
    public String category;
    
    public CeekJPNewsEntry(String description, String publisher, String title, String url,String category) {
        super();
        
        this.description = description;
        this.publisher = publisher;
        this.title = title;
        this.url = url;
        this.category = category;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("title:" + title + "\n");
        buf.append("category:" + category + "\n");
        buf.append("url:" + url + "\n");
        buf.append("description:" + description + "\n");
        buf.append("publisher:" + publisher + "\n");
        
        return buf.toString();
    }
    
}