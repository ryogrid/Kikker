package jp.ryo.informationPump.server.helper;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.*;

import jp.ryo.informationPump.server.collab.CollaborativeInformatoinManager;
import jp.ryo.informationPump.server.data.*;
import jp.ryo.informationPump.server.util.*;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.htmlparser.Node;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.*;
import org.htmlparser.util.*;

public class HatebuHelper {
    private static final String HATENA_RPC_URL= "http://b.hatena.ne.jp/xmlrpc";
    private static final String HATEBU_API_NAME= "bookmark.getCount";
    private static final int HATEBU_API_LIMIT = 50; //�͂ăuAPI�ň��Ƀ��N�G�X�g�ł���URL�̐�
    
    public static final String HATENA_CRAWL_BASE_URL = "http://b.hatena.ne.jp/entrylist?sort=eid&of=";
    public static final String HATEBU_BASE_URL = "http://b.hatena.ne.jp/";
    public static final String HATEBU_ENTRY_PAGE_URL = "entry/";
    
    public static final Double BOOKMARK_POINT_NORMAL = new Double(1);
    public static final Double BOOKMARK_POINT_COMMENTED = new Double(3);
    
    //url�̔z��(�v�f�����50)��n����,���ꂼ��̃u�b�N�}�[�N���ꂽ�񐔂̔z���Ԃ�
    public static int[] getBookmarkedCount(String urls[]){
        
        ArrayList params = new ArrayList();
        int len = urls.length;
        for(int i=0;i<len;i++){
            params.add(urls[i]);
        }
        
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(HATENA_RPC_URL));

            XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);

            try {
//                RequestCounter.checkCanDoCommunicate();
                
                HashMap result = (HashMap) client.execute(HATEBU_API_NAME, params);
                
                int results[] = new int[len];
                for(int i=0;i<len;i++){
                    Object obj = result.get(urls[i]);
                    if(obj!=null){
                        int tmp = ((Integer)obj).intValue();
                        if(tmp==0){
                            results[i] = 1;
                        }else{
                            results[i] = tmp;    
                        }                        
                    }
                }
                return results;
            } catch (XmlRpcException ex) {
                ex.printStackTrace();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public static int[] getBookmarkedCount(StoreBoxForWebData datas[]){
        int all_len = datas.length;

        String str_params[] = new String[all_len];
        for(int i=0;i<all_len;i++){
            str_params[i]=datas[i].data.getAddress();
        }
        
        int left_len=all_len;
        int results[] = new int[all_len]; 
        
        while(left_len!=0){
            if(left_len>HATEBU_API_LIMIT){
                String tmp_params[] = new String[HATEBU_API_LIMIT];
                int tmp_results[] = new int[HATEBU_API_LIMIT];
                
                int start_len=all_len-left_len;
                int end_index=start_len+HATEBU_API_LIMIT;
                
                //�ЂƂ܂��v�f���R�s�[
                for(int i=start_len;i<end_index;i++){
                    tmp_params[i-start_len]=str_params[i];
                }
                tmp_results = getBookmarkedCount(str_params);
                
                //���ʂ�K�؂ȏꏊ�ɃR�s�[
                for(int i=start_len;i<end_index;i++){
                    results[i]=tmp_results[i-start_len];
                }
                
                left_len-=HATEBU_API_LIMIT;
            }else{
                String tmp_params[] = new String[left_len];
                int tmp_results[] = new int[left_len];
                
                int start_len=all_len-left_len;
                int end_index=start_len+left_len;
                
                //�ЂƂ܂��v�f���R�s�[
                for(int i=start_len;i<end_index;i++){
                    tmp_params[i-start_len]=str_params[i];
                }
                tmp_results = getBookmarkedCount(str_params);
                
                //���ʂ�K�؂ȏꏊ�ɃR�s�[
                for(int i=start_len;i<end_index;i++){
                    results[i]=tmp_results[i-start_len];
                }

                break;
            }
        }
        
        return results;
    }
    
    public static int[] getBookmarkedCount(StoreBoxForDocumentEntry datas[]){
        int all_len = datas.length;

        String str_params[] = new String[all_len];
        for(int i=0;i<all_len;i++){
            str_params[i]=datas[i].data.getAddress();
        }
        
        int left_len=all_len;
        int results[] = new int[all_len]; 
        
        while(left_len!=0){
            if(left_len>HATEBU_API_LIMIT){
                String tmp_params[] = new String[HATEBU_API_LIMIT];
                int tmp_results[] = new int[HATEBU_API_LIMIT];
                
                int start_len=all_len-left_len;
                int end_index=start_len+HATEBU_API_LIMIT;
                
                //�ЂƂ܂��v�f���R�s�[
                for(int i=start_len;i<end_index;i++){
                    tmp_params[i-start_len]=str_params[i];
                }
                tmp_results = getBookmarkedCount(tmp_params);
                
                //���ʂ�K�؂ȏꏊ�ɃR�s�[
                for(int i=start_len;i<end_index;i++){
                    results[i]=tmp_results[i-start_len];
                }
                
                left_len-=HATEBU_API_LIMIT;
            }else{
                String tmp_params[] = new String[left_len];
                int tmp_results[] = new int[left_len];
                
                int start_len=all_len-left_len;
                int end_index=start_len+left_len;
                
                //�ЂƂ܂��v�f���R�s�[
                for(int i=start_len;i<end_index;i++){
                    tmp_params[i-start_len]=str_params[i];
                }
                tmp_results = getBookmarkedCount(tmp_params);
                
                //���ʂ�K�؂ȏꏊ�ɃR�s�[
                for(int i=start_len;i<end_index;i++){
                    results[i]=tmp_results[i-start_len];
                }

                break;
            }
        }
        
        return results;
    }
    
    //key=�����N�A�h���X(String) Value=HatebuEntry��v�f�Ƃ���,�܂�e�G���g���̏����܂�HashMap��Ԃ�
    public static HashMap crawl(int crawl_pages) throws ParserException, MalformedURLException, IOException{
        
        HashMap result_map = new HashMap();
        
        SparseMatrix matrix = null;

        CollaborativeInformatoinManager collab_maneger = CollaborativeInformatoinManager.getInstance();
        if(collab_maneger.getMatrix()==null){
            matrix = new SparseMatrix();
        }else{
            matrix = collab_maneger.getMatrix();
        }
        
        for(int i=0;i<crawl_pages;i++){
            try {
//              500�~���b�X���[�v������
                Thread.sleep(500);
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
            
//            String html = Util.doGet(HATENA_CRAWL_BASE_URL + i*50);
//            Lexer lexer = new Lexer(html);
            
            Lexer lexer = new Lexer((new URL(HATENA_CRAWL_BASE_URL + i*50)).openConnection());
            
            org.htmlparser.Parser parser = new org.htmlparser.Parser(lexer);
            
            Date current_date = new Date();
            try {
                NodeList entry_list = parser.extractAllNodesThatMatch(new HasAttributeFilter("class","entry"));
                SimpleNodeIterator entry_itr =  entry_list.elements();
                HashMap crawledDatas = new HashMap();
                while(entry_itr.hasMoreNodes()){
                    Node entry = entry_itr.nextNode();
                    
                    NodeList each_divs = entry.getChildren();//�^�C�g���Ȃ�,�^�O,�J�e�S���̂��ꂼ����܂ރ��X�g
                    NodeList has_attr = each_divs.extractAllNodesThatMatch(new NodeClassFilter(Div.class));
                    Node each_divs_arr[] = has_attr.toNodeArray();
                    
                    Node title_and_link = each_divs_arr[0];
                    Node keywords_node = each_divs_arr[1];
                    Node tags=null;
                    Node categries_and_users=null;
                    if(each_divs_arr.length ==4){
                        tags = each_divs_arr[2];
                        categries_and_users = each_divs_arr[3];    
                    }else{//�^�O���t�����Ă��Ȃ������ꍇ
                        categries_and_users = each_divs_arr[1];
                    }
                    
                    //�^�C�g���ƃ����N�̃O���[�v������
                    LinkTag title_link_tag = (LinkTag)((title_and_link.getChildren()).elementAt(1));
                    String title = title_link_tag.getLinkText();
                    String link = title_link_tag.getLink();
                    
                    //�L�[���[�h�̃O���[�v������
                    NodeList keywords_children = keywords_node.getChildren();
                    NodeList each_keywords = keywords_children.extractAllNodesThatMatch(new HasAttributeFilter("class","keyword"));
                    SimpleNodeIterator keyword_itr = each_keywords.elements();
                    HashMap keywords_map = new HashMap();
                    while(keyword_itr.hasMoreNodes()){
                        LinkTag link_tag = (LinkTag)keyword_itr.nextNode();
                        keywords_map.put(link_tag.getLinkText().intern(),new Double(new String("1")));
                    }
                    
                    HashMap tags_map = null;
                    if(tags!=null){
                    //�^�O�̃O���[�v������
                        tags_map = new HashMap();
                        NodeList tags_children = tags.getChildren();
                        NodeList each_tags = tags_children.extractAllNodesThatMatch(new HasAttributeFilter("class","tag"));
                        SimpleNodeIterator tags_itr = each_tags.elements();
                        
                        while(tags_itr.hasMoreNodes()){
                            LinkTag link_tag = (LinkTag)tags_itr.nextNode();
                            tags_map.put(link_tag.getLinkText().intern(),new Double(new String("10")));
                        }    
                    }
                    
                    //�J�e�S���ƃ��[�U�{�����̃O���[�v������
                    NodeList link_tag_div = categries_and_users.getChildren();
                    NodeList link_tags = link_tag_div.extractAllNodesThatMatch(new NodeClassFilter(LinkTag.class));
                    
                    String category = "";
                    Object obj = link_tags.elementAt(1); 
                    if(obj!=null){
                        LinkTag category_tag = (LinkTag)link_tags.elementAt(1);
                        category = category_tag.getLinkText().intern();
                    }
                    
                    int view_user_count = 0;
                    try {
                        obj = link_tags.elementAt(3);
                        if(obj!=null){
                            LinkTag users_tag = (LinkTag)link_tags.elementAt(3);
                            
                            String view_users_str = users_tag.getLinkText();
                            view_user_count = Integer.parseInt((view_users_str.substring(0,view_users_str.length()-5)).trim());
                            
                            crawlEachBookmarkPage(users_tag, link, matrix);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    
                    DocumentEntry new_entry = new DocumentEntry(link,current_date,keywords_map,tags_map,title,view_user_count,category,DBUtil.HATEBU_TYPE);
                    new_entry.setClawledDate(new Date());
                    new_entry.setDescription(new String());
                    new_entry.setSorted_keywords(new ArrayList());
                    
                    crawledDatas.put(link,new_entry);
                }
                result_map.putAll(crawledDatas);
            } catch (ParserException e1) {
                e1.printStackTrace();
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        
        CollaborativeInformatoinManager collab_manager = CollaborativeInformatoinManager.getInstance();
        collab_manager.setMatrix(matrix);
        CollaborativeInformatoinManager.setInstance(collab_manager);

        return result_map;
    }
    
    //����ɂǂꂾ���̐��u�b�N�}�[�N����Ă���̂�������
    public static void countBookmarkCountPerDay(int target_day){
        int count = 0;
        
        int inner_target_day = target_day;
        HashMap url_map = new HashMap();
        boolean isFinished = false;
        for(int i=0;!isFinished;i++){
            String html = Util.doGet(HATENA_CRAWL_BASE_URL + i*50);
            Lexer lexer = new Lexer(html);
            org.htmlparser.Parser parser = new org.htmlparser.Parser(lexer);
            
            try {
                NodeList entry_list = parser.extractAllNodesThatMatch(new HasAttributeFilter("class","entry"));
                SimpleNodeIterator entry_itr =  entry_list.elements();
                while(entry_itr.hasMoreNodes()){
                    Node entry = entry_itr.nextNode();
                    
                    NodeList each_divs = entry.getChildren();//�^�C�g���Ȃ�,�^�O,�J�e�S���̂��ꂼ����܂ރ��X�g
                    NodeList has_attr = each_divs.extractAllNodesThatMatch(new NodeClassFilter(Div.class));
                    Node each_divs_arr[] = has_attr.toNodeArray();
                    
                    Node title_and_link = each_divs_arr[0];
                    Node categries_and_users=null;
                    if(each_divs_arr.length ==4){
                        categries_and_users = each_divs_arr[3];    
                    }else if(each_divs_arr.length == 6){
                        categries_and_users = each_divs_arr[4];
                    }else if(each_divs_arr.length == 5){
                        categries_and_users = each_divs_arr[3];
                    }else{//�^�O���t�����Ă��Ȃ������ꍇ
                        categries_and_users = each_divs_arr[2];
                    }
                    
                    //�^�C�g���ƃ����N�̃O���[�v������
                    LinkTag title_link_tag = (LinkTag)((title_and_link.getChildren()).elementAt(1));
                    String title = title_link_tag.getLinkText();
                    String link = title_link_tag.getLink();
                    
                    Node nodes[] = categries_and_users.getChildren().toNodeArray();
                    String date = null;
//                    if(each_divs_arr.length == 4){
//                        date = nodes[9].getText();
//                    }else{
                        date = nodes[8].getText(); 
//                    }
                    
                    String day_num_str = null;
                    try {
                         day_num_str = date.substring(date.indexOf("��")-2,date.indexOf("��"));     
                    } catch (Exception e) {
//                        e.printStackTrace();
                        date = nodes[10].getText();
                        day_num_str = date.substring(date.indexOf("��")-2,date.indexOf("��"));
                    }
                    
                    int day = Integer.parseInt(day_num_str);
                    
                    Object obj = url_map.get(link);
                    if((obj==null)&&(day==inner_target_day)){
                        url_map.put(link,new Object());
                        count++;
                    }
                    
                    if(day < inner_target_day){
                        System.out.println(count + "boorkmarked in " + inner_target_day);
                        if(inner_target_day == 1){
                            inner_target_day = 31;
                        }else{
                            inner_target_day--;    
                        }
                        
                        count = 0;
                    }
                    
                    if((inner_target_day==1)&&(day==31)){
                        System.out.println(count + "boorkmarked in " + inner_target_day);
                        if(inner_target_day == 1){
                            inner_target_day = 31;
                        }else{
                            inner_target_day--;    
                        }
                        
                        count = 0;
                    }
                }
            } catch (ParserException e1) {
                e1.printStackTrace();
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    
    public static SparseMatrix crawlForCollab(int crawl_pages) throws ParserException, MalformedURLException, IOException{
            SparseMatrix matrix = new SparseMatrix();
            for(int i=0;i<crawl_pages;i++){

//                String html = Util.doGet(HATENA_CRAWL_BASE_URL + i*50);
//                Lexer lexer = new Lexer(html);
                Lexer lexer = new Lexer((new URL(HATENA_CRAWL_BASE_URL + i*50)).openConnection());
                org.htmlparser.Parser parser = new org.htmlparser.Parser(lexer);
                
                try {
                    NodeList entry_list = parser.extractAllNodesThatMatch(new HasAttributeFilter("class","entry"));
                    SimpleNodeIterator entry_itr =  entry_list.elements();
                    while(entry_itr.hasMoreNodes()){
                        Node entry = entry_itr.nextNode();
                        
                        NodeList each_divs = entry.getChildren();//�^�C�g���Ȃ�,�^�O,�J�e�S���̂��ꂼ����܂ރ��X�g
                        NodeList has_attr = each_divs.extractAllNodesThatMatch(new NodeClassFilter(Div.class));
                        Node each_divs_arr[] = has_attr.toNodeArray();
                        
                        Node title_and_link = each_divs_arr[0];
                        Node categries_and_users=null;
                        if(each_divs_arr.length ==4){
                            categries_and_users = each_divs_arr[3];    
                        }else{//�^�O���t�����Ă��Ȃ������ꍇ
                            categries_and_users = each_divs_arr[2];
                        }
                        
                        //�^�C�g���ƃ����N�̃O���[�v������
                        LinkTag title_link_tag = (LinkTag)((title_and_link.getChildren()).elementAt(1));
                        String link = title_link_tag.getLink();
                        
                        //�J�e�S���ƃ��[�U�{�����̃O���[�v������
                        NodeList link_tag_div = categries_and_users.getChildren();
                        NodeList link_tags = link_tag_div.extractAllNodesThatMatch(new NodeClassFilter(LinkTag.class));

                        Object obj = null;
                        try {
                            obj = link_tags.elementAt(3);
                            if(obj!=null){
                                LinkTag users_tag = (LinkTag)link_tags.elementAt(3);
                                
                                crawlEachBookmarkPage(users_tag, link, matrix);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (ParserException e1) {
                    e1.printStackTrace();
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
            
            return matrix;
    }
    
    private static void crawlEachBookmarkPage(LinkTag users_tag,String link,SparseMatrix matrix) throws ParserException, MalformedURLException, IOException{
        String boorkmark_user_page_url = users_tag.getLink();
        crawlEachBookmarkPage(link, matrix, boorkmark_user_page_url);
    }
    

    public static void crawlEachBookmarkPageForUpdate(String target_entry_url,SparseMatrix matrix) throws ParserException, MalformedURLException, IOException{
        crawlEachBookmarkPage(target_entry_url, matrix, HATEBU_BASE_URL + HATEBU_ENTRY_PAGE_URL + target_entry_url);
    }
    
    private static void crawlEachBookmarkPage(String link, SparseMatrix matrix, String boorkmark_user_page_url) throws ParserException, MalformedURLException, IOException {
        String each_bookmark_page_html = Util.doGet(boorkmark_user_page_url);
        Lexer lexer2 = new Lexer(each_bookmark_page_html);
        
//        Lexer lexer2 = new Lexer((new URL(HATEBU_BASE_URL + boorkmark_user_page_url)).openConnection());
        
        org.htmlparser.Parser parser2 = new org.htmlparser.Parser(lexer2);
        NodeList user_list;
        try {
            user_list = parser2.extractAllNodesThatMatch(new HasAttributeFilter("class","hatena-id-icon"));
        } catch (ParserException e) {
            e.printStackTrace();
            return;
        }
        SimpleNodeIterator user_itr =  user_list.elements();
        while(user_itr.hasMoreNodes()){
            ImageTag user_img_tag = (ImageTag)user_itr.nextNode();
            String user_name = (user_img_tag.getAttributeEx("alt")).getValue();
            
            matrix.setValue(link.intern(),user_name.intern(),BOOKMARK_POINT_NORMAL);

//�R�����g�̕t�����G���g���ɏd�ݕt������ꍇ�͂�����            
//            Node parent = user_img_tag.getParent().getParent();
//            TextNode last_child = (TextNode)parent.getLastChild();
//            String comment = last_child.toPlainTextString();
//            
//            if(comment.replace(" ","").equals("")){
//                //�R�����g���������܂�Ă����ꍇ
//                matrix.setValue(link.intern(),user_name.intern(),BOOKMARK_POINT_COMMENTED);
//            }else{
//                matrix.setValue(link.intern(),user_name.intern(),BOOKMARK_POINT_NORMAL);
//            }
        }
    }
    
    public static ArrayList getFavorUsers(String id){
        ArrayList results = new ArrayList();
        String doc = Util.doGet(HATEBU_BASE_URL + id + "/favorite");
        Lexer lexer = new Lexer(doc);
        
        org.htmlparser.Parser parser = new org.htmlparser.Parser(lexer);
        
        try {
            NodeList favorite_list = parser.extractAllNodesThatMatch(new HasAttributeFilter("class","favoritelist"));
            if(favorite_list.size() > 0){
                Node div_element = favorite_list.toNodeArray()[0];
                NodeList list_elements = div_element.getChildren().elementAt(3).getChildren();
                
                SimpleNodeIterator itr = list_elements.elements();
                while(itr.hasMoreNodes()){
                    Node tmp_node = itr.nextNode();
                    if(tmp_node instanceof Bullet){
                        results.add(((ImageTag)((LinkTag)((Bullet)tmp_node).childAt(0)).childAt(0)).getAttribute("title"));
                    }
                }
            }
            return results;
        } catch (ParserException e) {
            e.printStackTrace();
        }
        
        return null;
    }
}