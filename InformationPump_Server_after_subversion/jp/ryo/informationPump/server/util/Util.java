/*
 * 作成日: 2006/02/04
 *
 * この生成されたコメントの挿入されるテンプレートを変更するため
 * ウィンドウ > 設定 > Java > コード生成 > コードとコメント
 */
package jp.ryo.informationPump.server.util;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import jp.ryo.informationPump.server.data.*;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;

import de.nava.informa.impl.basic.Item;


public class Util {
    private static String special_chars[] = {"&nbsp;","&iexcl;","&pound;","&curren;","&yen;","&brvbar;","&sent;","&uml;","&copy;","&ordf;","&laquo;","&not;","&shy;","&reg;","&macr;","&deg;","&plusmn;","&sup;","&sup;","&acute;","&micro;","&para;","&middot;","&cedil;","&sup;","&ordm;","&raquo;","&frac;","&frac;","&frac;","&iquest;","&Agrave;","&Aacute;","&Acirc;","&Atilde;","&Auml;","&Aring;","&AElig;","&Ccedil;","&Egrave;","&Eacute;","&Ecirc;","&Euml;","&Igrave;","&Iacute;","&Icirc;","&Iuml;","&ETH;","&Ntilde;","&Ograve;","&Ouml;","&times;","&Oslash;","&Ugrave;","&Uacute;","&Ucirc;","&Uuml;","&Yacute;","&THORN;","&szlig;","&agrave;","&aacute;","&aring;","&aelig;","&ccedil;","&egrave;","&eacute;","&ecirc;","&euml;","&igrave;","&iacute;","&icirc;","&iuml;","&eth;","&ntilde;","&ograve;","&oacute;","&ntilde;","&ograve;","&oacute;","&ocirc;","&otilde;","&ouml;","&divide;","&oslash;","&ugrave;","&uacute;","&ucirc;","&uuml;","&yacute;","&thorn;","&yuml;","&fnof;","&Alpha;","&Beta;","&Gamma;","&Delta;","&Epsilon;","&Zeta;","&Eta;","&Theta;","&Iota;","&Kappa;","&Lambda;","&Mu;","&Nu;","&Xi;","&Omicron;","&Pi;","&Rho;","&Sigma;","&Tau;","&Upsilon;","&Phi;","&Chi;","&Psi;","&Omega;","&alpha;","&beta;","&gamma;","&delta;","&epsilon;","&zeta;","&eta;","&theta;","&iota;","&kappa;","&lambda;","&mu;","&nu;","&xi;","&omicron;","&pi;","&rho;","&sigmaf;","&sigma;","&tau;","&upsilon;","&phi;","&chi;","&psi;","&omega;","&thetasym;","&upsih;","&piv;","&bull;","&hellip;","&prime;","&Prime;","&oline;","&frasl;","&weierp;","&image;","&real;","&trade;","&alefsym;","&larr;","&uarr;","&rarr;","&darr;","&harr;","&crarr;","&lArr;","&uArr;","&rArr;","&dArr;","&hArr;","&forall;","&part;","&exist;","&empty;","&nabla;","&isin;","&notin;","&ni;","&prod;","&sum;","&minus;","&lowast;","&radic;","&prop;","&infin;","&ang;","&and;","&or;","&cap;","&cup;","&int;","&there;","&sim;","&cong;","&asymp;","&ne;","&equiv;","&le;","&ge;","&sub;","&sup;","&nsub;","&sube;","&supe;","&oplus;","&otimes;","&perp;","&sdot;","&lceil;","&rceil;","&lfloor;","&rfloor;","&lang;","&rang;","&loz;","&spades;","&clubs;","&hearts;","&diams;","&quot;","&;","&amp;","&lt;","&gt;","&OElig;","&oelig;","&Scaron;","&scaron;","&Yuml;","&circ;","&tilde;","&ensp;","&emsp;","&thinsp;","&zwnj;","&zwj;","&lrm;","&rlm;","&ndash;","&mdash;","&lsquo;","&rsquo;","&sbquo;","&ldquo;","&rdquo;","&bdquo;","&dagger;","&Dagger;","&permil;","&lsaquo;","&rsaquo;","&euro;"};
    private static int ENABLE_ANALYZE_PAGE_SIZE = 500000;  //500Kまでは受け付ける
    private static final int GET_TIMEOUT_MILLI_SECONDS = 30000;
    
//平仮名、片仮名、漢字、アルファベット、数字、である場合はtrue、そうではない場合はfalseを返す
    public static boolean check(String str) {
        for (int i = 0; i < str.length(); i++) {
            char str_code = str.charAt(i);
            if (!((str_code >= '０' && str_code <= '９')
                    || (str_code >= '0' && str_code <= '9')
                    || (str_code >= 'ァ' && str_code <= 'ヶ')
                    || (str_code >= 'A' && str_code <= 'Z')
                    || (str_code >= 'a' && str_code <= 'z')
                    || (str_code >= 'ぁ' && str_code <= 'ん') 
                    || (str_code >= 19968 && str_code <= 40959))) //漢字
            {
                return false;
            }
        }
        return true;
    }
    
    public static String replaseSpecialChars(String str){
        int len = special_chars.length;
        
        String tmpStr = str;
        for(int i=0;i<len;i++){
            tmpStr = tmpStr.replaceAll(special_chars[i],"");
            if(tmpStr==null){
                return "";
            }
        }
        return tmpStr;
    }
    
    public static String replaceCommentTags(String str){
        String tmpStr = str;
        while(true){
            int start_index = tmpStr.indexOf("<!--");
            int end_index = tmpStr.indexOf("-->");
            
            if((start_index!=-1)&&(end_index > start_index)){
                //見つかった箇所を取り除く
                tmpStr = (tmpStr.substring(0,start_index)).concat(tmpStr.substring(end_index+3,tmpStr.length()));
            }else{
                return tmpStr;
            }
        }
    }
    
    //KeyにString,ValueにDoubleを持つHashMapを与えると、ソートされたArrayList(値はKeyAndDoubleTFIDF)を返す
    public static ArrayList getSortedList(HashMap map){
        Set set = map.entrySet();
        Iterator itr = set.iterator();
        ArrayList sorted_vector = new ArrayList();
        while(itr.hasNext()){
            Map.Entry entry  = (Map.Entry)itr.next();
            String keyword = (String)entry.getKey();
            Double tfidf = (Double)entry.getValue();
            sorted_vector.add(new KeyAndDoubleTFIDF(keyword,tfidf));
        }
        Collections.sort(sorted_vector,new DoubleComp());
        return sorted_vector;
    }
    
//  SortBoxForBiasSortを要素として持つArrayListを与えると、ソートされたArrayList(値は評価値がセットされたSortBoxForBiasSor)を返す
    public static ArrayList getBiasSortedList(ArrayList list){
        Collections.sort(list,new BiasEntryComp());
        return list;
    }

//  KeyにSortBoxForWebDataを持つVectorを渡すとソートして返す
    public static ArrayList getSortedList(ArrayList list){
        ArrayList sorted_list = (ArrayList)list.clone();
        Collections.sort(sorted_list,new DoubleComp());
        return sorted_list;
    }
    
    static public String substitute(String input, String pattern, String replacement) {
        int index = input.indexOf(pattern);

        if(index == -1) {
            return input;
        }

        StringBuffer buffer = new StringBuffer();

        buffer.append(input.substring(0, index) + replacement);

        if(index + pattern.length() < input.length()) {
            String rest = input.substring(index + pattern.length(), input.length());
            buffer.append(substitute(rest, pattern, replacement));
        }
        return buffer.toString();
    }
    
    static public String HTMLEscape(String input) {
        input = substitute(input, "&",  "&amp;");
        input = substitute(input, "<",  "&lt;");
        input = substitute(input, ">",  "&gt;");
        input = substitute(input, "\"", "&quot;");
        return input;
    }
    
    static public String decodeEscapcedStr(String input){
        input = substitute(input,  "&amp;", "&");
        input = substitute(input,  "&lt;", "<");
        input = substitute(input,  "&gt;", ">");
        input = substitute(input, "&quot;", "\"");
        return input;
    }
    
    static public String EscapeSharp(String input) {
        input = substitute(input, "#",  "&sharp;");
        return input;
    }
    
    static public String decodeEscapcedSharp(String input){
        input = substitute(input,  "&sharp;", "#");
        return input;
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
    
    public static String decodeQueryStringUTF8(String str){
        try {
            StringBuffer buf = new StringBuffer(); 
            int len = str.length();
            for(int i=0;i<len;i++){
                String tmp = str.substring(i,i+1);
                if(tmp.equals("%")){
                    String hex_str1 = str.substring(i+1,i+3);
                    
                    byte tmp_byte[]=null;
                    if(hex_str1.startsWith("E")){//日本語(カタカナ,漢字,ひらがなの場合)
                        tmp_byte = new byte[3];
                        tmp_byte[0]= (byte)(Integer.parseInt(hex_str1,16));
                        String hex_str2 = str.substring(i+4,i+6);
                        String hex_str3 = str.substring(i+7,i+9);
                        tmp_byte[1]= (byte)(Integer.parseInt(hex_str2,16));
                        tmp_byte[2]= (byte)(Integer.parseInt(hex_str3,16));
                        i+=8;
                    }else{
                        tmp_byte = new byte[1];
                        tmp_byte[0]= (byte)(Integer.parseInt(hex_str1,16));
                        i+=2;
                    }
                    buf.append(new String(tmp_byte,"UTF-8"));
                }else{
                    buf.append(tmp);
                }
            }
            
            return buf.toString();   
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String doGet(String url){
        HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());
        client.getHttpConnectionManager().getParams().setConnectionTimeout(GET_TIMEOUT_MILLI_SECONDS);
        client.getHttpConnectionManager().getParams().setSoTimeout(GET_TIMEOUT_MILLI_SECONDS);
        GetMethod get = new GetMethod(url);
        
        try {
            int iGetResultCode = client.executeMethod(get);
            
            String strGetResponseBody = "";
            if(iGetResultCode==200){
                if((get.getResponseHeader("Content-Type").getValue().indexOf("text") != -1)||(get.getResponseHeader("Content-Type").getValue().indexOf("html")!= -1) ){                    
                    strGetResponseBody = get.getResponseBodyAsString();    
                }    
            }
            
            return strGetResponseBody;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            get.releaseConnection();
        }
        return null;
    }
    
//    //取得できない場合,Nullを返す
//    public static InputStream doGetForStream(String url){
//        HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());
//        client.getHttpConnectionManager().getParams().setConnectionTimeout(30000);
//        GetMethod get = new GetMethod(url);
//        
//        try {
//            int iGetResultCode = client.executeMethod(get);
//            
//            InputStream stream = null;
//            if(iGetResultCode==200){
//                if((get.getResponseHeader("Content-Type").getValue().indexOf("text") != -1)||(get.getResponseHeader("Content-Type").getValue().indexOf("html")!= -1) ){
//                    stream = get.getResponseBodyAsStream();
//                }    
//            }
//            
//            return stream;
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        } finally {
//            get.releaseConnection();
//        }
//        return null;
//    }
    
    public static DoGetResultWithCharset doGetWithCharset(String url){
        HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());
        client.getHttpConnectionManager().getParams().setConnectionTimeout(GET_TIMEOUT_MILLI_SECONDS);
        client.getHttpConnectionManager().getParams().setSoTimeout(GET_TIMEOUT_MILLI_SECONDS);
        GetMethod get = new GetMethod(url);
        
        try {
            int iGetResultCode = client.executeMethod(get);
            if((get.getResponseHeader("Content-Type").getValue().indexOf("text") != -1)||(get.getResponseHeader("Content-Type").getValue().indexOf("html")!= -1) ){
                Header headers[] = get.getResponseHeaders();
                Header content_length_header = get.getResponseHeader("Content-Length");
                Header chanked_header = get.getResponseHeader("Transfer-Encoding");
                
                //転送エンコーディングがchunkedか、Content-Lengthが指定したサイズ以下であれば
                if(((chanked_header != null)&&(chanked_header.getValue().equals("chunked"))||((content_length_header != null)&& Integer.parseInt(content_length_header.getValue()) <= ENABLE_ANALYZE_PAGE_SIZE))){
                    final byte strGetResponseBody[] = get.getResponseBody();
                    
                    //文字セットを取得
                    Header charsetHeader[] = get.getResponseHeaders("Content-Type");
                    String contains_charset = charsetHeader[0].getValue();
                    String splited[] = contains_charset.split("=");
                    String charset = null;
                    if(splited.length>=2){
                        charset = splited[1];
                    }
                    
                    return new DoGetResultWithCharset(strGetResponseBody,charset);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            get.releaseConnection();
        }
        try {
            return new DoGetResultWithCharset("".getBytes("UTF-8"),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
   
    //無視すべき文字列だった場合trueを返す
    public static boolean checkIgnoreKeyword(String keyword){
        String to_ignores[] = {"a","abc","about","add","al","ad","all","alt","am","any","aor","apr","at","b","back","bar","bb","begin","beginning","bis","black","block","blue","body","bon","border","both","bottom","box","bp","br","brown","c","call","case","center","class","clear","close","col","color","cols","css","d","december","default","div","do","document","does","ds","dt","e","each","east","elements","else","end","error","exception","exit","f","field","file","finally","finish","first","float","font","for","found","from","function","functions","g","get","gets","go","going","got","h","had","has","have","he","head","header","her","here","hp","hr","href","html","hu","i","id","if","img","index","input","into","int","is","it","item","j","january","jump","just","key","l","large","le","link","left","li","line","link","ll","load","location","m","ma","mail","main","margin","may","me","minute","minutes","more","must","my","n","name","next","nn","non","none","normal","north","not","now","null","number","o","object","of","off","ol","one","or","other","our","p","page","param","path","point","pre","private","public","q","r","really","refer","repeat","return","returns","right","s","same","script","section","self","sent","she","single","size","sm","small","smaller","so","some","space","span","src","start","starts","step","still","stop","strong","style","sub","t","table","td","text","th","than","the","their","them","then","they","this","times","title","to","top","tr","try","u","ul","under","url","using","v","value","var","version","very","w","want","was","we","wednesday","weeks","were","what","when","which","while","white","who","within","without","x","y","yellow","yes","you","your","z","0","1","2","3","4","5","6","7","8","9","10","dd","meta","dl","gothic","padding","true","false","new","form","solid","content","xhtml","japan","domain","by","host","frame","site"};
//        String to_ignores_jpn[] = {"縺","繧","繝","蜷","螂","荳","螳","縲","莉","筍","螟","网","爨","爭","衙","螂","箏","螢","蠢","蛻","蜩","萃","熙","荀","箒","荵","莠","蜈","蝗","穃","蜒","莠","荵","菴 ","蜒","莠","菴","荵","蟆","鴫","蟆 ","蠕","迚","薙","翫","莠","励","隕","蠕","倥","峨","蜈 ","蛹","蟷","蠕","蟆","蜈"};
        String to_ignores_jpn[] = {"月","火","水","木","金","土","日","年",};
        
        char str_code = keyword.charAt(0);
        //一文字目が日本語文字列だった場合
        if((str_code >= 'ァ' && str_code <= 'ヶ')|| (str_code >= 'ぁ' && str_code <= 'ん') || (str_code >= 19968 && str_code <= 40959)){
            if(keyword.length()==1){
                int len = to_ignores_jpn.length;
                for(int i=0;i<len;i++){
                    if(to_ignores_jpn[i].equals(keyword)){
                        return true;
                    }
                }
                return false;
            }else{
                return false;    
            }
        //数字の羅列の文字列は除外する
        }else if(((str_code >= '0')&&(str_code<='9'))||((str_code >= '０')&&(str_code<='９'))){
            int len = keyword.length();
            for(int i=1;i<len;i++){
                if(!((keyword.charAt(i) >= '0')&&(keyword.charAt(i)<='9'))||((str_code >= '０')&&(str_code<='９'))){
                    return false;
                }
            }
            return true;
        }else{
            int len = to_ignores.length;
            for(int i=0;i<len;i++){
                if(to_ignores[i].equalsIgnoreCase(keyword)){
                    return true;
                }
            }
            return false;
        }
    }
    
    //HashMapの状態で与えられた趣向ベクトルをユーザーが入力するときと同じ文字列で返す
    public static String convertTasteMapToStr(HashMap taste_vec){
        Set taste_set = taste_vec.entrySet();
        Iterator itr = taste_set.iterator();
        StringBuffer buf = new StringBuffer();
        while(itr.hasNext()){
            Map.Entry entry = (Map.Entry)itr.next();
            String keyword = (String)entry.getKey();
            Double tfidf = (Double)entry.getValue();
            
            buf.append(keyword + ":" + tfidf + ",");
        }
        
        String result_str = buf.toString(); 
        return result_str.substring(0,result_str.length()-1);
    }
    
    public static String getDateStrForCookie(int expire_minutes){
        StringBuffer buf = new StringBuffer();
        
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT"));
        calendar.add(Calendar.MINUTE,expire_minutes);
        
        buf.append(getDayStr(calendar.get(Calendar.DAY_OF_WEEK)) + ", ");
        buf.append(calendar.get(Calendar.DATE) + "-");
        buf.append(getMonthStr(calendar.get(Calendar.MONTH)) + "-");
        buf.append(calendar.get(Calendar.YEAR) + " ");
        buf.append(calendar.get(Calendar.HOUR_OF_DAY) + ":");
        buf.append(calendar.get(Calendar.MINUTE) +  ":");
        buf.append(calendar.get(Calendar.SECOND) + " " + "GMT");
        
        return buf.toString();
    }
    
    private static String getDayStr(int day){
        switch (day) {
        case 1:
            return "Sun";
        case 2:
            return "Mon";
        case 3:
            return "Tue";
        case 4:
            return "Wed";
        case 5:
            return "Thu";
        case 6:
            return "Fri";
        case 7:
            return "Sat";
        default:
            throw new RuntimeException();
        }
    }
    
    private static String getMonthStr(int month){
        switch (month) {
            case 0:
                return "Jan";
            case 1:
                return "Feb";
            case 2:
                return "Mar";
            case 3:
                return "Apr";
            case 4:
                return "May";
            case 5:
                return "Jun";
            case 6:
                return "Jur";
            case 7:
                return "Aug";
            case 8:
                return "Sep";
            case 9:
                return "Oct";
            case 10:
                return "Nov";
            case 11:
                return "Dec";
            default:
                throw new RuntimeException();
        }
    }
    
//  与えた文字列と、システムタイムでハッシュ値を算出(失敗した場合nullを返す)
    public static String generateHash(String str) {
        //      MessageDigestオブジェクトの初期化
        StringBuffer buffer = new StringBuffer(str);
        buffer.append(System.currentTimeMillis());       //システムタイムを文字列として追加
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA1");
            md.update(buffer.toString().getBytes());    //作成しておいた文字列で初期化
            return byteArrayToString(md.digest());          //ダイジェスト値を返す
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }
    
//  バイト配列をStringの文字列へ変換
    private static String byteArrayToString(byte[] array){
        StringBuffer tmp = new StringBuffer();
        for(int i = 0; i < array.length;i++){
            tmp.append(array[i]);
        }
        String result = tmp.toString();
        return result;
    }
    
    public static String getCookieValue(String header_str){
        String splited[] = header_str.split("=");
        if(splited.length==2){
            String cookie_value = splited[1];
            return cookie_value;
        }else{
            return null;
        }
    }
    
    public static DocumentEntry convertToDocumentEntry(CeekJPNewsEntry entry){
        DocumentEntry result_entry = new DocumentEntry(entry.url,new Date(),new HashMap(),new HashMap(),entry.title,-1,entry.category,DBUtil.CEEK_NEWS_TYPE);
        result_entry.setDescription("");
        result_entry.setClawledDate(new Date());
        
        return result_entry;
    }
    
    public static DocumentEntry[] convertToDocumentEntryArray(CeekJPNewsEntry entries[]){
        int len = entries.length;
        DocumentEntry results[] = new DocumentEntry[len];
        for(int i=0;i<len;i++){
            results[i] = convertToDocumentEntry(entries[i]);
        }
        return results;
    }
    
    public static CeekJPNewsEntry[] convertToCeekJPNewsEntryArray(Item items[]){
        int len = items.length;
        CeekJPNewsEntry results[] = new CeekJPNewsEntry[len];
        for (int i = 0; i < len; i++) {
            results[i] = new CeekJPNewsEntry(items[i].getDescription(),
                    items[i].getElementValue("dc:publisher"), items[i]
                            .getTitle(), items[i].getElementValue("link"),
                    items[i].getSubject());
        }
        
        return results;
    }
    
    //指定した文字列がいくつ含まれるか返す
    public static int countStr(String target_str,String to_find){
        String splited[] = target_str.split(to_find);
        return splited.length + 1;
    }
}
