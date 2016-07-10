package jp.ryo.informationPump.server.data;


public class DoGetResultWithCharset{
    public byte body[];
    public String charset;
    
    public DoGetResultWithCharset(byte body[], String charset) {
        super();
        
        this.body = body;
        this.charset = charset;
    }
}