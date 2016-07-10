package jp.ryo.informationPump.server.helper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;

public class YahooHelper {
    
    public static long getContainsPageCount(String keyword[])throws IOException{
        String doc = getDocument(keyword);
        String splited[] = doc.split("åèñ⁄ / ñÒ<strong>");
        String splited2[]=null;
        if(splited.length>=2){
            splited2 = splited[1].split("</strong>");    
        }else{
            throw new IOException("yahooÇ≈ÇÃåüçıÇ…é∏îs?");
        }
        
        return Long.parseLong(splited2[0].replaceAll(",","")); 
    }
    
    public static String getDocument(String keyword[]){
        StringBuffer buf = new StringBuffer();
        for(int i=0;i<keyword.length;i++){
            if(i!=0){
                buf.append("+" + getQueryString(keyword[i]));
            }else{
                buf.append(getQueryString(keyword[i]));
            }
        }
        String q = buf.toString();
        return doGet("http://search.yahoo.co.jp/search?_adv_prop=web&x=op&ei=UTF-8&fr=top&va_vt=any&vp="+ q + "&vp_vt=any&vo_vt=any&ve_vt=any&vd=all&vst=0&vf=all&yuragi=off&fl=0&n=10");
    }
    
    private static String doGet(String url){
        HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());
        client.getHttpConnectionManager().getParams().setConnectionTimeout(30000);
        GetMethod get = new GetMethod(url);
        
        try {
            int iGetResultCode = client.executeMethod(get);
            
            final String strGetResponseBody = get.getResponseBodyAsString();
            return strGetResponseBody;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            get.releaseConnection();
        }
        return null;
    }
    
    public static String getQueryString(String str){
        try {
            StringBuffer buf = new StringBuffer();
            for(int i=0;i<str.length();i++){
                String a_str = str.substring(i,i+1);
                if(a_str.matches("[0-9a-zA-Z]")){
                    buf.append(a_str);
                }else{
                    byte bytes[] = a_str.getBytes("UTF-8");
                    for(int j=0;j<bytes.length;j++){
                        String tmpHex = Integer.toHexString(bytes[j]);
                        if(tmpHex.length() == 1){
                            tmpHex = "0" + tmpHex;
                        }
                        if(tmpHex.length()==2){
                            buf.append("%" + (tmpHex.toUpperCase()));
                        }else{
                            buf.append("%" + (tmpHex.substring(6)).toUpperCase());    
                        }
                    }
                }
            }
            return buf.toString();   
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
