package jp.ryo.informationPump.server.web_server;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;

import jp.ryo.informationPump.server.*;
import jp.ryo.informationPump.server.crawler.*;
import jp.ryo.informationPump.server.data.*;
import jp.ryo.informationPump.server.helper.CeekNewsHelper;
import jp.ryo.informationPump.server.learn.UserTasteUpdater;
import jp.ryo.informationPump.server.rpc.GadgetRPCProcessor;
import jp.ryo.informationPump.server.util.*;
import jp.ryo.informationPump.server.web_server.rss.RssGenerator;

public class HttpRequestProcessor implements Runnable {
    private final String SHINOBI_TOP = "072197900";
    private final String SHINOBI_KEYWORD = "072197901";
    private final String SHINOBI_SUGGEST = "072197902";
    private final String SHINOBI_REGIST = "0721979003";
    private final String SHINOBI_OTHER = "0721979004";
    
    private final int BUNNER_TYPE_HATEBU = 0;
    private final int BUNNER_TYPE_NEWS = 1;
    private final int BUNNER_TYPE_OTHER = 2;
    private final int BUNNER_TYPE_YOUTUBE = 3;
    
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private BufferedWriter bwriter;

    private int read_bytes;// makePageAsStream関数とパラメータを交換するための変数
    private String doc_type;// makePageAsStream関数とパラメータを交換するための変数

    private String cookie_str; // この文字列がセットされている場合はクライアントへヘッダとして追加して返す
    // "\r\n"付き

    private String share_user_id;// HttpRequestProcessor内で共有するための変数

    public HttpRequestProcessor(Socket sockets) throws IOException {
        socket = sockets;
        this.input = new DataInputStream(new BufferedInputStream(this.socket
                .getInputStream()));
        this.output = new DataOutputStream(new BufferedOutputStream(this.socket
                .getOutputStream()));
        this.bwriter = new BufferedWriter(new OutputStreamWriter(output));
    }

    public void run() {
        
        try {
            //排他制御のためのカウンタをインクリメント
//            RequestCounter.incRequestCount();
            
            // アクセス数をインクリメント
            UserAdmin u_admin = DBUserAdmin.getInstance();
            byte buf[] = new byte[20000];
            int c;
            int readed = -1;
            try {
                try {
                    //クライアントがメッセージを全て書き出せるように少し待つ
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                readed = input.read(buf);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            if (readed != -1) {
                try {
                    byte true_readed[] = new byte[readed];
                    System.arraycopy(buf, 0, true_readed, 0, readed);
                    buf = null;
                    String recieve = new String(true_readed);

                    StringTokenizer tokenizer = new StringTokenizer(recieve);

                    String request_method = tokenizer.nextToken();
                    String path = tokenizer.nextToken();

                    System.out.println("New HTTP connection accepted "
                            + socket.getInetAddress() + ":" + socket.getPort()
                            + "," + path);

                    // ヘッダをパースしてHashMapへ入れておく
                    HashMap header_map = new HashMap();
                    do {
                        String header_element = tokenizer.nextToken("\r\n");
                        int divide_point = header_element.indexOf(":");
                        if (divide_point != -1) {
                            String key = header_element.substring(0,
                                    divide_point);
                            String value = header_element
                                    .substring(divide_point + 1);

                            header_map.put(key.trim(), value.trim());
                        }
                    } while (tokenizer.hasMoreTokens());

                    HashMap post_keys = null;// Postで与えられた各種の値
                    if (request_method.equalsIgnoreCase("post")) {
                        int index = recieve.indexOf("\r\n\r\n");

                        URLCodec codec = new URLCodec();
                        try {
                            String post_str = codec.decode(recieve
                                    .substring(index + 4), "JISAutoDetect");
                            post_keys = parsePostMessage(post_str);
                        } catch (DecoderException e) {
                            e.printStackTrace();
                        }
                    }

                    long start = System.currentTimeMillis();
                    InputStream contens_in = makePageAsStream(path, post_keys,
                            header_map);
                    System.out.println("Make page have needed "
                            + (System.currentTimeMillis() - start) + " msec :"
                            + path);

                    bwriter.write("HTTP/1.1 200 OK\r\n");

                    // クッキーをセットさせたい場合はそれもセット
                    if (cookie_str != null) {
                        bwriter.write(cookie_str);
                    }

                    bwriter.write("Server: Kikker_Web_Server\r\n");
                    bwriter.write("Connection: close\r\n");
                    // bwriter.write("Date: Wed, 15 Feb 2006 18:51:24 GMT\r\n");
                    bwriter.write("Date: " + (new Date()).toString() + "r\n");
                    bwriter.write("Accept-Ranges: bytes\r\n");

                    if (doc_type.equals("html")) {
                        bwriter.write("Content-Type: text/html\r\n");
                    } else {
                        bwriter.write("Content-Type: text/xml\r\n");
                    }

                    bwriter.write("Content-Length: " + read_bytes + "\r\n");
                    bwriter.write("\r\n");
                    bwriter.flush();

                    // BufferedInputStream contents_in = new BufferedInputStream(new
                    // FileInputStream(new File(DocumentRoot + "/index.rdf")));

                    BufferedOutputStream bout = new BufferedOutputStream(output);
                    int count = 0;
                    while ((c = contens_in.read()) != -1) {
                        //                    this.output.write(c);
                        bout.write(c);
                        //                    System.out.write(c);
                        count++;
                    }
                    bout.flush();
                    System.out.flush();
                    bwriter.flush();
                    //                System.out.println(count + "bytes writed");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    cookie_str = null;
                    try {
                        output.close();
                        input.close();
                        bwriter.close();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } finally {
//            RequestCounter.decRequestCount();
        }        
    }

    private HashMap parsePostMessage(String post_str) {
        HashMap result_map = new HashMap();
        String splited[] = post_str.split("&");
        int len = splited.length;
        for (int i = 0; i < len; i++) {
            String key_value[] = splited[i].split("=");
            // キーと値をStringで保持しておく
            if (key_value.length > 1) {
                result_map.put(key_value[0], key_value[1]);
            }
        }

        return result_map;
    }

    // pathにはURLのドメイン以降のパス文字列
    // post_keysにはkeyとしてキーのStrign,valueとして値のStringを保持したHashMap(GETの場合はnull)
    private InputStream makePageAsStream(String path, HashMap post_keies,
            HashMap header_map) {

        try {
            //htmlページのカウンタを回す
            CounterManager.getInstance().incHTMLCount();
            
            int index_of_question = path.indexOf("?id=");
            int index_of_type = path.indexOf("&type=");
            int index_of_keyword = path.indexOf("?keyword=");
            int index_of_category = path.indexOf("&category=");

            Object cookie = header_map.get("Cookie");
            String cookie_header = null;
            String cookie_value = null;
            boolean isLogined = false;
            if (cookie != null) {
                cookie_header = (String) cookie;

                int index = cookie_header.indexOf(CookieManager.COOKIE_KEY_NAME
                        + "=");
                if (index != -1) {
                    String tmp_str = cookie_header.substring(index
                            + (CookieManager.COOKIE_KEY_NAME + "=").length());
                    String splited[] = tmp_str.split(";");
                    cookie_value = splited[0];
                    isLogined = CookieManager.isLogined(cookie_value);
                    if (isLogined == true) {
                        share_user_id = CookieManager
                                .getIDByCookie(cookie_value);
                    }
                }
            }

            if ((index_of_question != -1) && (index_of_type != -1) && (index_of_category!=-1)) {
                String tmp = path.substring(index_of_question + 4);
                String splited[] = tmp.split("&");

                String id = splited[0];
                String tmp_splited[] = (path.substring(index_of_type + 6)).split("&");
                String type = tmp_splited[0]; 
                String category = path.substring(index_of_category+10);

                UserAdmin u_admin = DBUserAdmin.getInstance();

                Object obj = u_admin.getUser(id);

                // 認証成功
                if (obj != null) {
                    if(category.equals("hatebu")){
                        if (type.equals("html")) {
                            return generateSugguestHTML(id, obj, isLogined);
                        } else if (type.equals("rss")) {
                            return generateSugguestRSS(obj);
                        } else {
                            // typeが不適合
                            String error = new String(
                                    "<html><head><title>Error!!</title></head><body><p></p>Given type is not Correct, please use \"html\" or \"rss\"</body></html>\r\n");

                            String error_doc = error;
                            ByteArrayInputStream ba_in = new ByteArrayInputStream(
                                    error_doc.getBytes());

                            read_bytes = error_doc.getBytes().length;
                            doc_type = "html";
                            return ba_in;
                        }
                    }else if(category.equals("news")){
                        if (type.equals("html")) {
                            return generateNewsHTML(id, obj, isLogined);
                        } else if (type.equals("rss")) {
                            return generateNewsRSS(obj);
                        } else {
                            // typeが不適合
                            String error = new String(
                                    "<html><head><title>Error!!</title></head><body><p></p>Given type is not Correct, please use \"html\" or \"rss\"</body></html>\r\n");

                            String error_doc = error;
                            ByteArrayInputStream ba_in = new ByteArrayInputStream(
                                    error_doc.getBytes());

                            read_bytes = error_doc.getBytes().length;
                            doc_type = "html";
                            return ba_in;
                        }
                    }else if(category.equals("youtube")){
                        if (type.equals("html")) {
                            return generateYoutubeHTML(id, obj, isLogined);
                        } else if (type.equals("rss")) {
                            return generateYoutubeRSS(obj);
                        } else {
                            // typeが不適合
                            String error = new String(
                                    "<html><head><title>Error!!</title></head><body><p></p>Given type is not Correct, please use \"html\" or \"rss\"</body></html>\r\n");

                            String error_doc = error;
                            ByteArrayInputStream ba_in = new ByteArrayInputStream(
                                    error_doc.getBytes());

                            read_bytes = error_doc.getBytes().length;
                            doc_type = "html";
                            return ba_in;
                        }
                    }else{
                        String error = new String(
                        "<html><head><title>Error!!</title></head><body><p></p>illegal category!!</body></html>\r\n");
                        String error_doc = error;
                        ByteArrayInputStream ba_in = new ByteArrayInputStream(
                                error_doc.getBytes());
        
                        read_bytes = error_doc.getBytes().length;
                        doc_type = "html";
        
                        return ba_in;
                    }
                } else {// 認証失敗
                    String error = new String(
                            "<html><head><title>Error!!</title></head><body><p></p>Given ID is not correct!!</body></html>\r\n");
                    String error_doc = error;
                    ByteArrayInputStream ba_in = new ByteArrayInputStream(
                            error_doc.getBytes());

                    read_bytes = error_doc.getBytes().length;
                    doc_type = "html";

                    return ba_in;
                }
            } else if ((index_of_keyword != -1) && (index_of_type != -1)) {// 各キーワードについてのページのリクエストの場合
                String tmp_splited[] = (path.substring(index_of_type + 6)).split("&");
                String type = tmp_splited[0]; 
                String category = path.substring(index_of_category+10);
                String keyword = path.substring(index_of_keyword + 9,
                        index_of_type);
                
                if (type.equals("html")) {
                    if(category.equals("hatebu")){
                        return generateHatebuKeywordHTML(keyword, isLogined);    
                    }else if(category.equals("news")){
                        return generateCeekJPNewsKeywordHTML(keyword, isLogined);
                    }else{
//                      categoryが不適合
                        String error = new String(
                                "<html><head><title>Error!!</title></head><body><p></p>不正なカテゴリです</body></html>\r\n");

                        String error_doc = error;
                        ByteArrayInputStream ba_in = new ByteArrayInputStream(
                                error_doc.getBytes());

                        read_bytes = error_doc.getBytes().length;
                        doc_type = "html";
                        return ba_in;
                    }
                } else if (type.equals("rss")) {
                    if(category.equals("hatebu")){
                        return generateHatebuKeywordRSS(keyword);    
                    }else if(category.equals("news")){
                        return generateCeekJPNewsKeywordRSS(keyword);
                    }else{
//                      typeが不適合
                        String error = new String(
                                "<html><head><title>Error!!</title></head><body><p></p>Given type is not Correct, please use \"html\" or \"rss\"</body></html>\r\n");

                        String error_doc = error;
                        ByteArrayInputStream ba_in = new ByteArrayInputStream(
                                error_doc.getBytes());

                        read_bytes = error_doc.getBytes().length;
                        doc_type = "html";
                        return ba_in; 
                    }
                } else {
                    // typeが不適合
                    String error = new String(
                            "<html><head><title>Error!!</title></head><body><p></p>Given type is not Correct, please use \"html\" or \"rss\"</body></html>\r\n");

                    String error_doc = error;
                    ByteArrayInputStream ba_in = new ByteArrayInputStream(
                            error_doc.getBytes());

                    read_bytes = error_doc.getBytes().length;
                    doc_type = "html";
                    return ba_in;
                }
            } else if (path.equals("/") || path.equals("")) {// トップページを吐く
                return generateTopPageHTML(isLogined);
            } else if (path.equals("/regist")) { // ユーザー登録用ページを生成
                return generateRegistPageHTML(post_keies, isLogined);
            } else if (path.equals("/edit")) { // キーワードベクトル再編集用ページを生成
                return generateEditPageHTML(post_keies, isLogined);
            } else if (path.equals("/search")) { // 検索を行う
                return generateSearchResult(post_keies, isLogined);
            } else if (path.equals("/update")) { // キーワードベクトルのupdateをPostでかける
                return executeUpdateAndReturnHTML(post_keies);
            } else if (path.equals("/login")) {
                return generateLoginPageHTML(post_keies, isLogined);
            } else if (path.equals("/logout")) {
                return generateLogoutPageHTML(cookie_value);
            } else if (path.startsWith("/learn?url=")) {
                return generateLearnAndRedirectHTML(path, cookie_value);
            } else if(path.startsWith("/rpc?")){
                doc_type = "html";
                return new ByteArrayInputStream(GadgetRPCProcessor.excecuteGadgetRPC(path).getBytes());
            }else {
                String error = new String(
                        "<html><head><title>Error!!</title></head><body><p></p>Your Requested Page or User isn't here!!</body></html>\r\n");
                String error_doc = error;
                ByteArrayInputStream ba_in = new ByteArrayInputStream(error_doc
                        .getBytes());

                read_bytes = error_doc.getBytes().length;
                doc_type = "html";
                return ba_in;
            }
        } catch (Throwable e) {
            e.printStackTrace();

            String error = new String(
                    "<html><head><title>Error!!</title></head><body><p></p>Info Pump Server internal Error Occured!!</body></html>\r\n");
            String error_doc = error;
            ByteArrayInputStream ba_in = new ByteArrayInputStream(error_doc
                    .getBytes());

            read_bytes = error_doc.getBytes().length;
            doc_type = "html";
            return ba_in;
        }
    }

    private InputStream generateNewsRSS(Object obj) {
//     rssのカウンタを回す
        CounterManager.getInstance().incRSSCount();
        
        WebMetadataAdmin w_admin = FileWebMetadataAdmin.getInstance();
        SearchResult sugguested = w_admin.search(((UserProfile) obj)
                .getTasteVector());
        UserProfile profile = (UserProfile) obj;

        String rssStr = RssGenerator.generateCeekJPNewsSuggestRSS(profile, sugguested);
        String rss_doc = rssStr.toString();
        ByteArrayInputStream ba_in = null;

        ba_in = new ByteArrayInputStream(rss_doc.getBytes());

        read_bytes = rss_doc.getBytes().length;
        doc_type = "rss";
        return ba_in;
    }

    private InputStream generateNewsHTML(String id, Object obj, boolean isLogined) {
        UserProfile profile = (UserProfile) obj;

        StringBuffer html_doc = new StringBuffer();

        html_doc
                .append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n");
        html_doc.append("<html>\n");

        html_doc.append("<head>\n");
        appendCSS(html_doc);
        html_doc
                .append("<META http-equiv=\"Content-Type\" content=\"text/html; charset=shift_jis\">\n");

        html_doc.append("<title>News Links Suggest to " + id + "</title>\n");
        html_doc
                .append("<LINK rel=\"alternate\" type=\"application/rss+xml\" title=\"Links Suggest to "
                        + id
                        + "\" href=\"http://"
                        + WebServer.HOST_NAME
                        + ":"
                        + WebServer.PORT
                        + "/?id="
                        + profile.getId()
                        + "&amp;type=rss&amp;category=news\">");
        html_doc
                .append("<LINK href=\"http://ryogrid.myhome.cx/distribute/kikker_style.css\" type=text/css rel=stylesheet>");
        html_doc.append("</head>\n<body>");

        appendBunner(html_doc, profile.getId(), isLogined,BUNNER_TYPE_NEWS);
        appendSHINOBI(html_doc, SHINOBI_SUGGEST);
        html_doc
                .append("<a href=\"http://"
                        + WebServer.HOST_NAME
                        + ":"
                        + WebServer.PORT
                        + "/?id="
                        + profile.getId()
                        + "&amp;type=rss&amp;category=news\"><img src=\"http://ryogrid.myhome.cx/distribute/rss_icon.gif\"></a>\n");

        
        
        //データを取り出しておく
        CrawledDataManager c_manager = DBCrawledDataManager.getInstance();
        StoreBoxForDocumentEntry all_entries[] = c_manager.getAllEntry(DBUtil.CEEK_NEWS_TYPE);
        
        //各項目を出力
        appendANewsCategory(html_doc,all_entries,CeekNewsHelper.NATIONAL,profile.getTasteVector(),isLogined);
        appendANewsCategory(html_doc,all_entries,CeekNewsHelper.POLITICS,profile.getTasteVector(),isLogined);
        appendANewsCategory(html_doc,all_entries,CeekNewsHelper.WORLD,profile.getTasteVector(),isLogined);
        appendANewsCategory(html_doc,all_entries,CeekNewsHelper.CHINA,profile.getTasteVector(),isLogined);
        appendANewsCategory(html_doc,all_entries,CeekNewsHelper.BUSINESS,profile.getTasteVector(),isLogined);
        appendANewsCategory(html_doc,all_entries,CeekNewsHelper.IT,profile.getTasteVector(),isLogined);
        appendANewsCategory(html_doc,all_entries,CeekNewsHelper.SPORTS,profile.getTasteVector(),isLogined);
        appendANewsCategory(html_doc,all_entries,CeekNewsHelper.ENTERTAINMENT,profile.getTasteVector(),isLogined);
        appendANewsCategory(html_doc,all_entries,CeekNewsHelper.SCIENCE,profile.getTasteVector(),isLogined);
        appendANewsCategory(html_doc,all_entries,CeekNewsHelper.OBITUARIES,profile.getTasteVector(),isLogined);
        appendANewsCategory(html_doc,all_entries,CeekNewsHelper.LOCAL,profile.getTasteVector(),isLogined);
        appendANewsCategory(html_doc,all_entries,CeekNewsHelper.ETC,profile.getTasteVector(),isLogined);
        
////      キーワードベクトルを表示する
//        html_doc
//                .append("<DIV id=container><DIV id=body><DIV class=option style=\"CLEAR: both\">");
//
//        html_doc.append("<H3><A>" + id + "の気になっているキーワード</A></H3>");
//        html_doc.append("<DIV class=optionsub>");
//        appendAdsenceLect(html_doc);
//
//        HashMap taste_vec = ((UserProfile) obj).getTasteVector();
//        ArrayList sorted_vec = Util.getSortedList(taste_vec);
//
//        for (int i = 0; i < ((sorted_vec.size() > KikkerConfigration.ALIGN_USER_KEYWORDS)? KikkerConfigration.ALIGN_USER_KEYWORDS : sorted_vec.size()) ; i++) {
//            KeyAndDoubleTFIDF key_tfidf = (KeyAndDoubleTFIDF) sorted_vec.get(i);
//            String keyword = key_tfidf.keyword;
//            double tfidf = key_tfidf.tfidf.doubleValue();
//
//            html_doc
//                    .append("<p>"
//                            + (i + 1)
//                            + "位</em>:<font color=\"#666666\">"
//                            + ((int) tfidf)
//                            + " ｷﾆﾅｰﾙ</font>:<font size=\"5\"><SPAN class=entry-footer><a class=keyword href=\"http://"
//                            + WebServer.HOST_NAME + ":" + WebServer.PORT
//                            + "/?keyword=" + Util.getQueryString(keyword)
//                            + "&amp;type=html&amp;category=news\">" + keyword
//                            + "</a></span></font>\n");
//        }
//        appendAmazon(html_doc);
//
//        // optionsubとoption,container,bodyを閉じる
//        html_doc.append("</div></div></div></div>");
        
        appendAnalytics(html_doc);
        html_doc.append("</body></html>\n");
        html_doc.append("\r\n");

        String doc = html_doc.toString();
        ByteArrayInputStream ba_in = new ByteArrayInputStream(doc.getBytes());

        read_bytes = doc.getBytes().length;
        doc_type = "html";
        return ba_in;
    }

    private InputStream generateLearnAndRedirectHTML(String path,
            String cookie_value) {

         String tmp_splited[] = Util.decodeEscapcedSharp(Util.decodeEscapcedStr(path.substring(11))).split("&category=");
         if(tmp_splited.length!=2){
             String error = new String(
             "<html><head><title>Error!!</title></head><body><p></p>Kikkerへのアドレス指定方法が不正です</body></html>\r\n");
             String error_doc = error;
             ByteArrayInputStream ba_in = new ByteArrayInputStream(error_doc
                     .getBytes());

             read_bytes = error_doc.getBytes().length;
             doc_type = "html";
             return ba_in;
         }
         
        String address = tmp_splited[0];
        String category = tmp_splited[1];
        
        if ((cookie_value != null)&&CookieManager.isLogined(cookie_value)) {
            String id = CookieManager.getIDByCookie(cookie_value);

            // 非同期に更新
            UserTasteUpdater updater = null;
            if(category.equals("hatebu")){
                updater = new UserTasteUpdater(id, address,DBUtil.HATEBU_TYPE);
            }else if(category.equals("news")){
                updater = new UserTasteUpdater(id, address,DBUtil.CEEK_NEWS_TYPE);
            }else if(category.equals("youtube")){
                updater = new UserTasteUpdater(id, address,DBUtil.YOUTUBE_TYPE);
            }else{
                String error = new String(
                "<html><head><title>Error!!</title></head><body><p></p>カテゴリーが不正です</body></html>\r\n");
                String error_doc = error;
                ByteArrayInputStream ba_in = new ByteArrayInputStream(error_doc
                        .getBytes());
   
                read_bytes = error_doc.getBytes().length;
                doc_type = "html";
                return ba_in;
            }
                
            updater.start();
        }

        // リダイレクトするためのHTMLを返す
        String doc_str = new String(
                "<html><head><META HTTP-EQUIV=\"Refresh\" CONTENT=\"0; URL="
                        + Util.HTMLEscape(address) 
                        + "\"></head><body></body></html>");
        ByteArrayInputStream ba_in = new ByteArrayInputStream(doc_str
                .getBytes());

        read_bytes = doc_str.getBytes().length;
        doc_type = "html";
        return ba_in;

    }

    // ヘッダは値だけ
    // cookie_valueはnullの可能性がある
    private InputStream generateLogoutPageHTML(String cookie_value) {
        if (cookie_value != null) {
            CookieManager.logout(cookie_value);
        }

        String doc_str = new String(
                "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"><html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=shift_jis\"><title>ログアウト成功</title><META HTTP-EQUIV=\"Refresh\" CONTENT=\"2; URL=http://"
                        + WebServer.HOST_NAME
                        + ":"
                        + WebServer.PORT
                        + "/\"></head><body><p>ログアウトに成功しました</p>");
        StringBuffer html_doc = new StringBuffer(doc_str);
        appendAdsence(html_doc);
        appendAnalytics(html_doc);
        html_doc.append("</body></html>");
        doc_str = html_doc.toString();
        ByteArrayInputStream ba_in = new ByteArrayInputStream(doc_str
                .getBytes());

        read_bytes = doc_str.getBytes().length;
        doc_type = "html";
        return ba_in;
    }

    private InputStream generateLoginPageHTML(HashMap post_keies,
            boolean isLogined) {
        if (post_keies == null) { // GETの場合
            StringBuffer html_doc = new StringBuffer();

            html_doc
                    .append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n");
            html_doc.append("<html>\n");

            html_doc.append("<head>\n");
            html_doc
                    .append("<META http-equiv=\"Content-Type\" content=\"text/html; charset=shift_jis\">\n");
            html_doc
                    .append("<LINK href=\"http://ryogrid.myhome.cx/distribute/kikker_style.css\" type=text/css rel=stylesheet>");

            html_doc.append("<title>ログイン</title>\n");

            html_doc.append("</head>");
            html_doc.append("<body>");
            appendBunner(html_doc, "誰か", isLogined,BUNNER_TYPE_OTHER);
            appendSHINOBI(html_doc, SHINOBI_OTHER);

            html_doc
                    .append("<DIV id=container><DIV id=body><DIV class=option style=\"CLEAR: both\">");

            html_doc.append("<H3><A>ログイン</A></H3>");
            html_doc.append("<DIV class=optionsub>");
            html_doc.append("<br>");

            html_doc.append("<FORM action=http://" + WebServer.HOST_NAME + ":"
                    + WebServer.PORT + "/login method=post>");
            html_doc.append("<H2 class=registdescription>ユーザID</H2>");
            html_doc
                    .append("<input type=\"text\" name=\"id\" size=\"40\"><br>");
            html_doc.append("<H2 class=registdescription>パスワード</H2>");
            html_doc
                    .append("<input type=\"password\" name=\"password\" size=\"40\"><br>");

            appendAdsence(html_doc);
            html_doc.append("<INPUT type=submit value=\" ログイン \">");
            html_doc.append("</FORM>");

            // optionsubとoption,container,bodyを閉じる
            html_doc.append("</div></div></div></div>");

            appendAnalytics(html_doc);
            html_doc.append("</body></html>\n");
            html_doc.append("\r\n");

            String doc = html_doc.toString();
            ByteArrayInputStream ba_in = new ByteArrayInputStream(doc
                    .getBytes());

            read_bytes = doc.getBytes().length;
            doc_type = "html";

            return ba_in;
        } else {// Postの場合
            String id_str = (String) post_keies.get("id");
            String passwd_str = (String) post_keies.get("password");

            if ((id_str == null) || (passwd_str == null)) {
                String error = new String(
                        "<html><head><title>Error!!</title></head><body><p>Your Input is ellegal!!</p></body></html>\r\n");
                String error_doc = error;
                ByteArrayInputStream ba_in = new ByteArrayInputStream(error_doc
                        .getBytes());

                read_bytes = error_doc.getBytes().length;
                doc_type = "html";
                return ba_in;
            }

            UserAdmin u_admin = DBUserAdmin.getInstance();

            // 指定されたユーザーが存在しない場合
            if (u_admin.getUser(id_str) == null) {
                String error = new String(
                        "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"><html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=shift_jis\"><title>Error!!</title></head><body><p>"
                                + id_str
                                + "というユーザーは存在しません</p></body></html>\r\n");
                String error_doc = error;
                ByteArrayInputStream ba_in = new ByteArrayInputStream(error_doc
                        .getBytes());

                read_bytes = error_doc.getBytes().length;
                doc_type = "html";
                return ba_in;
            }

            // パスワードが間違っている場合
            UserProfile user_prof = u_admin.getUser(id_str);
            if (!user_prof.getPassword().equals(passwd_str)) {
                String error = new String(
                        "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"><html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=shift_jis\"><title>Error!!</title></head><body><p>パスワードが間違っています</p></body></html>\r\n");
                String error_doc = error;
                ByteArrayInputStream ba_in = new ByteArrayInputStream(error_doc
                        .getBytes());

                read_bytes = error_doc.getBytes().length;
                doc_type = "html";
                return ba_in;
            }

            // クッキーを登録する
            String cookie_str = CookieManager.login(id_str);
            setCookieStr(CookieManager.COOKIE_KEY_NAME, cookie_str,
                    CookieManager.COOKIE_APPLY_PATH);

            String doc_str = new String(
                    "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"><html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=shift_jis\"><title>ログイン成功</title></head><body><p><em>ID:</em>"
                            + id_str + "のログインに成功しました</p><br>");

            StringBuffer html_doc = new StringBuffer(doc_str);

            html_doc
                    .append("<font size=\"4\"><b>ログインした後は,自分にお薦めされたリンクや,Topから他のユーザーへのお薦めリンク,キーワードに関するページの一覧のリンクなどをクリックすることでKikkerがあなたの好みを学習します。いろいろ見てみて下さい。学習が終わったらページ上部にあるログアウトボタンでログアウトして下さい</b></font><br><br>");
            html_doc.append("<a href=\""
                    + Util.HTMLEscape("/?id=" + id_str + "&type=html&category=hatebu")
                    + "\"><font size=\"4\"><b>" + id_str
                    + " のページはこちら</b></font></a><br>");
            appendAdsence(html_doc);
            appendSHINOBI(html_doc, SHINOBI_OTHER);

            html_doc.append("</body></html>");
            doc_str = html_doc.toString();
            ByteArrayInputStream ba_in = new ByteArrayInputStream(doc_str
                    .getBytes());

            read_bytes = doc_str.getBytes().length;
            doc_type = "html";
            return ba_in;

        }
    }

    private InputStream executeUpdateAndReturnHTML(HashMap post_keies) {
        if (post_keies == null) { // GETの場合
            String error = new String(
                    "<html><head><title>Error!!</title></head><body><p>Here is not document!!</p></body></html>\r\n");
            String error_doc = error;
            ByteArrayInputStream ba_in = new ByteArrayInputStream(error_doc
                    .getBytes());

            read_bytes = error_doc.getBytes().length;
            doc_type = "html";
            return ba_in;
        } else {// Postの場合
            try {
                String id_str = (String) post_keies.get("id");
                String passwd_str = (String) post_keies.get("password");
                String taste_str = (String) post_keies.get("taste_vector");

                if ((id_str == null) || (passwd_str == null)
                        || (taste_str == null)) {
                    String error = new String(
                            "<html><head><title>Error!!</title></head><body><p>Your Input is ellegal!!</p></body></html>\r\n");
                    String error_doc = error;
                    ByteArrayInputStream ba_in = new ByteArrayInputStream(
                            error_doc.getBytes());

                    read_bytes = error_doc.getBytes().length;
                    doc_type = "html";
                    return ba_in;
                }

                UserAdmin u_admin = DBUserAdmin.getInstance();

                // 指定されたユーザーが存在しない場合
                if (u_admin.getUser(id_str) == null) {
                    String error = new String(
                            "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"><html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=shift_jis\"><title>Error!!</title></head><body><p>不正なアクセスです</p></body></html>\r\n");
                    String error_doc = error;
                    ByteArrayInputStream ba_in = new ByteArrayInputStream(
                            error_doc.getBytes());

                    read_bytes = error_doc.getBytes().length;
                    doc_type = "html";
                    return ba_in;
                }

                // パスワードが間違っている場合
                UserProfile user_prof = u_admin.getUser(id_str);
                if (!user_prof.getPassword().equals(passwd_str)) {
                    String error = new String(
                            "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"><html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=shift_jis\"><title>Error!!</title></head><body><p>不正なアクセスです</p></body></html>\r\n");
                    String error_doc = error;
                    ByteArrayInputStream ba_in = new ByteArrayInputStream(
                            error_doc.getBytes());

                    read_bytes = error_doc.getBytes().length;
                    doc_type = "html";
                    return ba_in;
                }

                u_admin.updateTasete(id_str, parseToVector(taste_str));

                String doc_str = new String(
                        "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"><html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=shift_jis\"><title>更新成功!!</title></head><body><p><em>ID:</em>"
                                + id_str
                                + "のキーワードの更新に成功しました</p><br><a href=\""
                                + Util.HTMLEscape("/?id=" + id_str
                                        + "&type=html&category=hatebu")
                                + "\"><font size=\"4\"><b>"
                                + id_str
                                + " のページはこちら</b></font></a><br>");
                StringBuffer html_doc = new StringBuffer(doc_str);
                appendAdsence(html_doc);
                appendAnalytics(html_doc);
                html_doc.append("</body></html>");
                doc_str = html_doc.toString();
                ByteArrayInputStream ba_in = new ByteArrayInputStream(doc_str
                        .getBytes());

                read_bytes = doc_str.getBytes().length;
                doc_type = "html";
                return ba_in;
            } catch (Exception e) {
                e.printStackTrace();
                String error = new String(
                        "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"><html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=shift_jis\"><title>Error!!</title></head><body><p>Kikker internal error occured!!</p></body></html>\r\n");
                String error_doc = error;
                ByteArrayInputStream ba_in = new ByteArrayInputStream(error_doc
                        .getBytes());

                read_bytes = error_doc.getBytes().length;
                doc_type = "html";
                return ba_in;
            }
        }
    }

    private InputStream generateSearchResult(HashMap post_keies,
            boolean isLogined) {
        if (post_keies != null) {
            Object tmp_keyword = post_keies.get("keyword");
            Object tmp_target = post_keies.get("target");
            if ((tmp_keyword != null)&&(tmp_target!= null)) {
                String keyword = (String) tmp_keyword;
                String target = (String) tmp_target;
                
                if(target.equals("hatebu")){
                    return generateHatebuKeywordHTML(keyword, isLogined);    
                }else if(target.equals("news")){
                    return generateCeekJPNewsKeywordHTML(keyword, isLogined);
                }else{
                    String error = new String(
                    "<html><head><title>Error!!</title></head><body><p></p>キーワード検索の対象が不正です</body></html>\r\n");
                    String error_doc = error;
                    ByteArrayInputStream ba_in = new ByteArrayInputStream(error_doc
                            .getBytes());
        
                    read_bytes = error_doc.getBytes().length;
                    doc_type = "html";
                    return ba_in;
                }
            } else {
                String error = new String(
                        "<html><head><title>Error!!</title></head><body><p></p>Error!!</body></html>\r\n");
                String error_doc = error;
                ByteArrayInputStream ba_in = new ByteArrayInputStream(error_doc
                        .getBytes());

                read_bytes = error_doc.getBytes().length;
                doc_type = "html";
                return ba_in;
            }
        } else {
            String error = new String(
                    "<html><head><title>Error!!</title></head><body><p></p>Error!!</body></html>\r\n");
            String error_doc = error;
            ByteArrayInputStream ba_in = new ByteArrayInputStream(error_doc
                    .getBytes());

            read_bytes = error_doc.getBytes().length;
            doc_type = "html";
            return ba_in;
        }
    }

    private InputStream generateEditPageHTML(HashMap post_keies,
            boolean isLogined) throws Exception {
        if (post_keies == null) { // GETの場合
            StringBuffer html_doc = new StringBuffer();

            html_doc
                    .append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n");
            html_doc.append("<html>\n");

            html_doc.append("<head>\n");
            html_doc
                    .append("<META http-equiv=\"Content-Type\" content=\"text/html; charset=shift_jis\">\n");
            html_doc
                    .append("<LINK href=\"http://ryogrid.myhome.cx/distribute/kikker_style.css\" type=text/css rel=stylesheet>");

            html_doc.append("<title>登録情報変更</title>\n");

            html_doc.append("</head>");
            html_doc.append("<body>");
            appendSHINOBI(html_doc, SHINOBI_OTHER);
            appendBunner(html_doc, "誰か", isLogined,BUNNER_TYPE_OTHER);

            html_doc
                    .append("<DIV id=container><DIV id=body><DIV class=option style=\"CLEAR: both\">");

            html_doc.append("<H3><A>登録情報変更</A></H3>");
            html_doc.append("<DIV class=optionsub>");
            html_doc.append("<br>");

            html_doc.append("<FORM action=http://" + WebServer.HOST_NAME + ":"
                    + WebServer.PORT + "/edit method=post>");
            html_doc.append("<H2 class=registdescription>ユーザID</H2>");
            html_doc
                    .append("<input type=\"text\" name=\"id\" size=\"40\"><br>");
            html_doc.append("<H2 class=registdescription>パスワード</H2>");
            html_doc
                    .append("<input type=\"text\" name=\"password\" size=\"40\"><br>");

            appendAdsence(html_doc);
            html_doc.append("<INPUT type=submit value=\" 確認 \">");
            html_doc.append("</FORM>");

            // optionsubとoption,container,bodyを閉じる
            html_doc.append("</div></div></div></div>");

            appendAnalytics(html_doc);
            html_doc.append("</body></html>\n");
            html_doc.append("\r\n");

            String doc = html_doc.toString();
            ByteArrayInputStream ba_in = new ByteArrayInputStream(doc
                    .getBytes());

            read_bytes = doc.getBytes().length;
            doc_type = "html";

            return ba_in;
        } else {// Postの場合
            String id_str = (String) post_keies.get("id");
            String passwd_str = (String) post_keies.get("password");

            if ((id_str == null) || (passwd_str == null)) {
                String error = new String(
                        "<html><head><title>Error!!</title></head><body><p>Your Input is ellegal!!</p></body></html>\r\n");
                String error_doc = error;
                ByteArrayInputStream ba_in = new ByteArrayInputStream(error_doc
                        .getBytes());

                read_bytes = error_doc.getBytes().length;
                doc_type = "html";
                return ba_in;
            }

            UserAdmin u_admin = DBUserAdmin.getInstance();

            // すでに同名のユーザが存在した場合
            if (u_admin.getUser(id_str) == null) {
                String error = new String(
                        "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"><html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=shift_jis\"><title>Error!!</title></head><body><p>"
                                + id_str
                                + "というユーザーは存在しません</p></body></html>\r\n");
                String error_doc = error;
                ByteArrayInputStream ba_in = new ByteArrayInputStream(error_doc
                        .getBytes());

                read_bytes = error_doc.getBytes().length;
                doc_type = "html";
                return ba_in;
            }

            // パスワードが間違っている場合
            UserProfile user_prof = u_admin.getUser(id_str);
            if (!user_prof.getPassword().equals(passwd_str)) {
                String error = new String(
                        "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"><html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=shift_jis\"><title>Error!!</title></head><body><p>パスワードが間違っています</p></body></html>\r\n");
                String error_doc = error;
                ByteArrayInputStream ba_in = new ByteArrayInputStream(error_doc
                        .getBytes());

                read_bytes = error_doc.getBytes().length;
                doc_type = "html";
                return ba_in;
            }

            // キーワードベクトル再編集のページを生成
            StringBuffer html_doc = new StringBuffer();

            html_doc
                    .append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n");
            html_doc.append("<html>\n");

            html_doc.append("<head>\n");
            html_doc
                    .append("<META http-equiv=\"Content-Type\" content=\"text/html; charset=shift_jis\">\n");
            html_doc
                    .append("<LINK href=\"http://ryogrid.myhome.cx/distribute/kikker_style.css\" type=text/css rel=stylesheet>");

            html_doc.append("<title>登録キーワード変更</title>\n");

            html_doc.append("</head>");
            html_doc.append("<body>");
            appendBunner(html_doc, id_str, isLogined,BUNNER_TYPE_OTHER);

            html_doc
                    .append("<DIV id=container><DIV id=body><DIV class=option style=\"CLEAR: both\">");

            html_doc.append("<H3><A>登録キーワード変更</A></H3>");
            html_doc.append("<DIV class=optionsub>");
            html_doc.append("<br>");

            html_doc.append("<FORM action=http://" + WebServer.HOST_NAME + ":"
                    + WebServer.PORT + "/update method=post>");
            html_doc.append("<INPUT type=hidden value=" + id_str + " name=id>");
            html_doc.append("<INPUT type=hidden value=" + passwd_str
                    + " name=password>");

            appendAdsence(html_doc);

            html_doc.append("<H2 class=registdescription>あなたの好きなキーワード</H2>");
            html_doc
                    .append("<TEXTAREA name=\"taste_vector\" rows=\"10\" cols=\"60\">"
                            + Util.convertTasteMapToStr(user_prof
                                    .getTasteVector()) + "</TEXTAREA><br>");

            html_doc
                    .append("<H2 class=registdescription>説明:  <em>SNS:50,Ajax:10,Java:820</em> というように<em>キーワード:値</em>のペアをカンマで区切って記入してください。そのとき、改行は入れず一行で書き連ねてください</H2>");
            html_doc
                    .append("<H2 class=registdescription>注意: 値は各々のキーワード間での比さえ正しければ、各々の値は1000付近でも0.1付近でも大丈夫です</H2>");

            html_doc.append("</select>");
            html_doc.append("<INPUT type=submit value=\" 更新 \">");
            html_doc.append("</FORM>");

            // optionsubとoption,container,bodyを閉じる
            html_doc.append("</div></div></div></div>");

            appendAnalytics(html_doc);
            html_doc.append("</body></html>\n");
            html_doc.append("\r\n");

            String doc = html_doc.toString();
            ByteArrayInputStream ba_in = new ByteArrayInputStream(doc
                    .getBytes());

            read_bytes = doc.getBytes().length;
            doc_type = "html";

            return ba_in;
        }
    }

    private InputStream generateRegistPageHTML(HashMap post_keies,
            boolean isLogined) throws Exception {
        if (post_keies == null) { // GETの場合
            StringBuffer html_doc = new StringBuffer();

            html_doc
                    .append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n");
            html_doc.append("<html>\n");

            html_doc.append("<head>\n");
            html_doc
                    .append("<META http-equiv=\"Content-Type\" content=\"text/html; charset=shift_jis\">\n");
            html_doc
                    .append("<LINK href=\"http://ryogrid.myhome.cx/distribute/kikker_style.css\" type=text/css rel=stylesheet>");

            html_doc.append("<title>Kikker 新規ユーザー登録</title>\n");

            html_doc.append("</head>");
            html_doc.append("<body>");
            appendBunner(html_doc, "誰か", isLogined,BUNNER_TYPE_OTHER);
            appendSHINOBI(html_doc, SHINOBI_REGIST);

            // html_doc.append("<LINK rel=\"alternate\"
            // type=\"application/rss+xml\" title=\"キーワード『" + selected_keyword +
            // "』を含むページ\" href=\"http://" + WebServer.HOST_NAME + ":" +
            // WebServer.PORT + "/?keyword=" + Util.getQueryString(keyword) +
            // "&amp;type=rss\"></head>\n<body>");
            html_doc
                    .append("<DIV id=container><DIV id=body><DIV class=option style=\"CLEAR: both\">");

            html_doc.append("<H3><A>Kikker 新規ユーザー登録</A></H3>");
            html_doc.append("<DIV class=optionsub>");
            // html_doc.append("<h3><em>Kikker Web Service
            // 新規ユーザー登録</em></h3><br>\n");
            html_doc.append("<br>");

            html_doc.append("<FORM action=http://" + WebServer.HOST_NAME + ":"
                    + WebServer.PORT + "/regist method=post>");
            // html_doc.append("<em>ユーザID</em><br>");
            html_doc.append("<H2 class=registdescription>あなたが希望するユーザID</H2>");
            html_doc
                    .append("<input type=\"text\" name=\"id\" size=\"40\"><br>");
            html_doc.append("<H2 class=registdescription>あなたが希望するパスワード</H2>");
            // html_doc.append("<em>パスワード</em><br>");
            html_doc
                    .append("<input type=\"text\" name=\"password\" size=\"40\"><br>");
            // html_doc.append("<em>あなたの好きなキーワードベクトル</em><br>");

            appendAdsence(html_doc);
            html_doc
                    .append("<H2 class=registdescription>あなたの好きな言葉とどれぐらい好きかの登録</H2>");
            html_doc
                    .append("<TEXTAREA name=\"taste_vector\" rows=\"10\" cols=\"60\"></TEXTAREA><br>");
            // html_doc.append("説明: <em>SNS:50,Ajax:10,Java:820</em>
            // というように<em>キーワード:値</em>のペアをカンマで区切って記入してください。そのとき、改行は入れず一行で書き連ねてください<br>");
            // html_doc.append("注意:
            // 値は各々のキーワード間での比さえ正しければ、各々の値は1000付近でも0.1付近でも,というかどうでもいいです<br>");
            html_doc
                    .append("<H2 class=registdescription>説明:　<font size=\"4\"><em>SNS:50,Ajax:10,Java:820</em></font>　というように　<font size=\"4\"><em>キーワード:あなたがどれぐらいその言葉が気になるかを表した好きな数値</em></font>　のペアを　<font size=\"4\"><em>カンマ(,)</em></font>　で区切って記入してください。改行は入れないで下さい</H2>");
            html_doc
                    .append("<H2 class=registdescription>例:　車の事が大好きで、花が好きで、お菓子がちょこっと好きなRyo君だったら　<font size=\"4\"><em>車:1000,花:500,お菓子:100</em></font>　というように上の記入ボックスに打ち込めばいい</H2>");
            // html_doc.append("<H2 class=registdescription>注意:
            // 値は各々のキーワード間での比さえ正しければ、各々の値は1000付近でも0.1付近でも大丈夫です</H2>");

            html_doc.append("</select>");
            html_doc.append("<INPUT type=submit value=\" 登録 \">");
            html_doc.append("</FORM>");

            // optionsubとoption,container,bodyを閉じる
            html_doc.append("</div></div></div></div>");

            appendAnalytics(html_doc);
            html_doc.append("</body></html>\n");
            html_doc.append("\r\n");

            String doc = html_doc.toString();
            ByteArrayInputStream ba_in = new ByteArrayInputStream(doc
                    .getBytes());

            read_bytes = doc.getBytes().length;
            doc_type = "html";

            return ba_in;
        } else {// Postの場合
            String id_str = (String) post_keies.get("id");
            String passwd_str = (String) post_keies.get("password");
            String taste_str = (String) post_keies.get("taste_vector");

            if ((id_str == null) || (passwd_str == null) || (taste_str == null)) {
                String error = new String(
                        "<html><head><title>Error!!</title></head><body><p>Id or Passowrd of Keywords is null</p></body></html>\r\n");
                String error_doc = error;
                ByteArrayInputStream ba_in = new ByteArrayInputStream(error_doc
                        .getBytes());

                read_bytes = error_doc.getBytes().length;
                doc_type = "html";
                return ba_in;
            }

            UserAdmin u_admin = DBUserAdmin.getInstance();

            // すでに同名のユーザが存在した場合
            if (u_admin.getUser(id_str) != null) {
                String error = new String(
                        "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"><html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=shift_jis\"><title>Error!!</title></head><body><p>あなたが指定したIDはすでに使われています。他のIDを指定してもう一度登録して下さい</p></body></html>\r\n");
                String error_doc = error;
                ByteArrayInputStream ba_in = new ByteArrayInputStream(error_doc
                        .getBytes());

                read_bytes = error_doc.getBytes().length;
                doc_type = "html";
                return ba_in;
            }

            HashMap taste_vec = parseToVector(taste_str);

            UserProfile new_user = new UserProfile("", id_str, "", 0, "",
                    passwd_str);
            new_user.setTaste_vector(taste_vec);
            new_user.setRegistDate(new Date());
            new_user.setLastCached(new Date());
            new_user.setPast_read_webs(new Vector());

            u_admin.createNewUser(new_user);

            String doc_str = new String(
                    "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"><html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=shift_jis\"><title>登録成功!!</title></head><body><p><em>ID:</em>"
                            + id_str
                            + " <em>パスワード:</em>"
                            + passwd_str
                            + "で登録に成功しました</p><br><a href=\""
                            + Util.HTMLEscape("/?id=" + id_str + "&type=html&category=hatebu")
                            + "\"><font size=\"4\"><b>"
                            + id_str
                            + " のページはこちら</b></font></a><br>");
            StringBuffer html_doc = new StringBuffer(doc_str);
            appendAdsence(html_doc);
            appendAnalytics(html_doc);
            html_doc.append("</body></html>");
            doc_str = html_doc.toString();
            ByteArrayInputStream ba_in = new ByteArrayInputStream(doc_str
                    .getBytes());

            read_bytes = doc_str.getBytes().length;
            doc_type = "html";
            return ba_in;
        }
    }

    // フォームから入力されてきた趣向情報をVectorへ変換
    private HashMap parseToVector(String taste_str) throws Exception {
        HashMap result_map = new HashMap();

        String splited[] = taste_str.split(",");
        int len = splited.length;
        for (int i = 0; i < len; i++) {
            String key_and_value[] = splited[i].split(":");
            if (key_and_value.length == 2) {
                result_map.put(key_and_value[0], Double
                        .valueOf(key_and_value[1]));
            } else {
                throw new Exception("キーワードフォームの入力が不正です");
            }
        }

        return result_map;
    }

    private InputStream generateTopPageHTML(boolean isLogined) {
        StringBuffer html_doc = new StringBuffer();

        html_doc
                .append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n");
        html_doc.append("<html>\n");

        html_doc.append("<head>\n");
        html_doc
                .append("<META http-equiv=\"Content-Type\" content=\"text/html; charset=shift_jis\">\n");

        html_doc.append("<title>Kikker-学習するユーザーカスタマイズドなニュースサイト-</title>\n");
        html_doc
                .append("<LINK href=\"http://ryogrid.myhome.cx/distribute/kikker_style.css\" type=text/css rel=stylesheet>");
        html_doc.append("</head>\n<body>");
        appendBunner(html_doc, "誰か", isLogined,BUNNER_TYPE_OTHER);
        appendSHINOBI(html_doc, SHINOBI_TOP);
        
        // 登録ユーザーのリストを出力
        html_doc.append("<DIV id=container>");

        html_doc.append("<DIV id=body>");

        // 紹介文を出力
        appendIntoroduction(html_doc);
        // 検索ボックスを出力
        appendKeySearchBox(html_doc);

        html_doc.append("<DIV class=option style=\"CLEAR: both\">");
        
//      実際にリストを出力
        UserAdmin u_admin = DBUserAdmin.getInstance();
        UserProfile profiles[] = u_admin.getAllUser();
        
        html_doc.append("<H3><A>現在登録されているユーザ(" + profiles.length  + "人)</A></H3>");
        html_doc.append("<DIV class=optionsub>");

        appendAdsenceLect(html_doc);
        int len = profiles.length;
        for (int i = 0; i < len; i++) {
            String user_id = profiles[i].getId();
            html_doc.append("<font size=\"4\"><a href=\""
                    + Util.HTMLEscape("/?id=" + user_id + "&type=html&category=hatebu") + "\">"
                    + user_id + " さん</a></font><br>\n");
        }

        // optionsubとoption,container,bodyを閉じる
        html_doc.append("</div></div></div></div>");
        
        html_doc.append("<center><h2>Today's Page View is <font color=\"red\" size=\"+2\">" + CounterManager.getInstance().getHtmlViewCount() + "</font> ,RSS View is <font color=\"red\" size=\"+2\">" +   CounterManager.getInstance().getRssViewCount() +  "</font> and Kikker API has called <font color=\"red\" size=\"+2\">" + CounterManager.getInstance().getAPIUsedCount() + "</font> times.</h2></center>");
        html_doc.append("<center><p>Copyright (C) 2005-2006 <a href=\"http://d.hatena.ne.jp/kanbayashi\">Ryo</a> , All Right Reserved.</p></center>");

        appendAnalytics(html_doc);
        
        html_doc.append("</body></html>\n");
        html_doc.append("\r\n");

        String doc = html_doc.toString();
        ByteArrayInputStream ba_in = new ByteArrayInputStream(doc.getBytes());

        read_bytes = doc.getBytes().length;
        doc_type = "html";

        return ba_in;
    }

    private void appendIntoroduction(StringBuffer html_doc) {
        // html_doc.append("<H2 class=topdescription><img
        // src=\"http://ryogrid.myhome.cx/favicon.ico\">Kikkerは各ユーザーへのホットなページを自動で整理し教えてくれる便利なサービスです。</H2>");
        html_doc
                .append("<H2 class=topdescription>Kikkerは各ユーザーへのホットなページを自動で整理し教えてくれる便利なサービスです。</H2>");
        html_doc.append("<DIV id=tophead>");
        html_doc.append("<DL id=catch>");
        html_doc.append("<DT><p>あなた<strong>専用</strong>のおすすめページをお届け</p>");
        html_doc
                .append("<DD>いくつかのキーワードを登録するとそれらのキーワードから判断したあなたの特徴を元にKikkerがおすすめページを探してきます</a>");
        html_doc.append("<DT><p>気になるキーワードを登録</p>");
        html_doc.append("<DD><a href=\"http://" + WebServer.HOST_NAME + ":"
                + WebServer.PORT + "/regist\">登録作業はWeb上で簡単・ラクチン</a>");
        html_doc.append("<DT><p>Kikkerはいつもあなたの事をお勉強</p>");
        html_doc
                .append("<DD><a><a href=\"http://"
                        + WebServer.HOST_NAME
                        + ":"
                        + WebServer.PORT
                        + "/login\">ログイン</a>した状態でリンクをクリックするとKikkerはリンク先のページの内容からあなたの好みを学習します.つまり,毎日Kikkerを使うだけで,よりあなた好みの情報が集まってくる!!</a>");
        html_doc
                .append("<DT><a href=\"http://b.hatena.ne.jp/keyword\">キーワード</a>でつながる ");
        html_doc.append("<DD>キーワードを通じて,内容が似ているページや,趣向が似ているユーザーを見つけることができます");
        html_doc.append("<DT><a href=\"http://d.hatena.ne.jp/kanbayashi/20060611#p7\">RSSリーダで毎日のホットな情報を購読</a>");
        html_doc.append("<DD>KikkerのRSSを各種RSSリーダで購読すればあなたの毎日の情報収集のコストをさらにグッと下げることができます");
        html_doc.append("<DT>Youtubeのビデオにも対応しました <font color=orange>7/21 New!!</font>");
        html_doc.append("<DD>ユーザごとのページ右上の「My Videoへ」もしくは「お薦めVideoへ」をクリックしてみて下さい。Youtube専用のアカウントを作って試すことをお薦めします。");
        html_doc
                .append("<P style=\"FONT-SIZE: 80%; LINE-HEIGHT: 135%\">※使い方などの詳細は<A");
        html_doc
                .append(" href=\"http://ryogrid.myhome.cx/wiki/pukiwiki.php?Kikker\">Kikker Wiki</A>、");
        html_doc
                .append("<P style=\"FONT-SIZE: 80%; LINE-HEIGHT: 135%\">※ご意見・ご要望は<A");
        html_doc
                .append(" href=\"http://ryogrid.myhome.cx/wiki/pukiwiki.php?%CD%D7%CB%BE%B7%C7%BC%A8%C8%C4\">要望掲示板</A></P>");
        html_doc
        .append("<P style=\"FONT-SIZE: 80%; LINE-HEIGHT: 135%\">※よくある質問と回答は<A");
html_doc
        .append(" href=\"http://ryogrid.myhome.cx/wiki/pukiwiki.php?%A4%E8%A4%AF%A4%A2%A4%EB%BC%C1%CC%E4\">よくある質問</A>");
        html_doc.append("</P></DIV>");
    }

    private InputStream generateHatebuKeywordRSS(String keyword) {
//      rssのカウンタを回す
        CounterManager.getInstance().incRSSCount();
        
        WebMetadataAdmin w_admin = FileWebMetadataAdmin.getInstance();
        StoreBoxForWebData sugguested_webs[] = w_admin
                .getTagetKeywordBelongs(keyword);

        String rssStr = RssGenerator.generateHatebuKeywordRSS(keyword,
                sugguested_webs);
        String rss_doc = rssStr.toString();
        ByteArrayInputStream ba_in = null;

        ba_in = new ByteArrayInputStream(rss_doc.getBytes());

        read_bytes = rss_doc.getBytes().length;
        doc_type = "rss";
        return ba_in;
    }
    
    private InputStream generateCeekJPNewsKeywordRSS(String keyword) {
//      rssのカウンタを回す
        CounterManager.getInstance().incRSSCount();
        
        WebMetadataAdmin w_admin = FileWebMetadataAdmin.getInstance();
        StoreBoxForWebData sugguested_webs[] = w_admin
                .getTagetKeywordBelongs(keyword);

        String rssStr = RssGenerator.generateCeekJPNewsKeywordRSS(keyword,
                sugguested_webs);
        String rss_doc = rssStr.toString();
        ByteArrayInputStream ba_in = null;

        ba_in = new ByteArrayInputStream(rss_doc.getBytes());

        read_bytes = rss_doc.getBytes().length;
        doc_type = "rss";
        return ba_in;
    }
    
    private InputStream generateHatebuKeywordHTML(String keyword, boolean isLogined) {
        String selected_keyword = Util.decodeQueryStringUTF8(keyword);

        WebMetadataAdmin w_admin = FileWebMetadataAdmin.getInstance();

        StringBuffer html_doc = new StringBuffer();

        html_doc
                .append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n");
        html_doc.append("<html>\n");

        html_doc.append("<head>\n");
        appendCSS(html_doc);
        html_doc
                .append("<META http-equiv=\"Content-Type\" content=\"text/html; charset=shift_jis\">\n");

        html_doc.append("<title>キーワード『" + selected_keyword
                + "』を含むページ</title>\n");
        html_doc
                .append("<LINK rel=\"alternate\" type=\"application/rss+xml\" title=\"キーワード『"
                        + selected_keyword
                        + "』を含むページ\" href=\"http://"
                        + WebServer.HOST_NAME
                        + ":"
                        + WebServer.PORT
                        + "/?keyword="
                        + Util.getQueryString(keyword)
                        + "&amp;type=rss&amp;category=hatebu\">");
        html_doc
                .append("<LINK href=\"http://ryogrid.myhome.cx/distribute/kikker_style.css\" type=text/css rel=stylesheet>");
        html_doc.append("</head>\n<body>");

        appendBunner(html_doc, "誰か", isLogined,BUNNER_TYPE_OTHER);
        appendSHINOBI(html_doc, SHINOBI_KEYWORD);

        html_doc
                .append("<a href=\"http://"
                        + WebServer.HOST_NAME
                        + ":"
                        + WebServer.PORT
                        + "/?keyword="
                        + Util.getQueryString(keyword)
                        + "&amp;type=rss&amp;category=hatebu\"><img src=\"http://ryogrid.myhome.cx/distribute/rss_icon.gif\"></a>\n");
        html_doc.append("<br>\n");

        // はてブからのものも追加する
        html_doc
                .append("<DIV id=container><DIV id=body><DIV class=option style=\"CLEAR: both\">");
        
        html_doc.append("<H3><A>はてブから探した『" + selected_keyword
                + "』を含むページ</A>&nbsp;<A href=\"/?keyword=" + Util.getQueryString(selected_keyword) + "&amp;type=html&category=news\">NEWSはコチラ</A></H3>");    

        
        html_doc.append("<DIV class=optionsub>");

        appendAdsenceLect(html_doc);
        CrawledDataManager clawle_manager = DBCrawledDataManager.getInstance();
        StoreBoxForDocumentEntry[] entries = clawle_manager
            .getTagetKeywordBelongs(selected_keyword,DBUtil.HATEBU_TYPE);

        for (int i = 0; i < entries.length; i++) {
            String link_address = entries[i].data.getAddress();
            String title = entries[i].data.getTitle();
            int user_view_count = entries[i].data.getView_users();
            Date crawled_date = entries[i].data.getClawledDate();
            if (isLogined == true) {
                html_doc
                        .append("<DIV class=entry><SPAN class=entry-body><a class=bookmark href=\"http://"
                                + WebServer.HOST_NAME
                                + ":"
                                + WebServer.PORT
                                + "/learn?url="
                                + Util.HTMLEscape(Util.EscapeSharp(link_address + "&category=hatebu"))
                                + "\" target=\"_blank\">"
                                + Util
                                        .HTMLEscape(((title == null) ? link_address
                                                : title))
                                + "</a></SPAN>&nbsp;<SPAN class=entry-footer><strong><a href=\"http://b.hatena.ne.jp/entry/"
                                + Util.HTMLEscape(link_address)
                                + "\">"
                                + user_view_count
                                + "users</a></strong></span><SPAN class=entry-footer>&nbsp;"
                                + (crawled_date.getYear() + 1900)
                                + "年"
                                + (crawled_date.getMonth() + 1)
                                + "月"
                                + crawled_date.getDate() + "日</SPAN><br>\n");
            } else {
                html_doc
                        .append("<DIV class=entry><SPAN class=entry-body><a class=bookmark href=\""
                                + Util.HTMLEscape(link_address)
                                + "\" target=\"_blank\">"
                                + Util
                                        .HTMLEscape(((title == null) ? link_address
                                                : title))
                                + "</a></SPAN>&nbsp;<SPAN class=entry-footer><strong><a href=\"http://b.hatena.ne.jp/entry/"
                                + Util.HTMLEscape(link_address)
                                + "\">"
                                + user_view_count
                                + "users</a></strong></span><SPAN class=entry-footer>&nbsp;"
                                + (crawled_date.getYear() + 1900)
                                + "年"
                                + (crawled_date.getMonth() + 1)
                                + "月"
                                + crawled_date.getDate() + "日</SPAN><br>\n");
            }

            // Suggestするページに付加するキーワード文字列を作成
            HashMap keywords = entries[i].data.getKeywords();
            ArrayList sorted_web_vec = Util.getSortedList(keywords);
            StringBuffer buf = new StringBuffer();
            int web_key_len = sorted_web_vec.size();
            for (int j = 0; j < ((web_key_len > KikkerConfigration.ALIGN_KEYWORD_LIMIT)?KikkerConfigration.ALIGN_KEYWORD_LIMIT : web_key_len); j++) {
                buf
                        .append("<a class=keyword href=\"http://"
                                + WebServer.HOST_NAME
                                + ":"
                                + WebServer.PORT
                                + "/?keyword="
                                + Util
                                        .getQueryString(((KeyAndDoubleTFIDF) (sorted_web_vec
                                                .get(j))).keyword)
                                + "&amp;type=html&amp;category=hatebu\">"
                                + ((KeyAndDoubleTFIDF) (sorted_web_vec.get(j))).keyword
                                + "</a>&nbsp;");
            }
            String key_list_str = buf.toString();

            html_doc.append("<SPAN class=entry-footer>キーワード: " + key_list_str
                    + "</span></div>\n");

        }
        appendAmazon(html_doc);
        // optionsubとoption,container,bodyを閉じる
        html_doc.append("</div></div></div></div>");

        appendAnalytics(html_doc);
        html_doc.append("</body></html>\n");
        html_doc.append("\r\n");

        String doc = html_doc.toString();
        ByteArrayInputStream ba_in = new ByteArrayInputStream(doc.getBytes());

        read_bytes = doc.getBytes().length;
        doc_type = "html";

        return ba_in;
    }
    
    private InputStream generateCeekJPNewsKeywordHTML(String keyword, boolean isLogined) {
        String selected_keyword = Util.decodeQueryStringUTF8(keyword);

        WebMetadataAdmin w_admin = FileWebMetadataAdmin.getInstance();

        StringBuffer html_doc = new StringBuffer();

        html_doc
                .append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n");
        html_doc.append("<html>\n");

        html_doc.append("<head>\n");
        appendCSS(html_doc);
        html_doc
                .append("<META http-equiv=\"Content-Type\" content=\"text/html; charset=shift_jis\">\n");

        html_doc.append("<title>キーワード『" + selected_keyword
                + "』を含むページ</title>\n");
        html_doc
                .append("<LINK rel=\"alternate\" type=\"application/rss+xml\" title=\"キーワード『"
                        + selected_keyword
                        + "』を含むページ\" href=\"http://"
                        + WebServer.HOST_NAME
                        + ":"
                        + WebServer.PORT
                        + "/?keyword="
                        + Util.getQueryString(keyword)
                        + "&amp;type=rss&amp;category=news\">");
        html_doc
                .append("<LINK href=\"http://ryogrid.myhome.cx/distribute/kikker_style.css\" type=text/css rel=stylesheet>");
        html_doc.append("</head>\n<body>");

        appendBunner(html_doc, "誰か", isLogined,BUNNER_TYPE_OTHER);
        appendSHINOBI(html_doc, SHINOBI_KEYWORD);

        html_doc
                .append("<a href=\"http://"
                        + WebServer.HOST_NAME
                        + ":"
                        + WebServer.PORT
                        + "/?keyword="
                        + Util.getQueryString(keyword)
                        + "&amp;type=rss&amp;category=news\"><img src=\"http://ryogrid.myhome.cx/distribute/rss_icon.gif\"></a>\n");
        html_doc.append("<br>\n");

        html_doc
                .append("<DIV id=container><DIV id=body><DIV class=option style=\"CLEAR: both\">");
        
        html_doc.append("<H3><A>CEEK.JP NEWSから探した『" + selected_keyword
                + "』を含むページ</A>&nbsp;<A href=\"/?keyword=" + Util.getQueryString(selected_keyword) + "&amp;type=html&category=hatebu\">はてブはコチラ</A></H3>");    

        
        html_doc.append("<DIV class=optionsub>");

        appendAdsenceLect(html_doc);
        CrawledDataManager clawle_manager = DBCrawledDataManager.getInstance();
        StoreBoxForDocumentEntry[] entries = clawle_manager
            .getTagetKeywordBelongs(selected_keyword,DBUtil.CEEK_NEWS_TYPE);

        for (int i = 0; i < entries.length; i++) {
            String link_address = entries[i].data.getAddress();
            String title = entries[i].data.getTitle();
            int user_view_count = entries[i].data.getView_users();
            Date crawled_date = entries[i].data.getClawledDate();
            if (isLogined == true) {
                html_doc
                        .append("<DIV class=entry><SPAN class=entry-body><a class=bookmark href=\"http://"
                                + WebServer.HOST_NAME
                                + ":"
                                + WebServer.PORT
                                + "/learn?url="
                                + Util.HTMLEscape(Util.EscapeSharp(link_address + "&category=news"))
                                + "\" target=\"_blank\">"
                                + Util
                                        .HTMLEscape(((title == null) ? link_address
                                                : title))
                                + "</a></SPAN>&nbsp;<SPAN class=entry-footer>"
                                + (crawled_date.getYear() + 1900)
                                + "年"
                                + (crawled_date.getMonth() + 1)
                                + "月"
                                + crawled_date.getDate() + "日</SPAN><br>\n");
            } else {
                html_doc
                        .append("<DIV class=entry><SPAN class=entry-body><a class=bookmark href=\""
                                + Util.HTMLEscape(link_address)
                                + "\" target=\"_blank\">"
                                + Util
                                        .HTMLEscape(((title == null) ? link_address
                                                : title))
                                + "</a></SPAN>&nbsp;<SPAN class=entry-footer>&nbsp;"
                                + (crawled_date.getYear() + 1900)
                                + "年"
                                + (crawled_date.getMonth() + 1)
                                + "月"
                                + crawled_date.getDate() + "日</SPAN><br>\n");
            }

            // Suggestするページに付加するキーワード文字列を作成
            HashMap keywords = entries[i].data.getKeywords();
            ArrayList sorted_web_vec = Util.getSortedList(keywords);
            StringBuffer buf = new StringBuffer();
            int web_key_len = sorted_web_vec.size();
            for (int j = 0; j < ((web_key_len > KikkerConfigration.ALIGN_KEYWORD_LIMIT)?KikkerConfigration.ALIGN_KEYWORD_LIMIT : web_key_len); j++) {
                buf
                        .append("<a class=keyword href=\"http://"
                                + WebServer.HOST_NAME
                                + ":"
                                + WebServer.PORT
                                + "/?keyword="
                                + Util
                                        .getQueryString(((KeyAndDoubleTFIDF) (sorted_web_vec
                                                .get(j))).keyword)
                                + "&amp;type=html&amp;category=news\">"
                                + ((KeyAndDoubleTFIDF) (sorted_web_vec.get(j))).keyword
                                + "</a>&nbsp;");
            }
            String key_list_str = buf.toString();

            html_doc.append("<SPAN class=entry-footer>キーワード: " + key_list_str
                    + "</span></div>\n");

        }
        appendAmazon(html_doc);
        // optionsubとoption,container,bodyを閉じる
        html_doc.append("</div></div></div></div>");

        appendAnalytics(html_doc);
        html_doc.append("</body></html>\n");
        html_doc.append("\r\n");

        String doc = html_doc.toString();
        ByteArrayInputStream ba_in = new ByteArrayInputStream(doc.getBytes());

        read_bytes = doc.getBytes().length;
        doc_type = "html";

        return ba_in;
    }

    private void appendAnalytics(StringBuffer html_doc) {
        html_doc
                .append("<script src=\"http://www.google-analytics.com/urchin.js\" type=\"text/javascript\">");
        html_doc.append("</script>");
        html_doc.append("<script type=\"text/javascript\">");
        html_doc.append("_uacct = \"UA-55410-3\";");
        html_doc.append("urchinTracker();");
        html_doc.append("</script>");
    }

    private InputStream generateSugguestRSS(Object obj) {
//      rssのカウンタを回す
        CounterManager.getInstance().incRSSCount();
        
        WebMetadataAdmin w_admin = FileWebMetadataAdmin.getInstance();
        SearchResult sugguested = w_admin.search(((UserProfile) obj)
                .getTasteVector());
        UserProfile profile = (UserProfile) obj;

        String rssStr = RssGenerator.generateHatebuSuggestRSS(profile, sugguested);
        String rss_doc = rssStr.toString();
        ByteArrayInputStream ba_in = null;

        ba_in = new ByteArrayInputStream(rss_doc.getBytes());

        read_bytes = rss_doc.getBytes().length;
        doc_type = "rss";
        return ba_in;
    }
    
    private InputStream generateYoutubeRSS(Object obj) {
//      rssのカウンタを回す
        CounterManager.getInstance().incRSSCount();
        
        WebMetadataAdmin w_admin = FileWebMetadataAdmin.getInstance();
        SearchResult sugguested = w_admin.search(((UserProfile) obj)
                .getTasteVector());
        UserProfile profile = (UserProfile) obj;

        String rssStr = RssGenerator.generateYoutubeSuggestRSS(profile, sugguested);
        String rss_doc = rssStr.toString();
        ByteArrayInputStream ba_in = null;

        ba_in = new ByteArrayInputStream(rss_doc.getBytes());

        read_bytes = rss_doc.getBytes().length;
        doc_type = "rss";
        return ba_in;
    }
    
    private InputStream generateSugguestHTML(String id, Object obj,
            boolean isLogined) {
        UserProfile profile = (UserProfile) obj;

        StringBuffer html_doc = new StringBuffer();

        html_doc
                .append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n");
        html_doc.append("<html>\n");

        html_doc.append("<head>\n");
        appendCSS(html_doc);
        html_doc
                .append("<META http-equiv=\"Content-Type\" content=\"text/html; charset=shift_jis\">\n");

        html_doc.append("<title>Links Suggest to " + id + "</title>\n");
        html_doc
                .append("<LINK rel=\"alternate\" type=\"application/rss+xml\" title=\"Links Suggest to "
                        + id
                        + "\" href=\"http://"
                        + WebServer.HOST_NAME
                        + ":"
                        + WebServer.PORT
                        + "/?id="
                        + profile.getId()
                        + "&amp;type=rss&amp;category=hatebu\">");
        html_doc
                .append("<LINK href=\"http://ryogrid.myhome.cx/distribute/kikker_style.css\" type=text/css rel=stylesheet>");
        html_doc.append("</head>\n<body>");

        appendBunner(html_doc, profile.getId(), isLogined,BUNNER_TYPE_HATEBU);
        appendSHINOBI(html_doc, SHINOBI_SUGGEST);
        html_doc
                .append("<a href=\"http://"
                        + WebServer.HOST_NAME
                        + ":"
                        + WebServer.PORT
                        + "/?id="
                        + profile.getId()
                        + "&amp;type=rss&amp;category=hatebu\"><img src=\"http://ryogrid.myhome.cx/distribute/rss_icon.gif\"></a>\n");

        html_doc
                .append("<DIV id=container><DIV id=body><DIV class=option style=\"CLEAR: both\">");

        html_doc.append("<H3><A>はてブから探した" + id + "におすすめのページ</A></H3>");
        html_doc.append("<DIV class=optionsub>");
        appendAdsenceLect(html_doc);

        CrawledDataManager c_manager = DBCrawledDataManager.getInstance();
        DocumentSearchResult hatebu_sugguested = c_manager
                .search(((UserProfile) obj).getTasteVector(),DBUtil.HATEBU_TYPE);
        for (int i = 0; i < (hatebu_sugguested.results.length > KikkerConfigration.ALIGN_HATEBU_HTML_LIMIT ? KikkerConfigration.ALIGN_HATEBU_HTML_LIMIT
                : hatebu_sugguested.results.length); i++) {
            String link_address = hatebu_sugguested.results[i].getAddress();
            String title = hatebu_sugguested.results[i].getTitle();
            Date crawled_date = hatebu_sugguested.results[i].getClawledDate();
            int user_view_count = hatebu_sugguested.results[i].getView_users();

            if (isLogined) {
                html_doc
                        .append("<DIV class=entry><SPAN class=entry-body><a class=bookmark href=\"http://"
                                + WebServer.HOST_NAME
                                + ":"
                                + WebServer.PORT
                                + "/learn?url="
                                + Util.HTMLEscape(Util.EscapeSharp(link_address + "&category=hatebu"))
                                + "\" target=\"_blank\">"
                                + Util
                                        .HTMLEscape(((title == null) ? link_address
                                                : title))
                                + "</a></SPAN>&nbsp;<SPAN class=entry-footer><strong><a>"
                                + ((int) Math
                                        .floor(hatebu_sugguested.eval_points[i] * 100.0))
                                + "point</a></strong> <strong><a href=\"http://b.hatena.ne.jp/entry/"
                                + Util.HTMLEscape(link_address)
                                + "\">"
                                + user_view_count
                                + "users</a></STRONG></SPAN><SPAN class=entry-footer>&nbsp;"
                                + (crawled_date.getYear() + 1900)
                                + "年"
                                + (crawled_date.getMonth() + 1)
                                + "月"
                                + crawled_date.getDate() + "日</SPAN><br>\n");
            } else {
                html_doc
                        .append("<DIV class=entry><SPAN class=entry-body><a class=bookmark href=\""
                                + Util.HTMLEscape(link_address)
                                + "\" target=\"_blank\">"
                                + Util
                                        .HTMLEscape(((title == null) ? link_address
                                                : title))
                                + "</a></SPAN>&nbsp;<SPAN class=entry-footer><strong><a>"
                                + ((int) Math
                                        .floor(hatebu_sugguested.eval_points[i] * 100.0))
                                + "point</a></strong> <strong><a href=\"http://b.hatena.ne.jp/entry/"
                                + Util.HTMLEscape(link_address)
                                + "\">"
                                + user_view_count
                                + "users</a></STRONG></SPAN><SPAN class=entry-footer>&nbsp;"
                                + (crawled_date.getYear() + 1900)
                                + "年"
                                + (crawled_date.getMonth() + 1)
                                + "月"
                                + crawled_date.getDate() + "日</SPAN><br>\n");
            }

            // Suggestするページに付加するキーワード文字列を作成
            HashMap keywords = hatebu_sugguested.results[i].getKeywords();
            List list = Collections.list(Collections.enumeration(keywords
                    .keySet()));
            StringBuffer buf = new StringBuffer();
            int web_key_len = list.size();
            for (int j = 0; j < ((web_key_len > KikkerConfigration.ALIGN_KEYWORD_LIMIT)?KikkerConfigration.ALIGN_KEYWORD_LIMIT : web_key_len); j++) {
                buf.append("<a class=keyword href=\"http://"
                        + WebServer.HOST_NAME + ":" + WebServer.PORT
                        + "/?keyword="
                        + Util.getQueryString((String) list.get(j))
                        + "&amp;type=html&amp;category=hatebu\">" + (String) list.get(j)
                        + "</a>&nbsp;");
            }
            String key_list_str = buf.toString();

            html_doc.append("<SPAN class=entry-footer>キーワード: " + key_list_str
                    + "</SPAN></DIV>\n");
        }

        // optionsubとoption,container,bodyを閉じる
        html_doc.append("</div></div></div></div>");


//        // 類似する趣向を持つユーザーを列挙
//
//        html_doc
//                .append("<DIV id=container><DIV id=body><DIV class=option style=\"CLEAR: both\">");
//
//        html_doc.append("<H3><A>" + id + "と同じような趣味の人たち</A></H3>");
//        html_doc.append("<DIV class=optionsub>");
//
//        UserAdmin u_admin = DBUserAdmin.getInstance();
//        UserSearchResult user_sugguested = u_admin.search(((UserProfile) obj)
//                .getTasteVector());
//        for (int i = 0; i < (user_sugguested.users.length > 100 ? 100
//                : user_sugguested.users.length); i++) {
//            if (!(user_sugguested.users[i].getId()).equals(((UserProfile) obj)
//                    .getId())) {
//                String sugguested_user_id = user_sugguested.users[i].getId();
//                html_doc
//                        .append("<span class=entry><SPAN class=entry-body><a class=bookmark href=\""
//                                + Util.HTMLEscape("/?id="
//                                        + user_sugguested.users[i].getId()
//                                        + "&type=html&category=hatebu")
//                                + "\">"
//                                + sugguested_user_id
//                                + " さん</a></span></span><br>\n");
//            }
//        }
//
//        // optionsubとoption,container,bodyを閉じる
//        html_doc.append("</div></div></div></div>");

        // html_doc.append("<br>\n");
        // html_doc.append("<br>\n");

        // キーワードベクトルを表示する
        html_doc
                .append("<DIV id=container><DIV id=body><DIV class=option style=\"CLEAR: both\">");

        html_doc.append("<H3><A>" + id + "の気になっているキーワード</A></H3>");
        html_doc.append("<DIV class=optionsub>");
        appendAdsenceLect(html_doc);

        HashMap taste_vec = ((UserProfile) obj).getTasteVector();
        ArrayList sorted_vec = Util.getSortedList(taste_vec);

        for (int i = 0; i < ((sorted_vec.size() > KikkerConfigration.ALIGN_USER_KEYWORDS)? KikkerConfigration.ALIGN_USER_KEYWORDS : sorted_vec.size()) ; i++) {
            KeyAndDoubleTFIDF key_tfidf = (KeyAndDoubleTFIDF) sorted_vec.get(i);
            String keyword = key_tfidf.keyword;
            double tfidf = key_tfidf.tfidf.doubleValue();

            html_doc
                    .append("<p>"
                            + (i + 1)
                            + "位</em>:<font color=\"#666666\">"
                            + ((int) tfidf)
                            + " ｷﾆﾅｰﾙ</font>:<font size=\"5\"><SPAN class=entry-footer><a class=keyword href=\"http://"
                            + WebServer.HOST_NAME + ":" + WebServer.PORT
                            + "/?keyword=" + Util.getQueryString(keyword)
                            + "&amp;type=html&amp;category=hatebu\">" + keyword
                            + "</a></span></font>\n");
        }
        appendAmazon(html_doc);

        // optionsubとoption,container,bodyを閉じる
        html_doc.append("</div></div></div></div>");

        appendAnalytics(html_doc);
        html_doc.append("</body></html>\n");
        html_doc.append("\r\n");

        String doc = html_doc.toString();
        ByteArrayInputStream ba_in = new ByteArrayInputStream(doc.getBytes());

        read_bytes = doc.getBytes().length;
        doc_type = "html";
        return ba_in;
    }
    
    private InputStream generateYoutubeHTML(String id, Object obj,
            boolean isLogined) {
        UserProfile profile = (UserProfile) obj;

        StringBuffer html_doc = new StringBuffer();

        html_doc
                .append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n");
        html_doc.append("<html>\n");

        html_doc.append("<head>\n");
        appendCSS(html_doc);
        html_doc
                .append("<META http-equiv=\"Content-Type\" content=\"text/html; charset=shift_jis\">\n");

        html_doc.append("<title>Youtube Video Suggest to " + id + "</title>\n");
        html_doc
                .append("<LINK rel=\"alternate\" type=\"application/rss+xml\" title=\"Youtube Video Suggest to "
                        + id
                        + "\" href=\"http://"
                        + WebServer.HOST_NAME
                        + ":"
                        + WebServer.PORT
                        + "/?id="
                        + profile.getId()
                        + "&amp;type=rss&amp;category=youtube\">");
        html_doc
                .append("<LINK href=\"http://ryogrid.myhome.cx/distribute/kikker_style.css\" type=text/css rel=stylesheet>");
        html_doc.append("</head>\n<body>");

        appendBunner(html_doc, profile.getId(), isLogined,BUNNER_TYPE_YOUTUBE);
        appendSHINOBI(html_doc, SHINOBI_SUGGEST);
        html_doc
                .append("<a href=\"http://"
                        + WebServer.HOST_NAME
                        + ":"
                        + WebServer.PORT
                        + "/?id="
                        + profile.getId()
                        + "&amp;type=rss&amp;category=youtube\"><img src=\"http://ryogrid.myhome.cx/distribute/rss_icon.gif\"></a>\n");

        html_doc
                .append("<DIV id=container><DIV id=body><DIV class=option style=\"CLEAR: both\">");

        html_doc.append("<H3><A>Youtubeから探した" + id + "におすすめのビデオ</A></H3>");
        html_doc.append("<DIV class=optionsub>");
        appendAdsenceLect(html_doc);

        CrawledDataManager c_manager = DBCrawledDataManager.getInstance();
        DocumentSearchResult youtube_suggested = c_manager
                .search(((UserProfile) obj).getTasteVector(),DBUtil.YOUTUBE_TYPE);
        for (int i = 0; i < (youtube_suggested.results.length > KikkerConfigration.ALIGN_YOUTUBE_HTML_LIMIT ? KikkerConfigration.ALIGN_YOUTUBE_HTML_LIMIT
                : youtube_suggested.results.length); i++) {
            String link_address = youtube_suggested.results[i].getAddress();
            String title = youtube_suggested.results[i].getTitle();
            Date crawled_date = youtube_suggested.results[i].getClawledDate();
            int user_view_count = youtube_suggested.results[i].getView_users();

            if (isLogined) {
                html_doc
                        .append("<DIV class=entry><SPAN class=entry-body><a class=bookmark href=\"http://"
                                + WebServer.HOST_NAME
                                + ":"
                                + WebServer.PORT
                                + "/learn?url="
                                + Util.HTMLEscape(Util.EscapeSharp(link_address + "&category=youtube"))
                                + "\" target=\"_blank\"><img src=\"" + youtube_suggested.results[i].getCategory() +  "\"/><br>"
                                + Util
                                        .HTMLEscape(((title == null) ? link_address
                                                : title))
                                + "</a></SPAN>&nbsp;<SPAN class=entry-footer><strong><a>"
                                + ((int) Math
                                        .floor(youtube_suggested.eval_points[i] * 100.0))
                                + "point</a></strong> "
                                + user_view_count
                                + "users</a></STRONG></SPAN><SPAN class=entry-footer>&nbsp;"
                                + (crawled_date.getYear() + 1900)
                                + "年"
                                + (crawled_date.getMonth() + 1)
                                + "月"
                                + crawled_date.getDate() + "日</SPAN><br>\n");
            } else {
                html_doc
                        .append("<DIV class=entry><SPAN class=entry-body><a class=bookmark href=\""
                                + Util.HTMLEscape(link_address)
                                + "\" target=\"_blank\"><img src=\"" + youtube_suggested.results[i].getCategory() +  "\"/><br>"
                                + Util
                                        .HTMLEscape(((title == null) ? link_address
                                                : title))
                                + "</a></SPAN>&nbsp;<SPAN class=entry-footer><strong><a>"
                                + ((int) Math
                                        .floor(youtube_suggested.eval_points[i] * 100.0))
                                + "point</a></strong> "
                                + user_view_count
                                + "users</a></STRONG></SPAN><SPAN class=entry-footer>&nbsp;"
                                + (crawled_date.getYear() + 1900)
                                + "年"
                                + (crawled_date.getMonth() + 1)
                                + "月"
                                + crawled_date.getDate() + "日</SPAN><br>\n");
            }

            // Suggestするページに付加するキーワード文字列を作成
            HashMap keywords = youtube_suggested.results[i].getKeywords();
            List list = Collections.list(Collections.enumeration(keywords
                    .keySet()));
            StringBuffer buf = new StringBuffer();
            int web_key_len = list.size();
            for (int j = 0; j < ((web_key_len > KikkerConfigration.ALIGN_KEYWORD_LIMIT)?KikkerConfigration.ALIGN_KEYWORD_LIMIT : web_key_len); j++) {
                buf.append("<a class=keyword href=\"http://"
                        + WebServer.HOST_NAME + ":1800/keyword="
                        + Util.getQueryString((String) list.get(j))
                        + "&amp;page=1\">" + (String) list.get(j)
                        + "</a>&nbsp;");
            }
            String key_list_str = buf.toString();

            html_doc.append("<SPAN class=entry-footer>キーワード: " + key_list_str
                    + "</SPAN></DIV>\n");
        }

        // optionsubとoption,container,bodyを閉じる
        html_doc.append("</div></div></div></div>");
        
////      キーワードベクトルを表示する
//        html_doc
//                .append("<DIV id=container><DIV id=body><DIV class=option style=\"CLEAR: both\">");
//
//        html_doc.append("<H3><A>" + id + "の気になっているキーワード</A></H3>");
//        html_doc.append("<DIV class=optionsub>");
//        appendAdsenceLect(html_doc);
//
//        HashMap taste_vec = ((UserProfile) obj).getTasteVector();
//        ArrayList sorted_vec = Util.getSortedList(taste_vec);
//
//        for (int i = 0; i < ((sorted_vec.size() > KikkerConfigration.ALIGN_USER_KEYWORDS)? KikkerConfigration.ALIGN_USER_KEYWORDS : sorted_vec.size()) ; i++) {
//            KeyAndDoubleTFIDF key_tfidf = (KeyAndDoubleTFIDF) sorted_vec.get(i);
//            String keyword = key_tfidf.keyword;
//            double tfidf = key_tfidf.tfidf.doubleValue();
//
//            html_doc
//                    .append("<p>"
//                            + (i + 1)
//                            + "位</em>:<font color=\"#666666\">"
//                            + ((int) tfidf)
//                            + " ｷﾆﾅｰﾙ</font>:<font size=\"5\"><SPAN class=entry-footer><a class=keyword href=\"http://"
//                            + WebServer.HOST_NAME + ":1800/keyword=" + Util.getQueryString(keyword)
//                            + "&amp;page=1\">" + keyword
//                            + "</a></span></font>\n");
//        }
//        appendAmazon(html_doc);
//
//        // optionsubとoption,container,bodyを閉じる
//        html_doc.append("</div></div></div></div>");
        
        appendAnalytics(html_doc);
        html_doc.append("</body></html>\n");
        html_doc.append("\r\n");

        String doc = html_doc.toString();
        ByteArrayInputStream ba_in = new ByteArrayInputStream(doc.getBytes());

        read_bytes = doc.getBytes().length;
        doc_type = "html";
        return ba_in;
    }
    
    private void appendAdsence(StringBuffer buf) {
        buf.append("<script type=\"text/javascript\"><!--\n");
        buf.append("google_ad_client = \"pub-1445543306712811\";\n");
        buf.append("google_ad_width = 468;\n");
        buf.append("google_ad_height = 60;\n");
        buf.append("google_ad_format = \"468x60_as\";\n");
        buf.append("google_ad_type = \"text_image\";\n");
        buf.append("google_ad_channel =\"6226693729\";\n");
        buf.append("google_color_border = \"FFFFFF\";\n");
        buf.append("google_color_bg = \"FFFFFF\";\n");
        buf.append("google_color_link = \"0000FF\";\n");
        buf.append("google_color_url = \"008000\";\n");
        buf.append("google_color_text = \"000000\";\n");
        buf.append("//--></script>\n");
        buf.append("<script type=\"text/javascript\"\n");
        buf
                .append("src=\"http://pagead2.googlesyndication.com/pagead/show_ads.js\">\n");
        buf.append("</script>\n");
        buf.append("<br>");
    }

    private void appendAdsenceLect(StringBuffer buf) {
        buf.append("<DIV id=ad>");
        buf.append("<script type=\"text/javascript\"><!--\n");
        buf.append("google_ad_client = \"pub-1445543306712811\";\n");
        buf.append("google_ad_width = 300;\n");
        buf.append("google_ad_height = 250;\n");
        buf.append("google_ad_format = \"300x250_as\";\n");
        buf.append("google_ad_type = \"text_image\";\n");
        buf.append("google_ad_channel =\"6226693729\";\n");
        buf.append("google_color_border = \"FFFFFF\";\n");
        buf.append("google_color_bg = \"FFFFFF\";\n");
        buf.append("google_color_link = \"0000FF\";\n");
        buf.append("google_color_url = \"008000\";\n");
        buf.append("google_color_text = \"000000\";\n");
        buf.append("//--></script>\n");
        buf.append("<script type=\"text/javascript\"\n");
        buf
                .append("src=\"http://pagead2.googlesyndication.com/pagead/show_ads.js\">\n");
        buf.append("</script>\n");
        // buf.append("<br>");
        buf.append("</DIV>");
    }

    private void appendAmazon(StringBuffer buf) {
        buf.append("<script language=\"JavaScript\">");
        buf.append("var url=document.location.href;");
        buf
                .append("document.write('<scr' + 'ipt language=\"JavaScript\" src=\"http://app.drk7.jp/AmazonSimilarity.cgi?url=' + escape(url)");
        buf.append("+ '&n=3&ie=sjis&oe=sjis");
        buf.append("&tl=40&cl=100&t=hatenanejp0f0-22&idx=Book&style=heavy");
        buf.append("&footer=1&force=0\"></scr' + 'ipt>');");
        buf.append("</script>");
    }

    private void appendCSS(StringBuffer buf) {
        buf.append("<style type=text/css>");
        buf
                .append("H2.topdescription {MARGIN-TOP: 0px; PADDING-LEFT: 55px; PADDING-TOP: 25px;}");
        buf.append("#container {MARGIN-TOP: 5px;}");

        buf.append(".drk7jpSimilarity{font-size:x-small;}");
        buf.append(".drk7jpSimilaritySiteTitles{font-size:x-small;}");
        buf
                .append(".drk7jpSimilaritySiteSummarys{font-size:xx-small;margin-bottom:1em;}");
        buf
                .append(".drk7jpSimilaritySiteTitles a:hover{text-decoration: underline overline;color:#E4851B;}");
        buf
                .append(".drk7jpSimilaritySiteTitles a{color:#104E8B;font-weight:bold;text-decoration:none;}");
        buf.append("</style>");
    }

    private void appendBunner(StringBuffer buf, String user_id,
            boolean isLogined,int bunner_type) {
        buf.append("<DIV id=banner>");
        buf
                .append("<H1><a href=\"http://ryogrid.myhome.cx/wiki/pukiwiki.php?Kikker\"><IMG height=40 alt=Kikker");
        buf
                .append(" src=\"http://ryogrid.myhome.cx/distribute/kikker_moji.GIF\" width=104></a><a href=\"http://ryogrid.myhome.cx/wiki/pukiwiki.php?Kikker\"><IMG height=40 alt=kikker");
        buf
                .append(" src=\"http://ryogrid.myhome.cx/distribute/suggest.GIF\" width=118></a><a href=\"http://ryogrid.myhome.cx/wiki/pukiwiki.php?Kikker\"><IMG height=40");
        buf
                .append(" alt=tri src=\"http://ryogrid.myhome.cx/distribute/square.GIF\" width=15 useMap=#kikkermap");
        buf
                .append(" border=0></a></H1><a href=\"http://ryogrid.myhome.cx/wiki/pukiwiki.php?Kikker\"><IMG class=logo title=Kikker");
        buf
                .append(" alt=Kikker src=\"http://ryogrid.myhome.cx/distribute/kikker.GIF\"></a></DIV><MAP");
        buf
                .append(" name=kikkermap><AREA title=Kikker shape=RECT alt=Kikker coords=1,10,15,25 href=\"http://ryogrid.myhome.cx/wiki/pukiwiki.php?Kikker\"><AREA title=Kikker shape=RECT alt=Kikker coords=1,25,15,40 href=\"http://ryogrid.myhome.cx/wiki/pukiwiki.php?Kikker\"></MAP>");

        buf.append("<DIV id=bannersub>");
        buf.append("<TABLE cellSpacing=0>");
        buf.append("<TBODY>");
        buf.append("<TR>");
        buf
                .append("<TD style=\"TEXT-ALIGN: left\" noWrap width=\"50%\">&nbsp;ようこそ");
        buf.append(((share_user_id != null) ? share_user_id : "誰か")
                + "</A>さん&nbsp;</TD>");
        buf.append("<TD noWrap><A href=\"http://" + WebServer.HOST_NAME + ":"
                + WebServer.PORT + "/\">Top</A></TD>");
        buf.append("<TD noWrap><A href=\"http://" + WebServer.HOST_NAME + ":"
                + WebServer.PORT + "/regist\">ユーザ登録</A></TD>");
        buf.append("<TD noWrap><A href=\"http://" + WebServer.HOST_NAME + ":"
                + WebServer.PORT + "/edit\">登録情報変更</A></TD>");
        if (isLogined == true) {
            buf.append("<TD noWrap><A href=\"http://" + WebServer.HOST_NAME
                    + ":" + WebServer.PORT + "/logout\">ログアウト</A></TD>");
        } else {
            buf.append("<TD noWrap><A href=\"http://" + WebServer.HOST_NAME
                    + ":" + WebServer.PORT + "/login\">ログイン</A></TD>");
        }

//        if ((isLogined == true) && (share_user_id != null)) {
//            buf.append("<TD noWrap><A href=\"http://" + WebServer.HOST_NAME
//                    + ":" + WebServer.PORT + "/?id=" + share_user_id
//                    + "&type=html&category=hatebu\">MyPageへ</A></TD>");
//        }
        
        if(bunner_type==BUNNER_TYPE_HATEBU){
            if ((isLogined == true) && (share_user_id != null)) {
                buf.append("<TD noWrap><A href=\"http://" + WebServer.HOST_NAME
                        + ":" + WebServer.PORT + "/?id=" + share_user_id
                        + "&type=html&category=news\">MyNewsへ</A></TD>");
                buf.append("<TD noWrap><A href=\"http://" + WebServer.HOST_NAME
                        + ":" + WebServer.PORT + "/?id=" + share_user_id
                        + "&type=html&category=youtube\">My Videoへ</A></TD>");
            }else{
                buf.append("<TD noWrap><A href=\"http://" + WebServer.HOST_NAME
                        + ":" + WebServer.PORT + "/?id=" + user_id
                        + "&type=html&category=news\">お薦めNewsへ</A></TD>");
                buf.append("<TD noWrap><A href=\"http://" + WebServer.HOST_NAME
                        + ":" + WebServer.PORT + "/?id=" + user_id
                        + "&type=html&category=youtube\">お薦めVideoへ</A></TD>");
            }
        }else if(bunner_type==BUNNER_TYPE_NEWS){
            if ((isLogined == true) && (share_user_id != null)) {
                buf.append("<TD noWrap><A href=\"http://" + WebServer.HOST_NAME
                        + ":" + WebServer.PORT + "/?id=" + share_user_id
                        + "&type=html&category=hatebu\">Myはてブへ</A></TD>");
                buf.append("<TD noWrap><A href=\"http://" + WebServer.HOST_NAME
                        + ":" + WebServer.PORT + "/?id=" + share_user_id
                        + "&type=html&category=youtube\">My Videoへ</A></TD>");
            }else{
                buf.append("<TD noWrap><A href=\"http://" + WebServer.HOST_NAME
                        + ":" + WebServer.PORT + "/?id=" + user_id
                        + "&type=html&category=hatebu\">お勧めはてブへ</A></TD>");
                buf.append("<TD noWrap><A href=\"http://" + WebServer.HOST_NAME
                        + ":" + WebServer.PORT + "/?id=" + user_id
                        + "&type=html&category=youtube\">お薦めVideoへ</A></TD>");
            }
        }else if(bunner_type==BUNNER_TYPE_YOUTUBE){
            if ((isLogined == true) && (share_user_id != null)) {
                buf.append("<TD noWrap><A href=\"http://" + WebServer.HOST_NAME
                        + ":" + WebServer.PORT + "/?id=" + share_user_id
                        + "&type=html&category=hatebu\">Myはてブへ</A></TD>");
                buf.append("<TD noWrap><A href=\"http://" + WebServer.HOST_NAME
                        + ":" + WebServer.PORT + "/?id=" + share_user_id
                        + "&type=html&category=news\">MyNewsへ</A></TD>");
            }else{
                buf.append("<TD noWrap><A href=\"http://" + WebServer.HOST_NAME
                        + ":" + WebServer.PORT + "/?id=" + user_id
                        + "&type=html&category=hatebu\">お勧めはてブへ</A></TD>");
                buf.append("<TD noWrap><A href=\"http://" + WebServer.HOST_NAME
                        + ":" + WebServer.PORT + "/?id=" + user_id
                        + "&type=html&category=news\">お薦めNewsへ</A></TD>");
            } 
        }else{
            if ((isLogined == true) && (share_user_id != null)) {
                buf.append("<TD noWrap><A href=\"http://" + WebServer.HOST_NAME
                        + ":" + WebServer.PORT + "/?id=" + share_user_id
                        + "&type=html&category=hatebu\">Myはてブへ</A></TD>");
            }
        }
        

        buf.append("<TD noWrap><A");
        buf
                .append(" href=\"http://ryogrid.myhome.cx/wiki/pukiwiki.php?Kikker\">Wikiへ</A></TD>");
        
        buf.append("<TD noWrap><A href=\"http://ryogrid.myhome.cx/wiki/pukiwiki.php?%A4%E8%A4%AF%A4%A2%A4%EB%BC%C1%CC%E4\">Q&A</A></TD>");
        
        buf.append("</TR></TBODY></TABLE></DIV>");
        buf.append("<br><br>");
    }

    private void appendKeySearchBox(StringBuffer buf) {
        buf.append("<DIV id=sidemenu>");
        buf.append("<DIV id=searcharea>");
        buf.append("<H3>キーワード検索</H3>");
        buf
                .append("<FORM style=\"DISPLAY: inline\" action=/search");
        buf.append(" method=post><INPUT class=sidesearch");
        buf
                .append(" name=keyword><INPUT class=sidesearch type=submit value=検索 name=.submit><BR>");
        buf.append("<INPUT type=radio CHECKED value=hatebu name=target><A>はてブ</A>");
        buf.append("<INPUT type=radio CHECKED value=news name=target><A>News</A>");
        buf.append("</FORM></DIV></DIV>");
    }

    // 今回のリクエストに対してはクッキーをセットさせるよなヘッダを返すようにする
    private void setCookieStr(String name, String value, String path) {
        StringBuffer cookie_header_buf = new StringBuffer();
        cookie_header_buf.append("Set-Cookie: ");
        cookie_header_buf.append(name + "=" + value + "; ");
        cookie_header_buf.append("expires="
                + Util.getDateStrForCookie(CookieManager.COOKIE_EXPIRE_MINUTES)
                + "; ");
        cookie_header_buf.append("path=" + path + "\r\n");

        cookie_str = cookie_header_buf.toString();
    }

    private void appendSHINOBI(StringBuffer buf, String page_num) {
        buf.append("<!--shinobi1-->");
        buf
                .append("<script type=\"text/javascript\" src=\"http://x6.yu-yake.com/ufo/"
                        + page_num + "\"></script>");
        buf.append("<noscript><a href=\"http://x6.yu-yake.com/bin/gg?"
                + page_num + "\" target=\"_blank\">");
        buf
                .append("<img src=\"http://x6.yu-yake.com/bin/ll?072197900\" border=\"0\"></a><br>");
        buf
                .append("<a style=\"font-size:80%\" href=\"http://www.shinobi.jp/travel/\" target=\"_blank\">格安航空券</a></noscript>");
        buf.append("<!--shinobi2-->");
    }
    
    private void appendANewsCategory(StringBuffer html_doc, StoreBoxForDocumentEntry all_entries[],String category,HashMap taste_vector,boolean isLogined){
        CrawledDataManager c_manager = DBCrawledDataManager.getInstance();
        DocumentSearchResult suggested = c_manager.searchNews(taste_vector,category,all_entries);
        
        if(suggested.results.length > 0){
            html_doc.append("<DIV id=container><DIV id=body><DIV class=option style=\"CLEAR: both\">");
            html_doc.append("<H3><A>" + category + "</A></H3>");
            html_doc.append("<DIV class=optionsub>");
            if(suggested.results.length >=4){
                appendAdsenceLect(html_doc);    
            }

            for (int i = 0; i < (suggested.results.length > KikkerConfigration.ALIGN_EACH_NEWS_HTML_LIMIT ? KikkerConfigration.ALIGN_EACH_NEWS_HTML_LIMIT
                    : suggested.results.length); i++) {
                String link_address = suggested.results[i].getAddress();
                String title = suggested.results[i].getTitle();
                Date crawled_date = suggested.results[i].getClawledDate();

                if (isLogined) {
                    html_doc
                            .append("<DIV class=entry><SPAN class=entry-body><a class=bookmark href=\"http://"
                                    + WebServer.HOST_NAME
                                    + ":"
                                    + WebServer.PORT
                                    + "/learn?url="
                                    + Util.HTMLEscape(Util.EscapeSharp(link_address + "&category=news"))
                                    + "\" target=\"_blank\">"
                                    + Util
                                            .HTMLEscape(((title == null) ? link_address
                                                    : title))
                                    + "</a></SPAN>&nbsp;<SPAN class=entry-footer><strong><a>"
                                    +  Math
                                            .round(suggested.eval_points[i] * 100.0)
                                    + "point</a></strong>"
                                    + "</SPAN><SPAN class=entry-footer>&nbsp;"
                                    + (crawled_date.getYear() + 1900)
                                    + "年"
                                    + (crawled_date.getMonth() + 1)
                                    + "月"
                                    + crawled_date.getDate() + "日</SPAN><br>\n");
                } else {
                    html_doc
                            .append("<DIV class=entry><SPAN class=entry-body><a class=bookmark href=\""
                                    + Util.HTMLEscape(link_address)
                                    + "\" target=\"_blank\">"
                                    + Util
                                            .HTMLEscape(((title == null) ? link_address
                                                    : title))
                                    + "</a></SPAN>&nbsp;<SPAN class=entry-footer><strong><a>"
                                    + Math
                                            .round(suggested.eval_points[i] * 100.0)
                                    + "point</a></strong>"
                                    + "</SPAN><SPAN class=entry-footer>&nbsp;"
                                    + (crawled_date.getYear() + 1900)
                                    + "年"
                                    + (crawled_date.getMonth() + 1)
                                    + "月"
                                    + crawled_date.getDate() + "日</SPAN><br>\n");
                }

                // Suggestするページに付加するキーワード文字列を作成
                HashMap keywords = suggested.results[i].getKeywords();
                List list = Collections.list(Collections.enumeration(keywords
                        .keySet()));
                StringBuffer buf = new StringBuffer();
                int web_key_len = list.size();
                for (int j = 0; j < ((web_key_len > KikkerConfigration.ALIGN_KEYWORD_LIMIT)?KikkerConfigration.ALIGN_KEYWORD_LIMIT : web_key_len); j++) {
                    buf.append("<a class=keyword href=\"http://"
                            + WebServer.HOST_NAME + ":" + WebServer.PORT
                            + "/?keyword="
                            + Util.getQueryString((String) list.get(j))
                            + "&amp;type=html&amp;category=news\">" + (String) list.get(j)
                            + "</a>&nbsp;");
                }
                String key_list_str = buf.toString();

                html_doc.append("<SPAN class=entry-footer>キーワード: " + key_list_str
                        + "</SPAN></DIV>\n");
            }
                        
            // optionsubとoption,container,bodyを閉じる
            html_doc.append("</div></div></div></div>");
        }
    }
}
