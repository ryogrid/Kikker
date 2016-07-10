package jp.ryo.informationPump.server.crawler.analyze;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import jp.ryo.informationPump.server.util.Util;

import org.htmlparser.filters.StringFilter;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.ParserException;

public abstract class DocumentAnalyzer implements Runnable{
    
    protected static String getCorrectEncodeHTML(byte[] body)
            throws UnsupportedEncodingException {
        String tmpStr = new String(body);
        int index = tmpStr
                .indexOf("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=");
        if (index != -1) {
            String splited[] = tmpStr
                    .split("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=");
            String splited2[] = splited[1].split("\"");
            String encode = splited2[0];
            String correct_encoded = null;
            if (encode.equalsIgnoreCase("shift_jis")) {
                correct_encoded = new String(body, "SJIS");
            } else if (encode.equalsIgnoreCase("euc-jp")) {
                correct_encoded = new String(body, "EUC-JP");
            } else if (encode.equalsIgnoreCase("iso-2022-jp")) {
                correct_encoded = new String(body, "ISO2022JP");
            } else if (encode.equalsIgnoreCase("UTF-8")) {
                correct_encoded = new String(body, "UTF-8");
            } else if (encode.equalsIgnoreCase("UTF-16")) {
                correct_encoded = new String(body, "UTF-16");
            } else {
                correct_encoded = new String(body);
            }
            return correct_encoded;
        } else {
            return new String(body);
        }
    }

    protected static String getCorrectEncodeXML(byte[] body)
            throws UnsupportedEncodingException {
        String tmpStr = new String(body);
        int index = tmpStr.indexOf("\" encoding=\"");
        if (index != -1) {
            String splited[] = tmpStr.split("\" encoding=\"");
            String splited2[] = splited[1].split("\"");
            String encode = splited2[0];
            String correct_encoded = null;
            if (encode.equalsIgnoreCase("shift_jis")) {
                correct_encoded = new String(body, "SJIS");
            } else if (encode.equalsIgnoreCase("euc-jp")) {
                correct_encoded = new String(body, "EUC-JP");
            } else if (encode.equalsIgnoreCase("iso-2022-jp")) {
                correct_encoded = new String(body, "ISO2022JP");
            } else if (encode.equalsIgnoreCase("UTF-8")) {
                correct_encoded = new String(body, "UTF-8");
            } else if (encode.equalsIgnoreCase("UTF-16")) {
                correct_encoded = new String(body, "UTF-16");
            } else {
                correct_encoded = new String(body);
            }
            return correct_encoded;
        } else {
            return null;
        }
    }

    protected static String getEncodedStr(byte[] body, String encode) throws UnsupportedEncodingException{
        if (encode.equalsIgnoreCase("shift_jis")) {
            return new String(body, "SJIS");
        } else if (encode.equalsIgnoreCase("euc-jp")) {
            return new String(body, "EUC-JP");
        } else if (encode.equalsIgnoreCase("iso-2022-jp")) {
            return new String(body, "ISO2022JP");
        } else if (encode.equalsIgnoreCase("UTF-8")) {
            return new String(body, "UTF-8");
        } else if (encode.equalsIgnoreCase("UTF-16")) {
            return new String(body, "UTF-16");
        } else {
            return new String(body);
        }
    }

    protected static String getTextPartOfHTML(String html) {
      Lexer lexer = new Lexer(html);
      org.htmlparser.Parser parser = new org.htmlparser.Parser(lexer);

      StringBuffer buf = new StringBuffer();
      try {
//          org.htmlparser.util.NodeList list = parser.extractAllNodesThatMatch(new StringFilter());
          org.htmlparser.util.NodeList list = parser.extractAllNodesThatMatch(new StringFilter());
          NodeIterator itr = list.elements();
          while (itr.hasMoreNodes()) {
              buf.append(itr.nextNode().getText());
          }
      } catch (ParserException e1) {
          e1.printStackTrace();
      }

      return buf.toString();
    }
    
//  キーワードとして登録して良い場合はtrue,ダメな場合はfalseを返す
    protected boolean filterKeyword(String str){
        return Util.check(str);
    }
    
    public abstract HashMap parseDocument(String str_body,String tag_keywords[]);

    public abstract void run();
}