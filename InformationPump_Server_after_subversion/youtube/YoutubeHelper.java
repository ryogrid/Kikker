package youtube;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import javax.xml.parsers.*;


import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class YoutubeHelper {
    private static String YOUTUBE_RPC_URL = "http://www.youtube.com/api2_xmlrpc";

    private static String YOUTUBE_GET_FEATURED_METHOD_NAME = "youtube.videos.list_featured";
    private static String YOUTUBE_GET_PROFILE_METHOD_NAME = "youtube.users.get_profile";
    private static String YOUTUBE_GET_USER_FAVARITES_METHOD_NAME = "youtube.users.list_favorite_videos";
    private static String YOUTUBE_GET_FRIENDS_METHOD_NAME = "youtube.users.list_friends";
    private static String YOUTUBE_GET_DETAILS_METHOD_NAME = "youtube.videos.get_details";
    private static String YOUTUBE_GET_VIDEO_LIST_BY_TAG_METHOD_NAME = "youtube.videos.list_by_tag";
    private static String YOUTUBE_GET_VIDEO_LIST_A_USER_UPLOADED_METHOD_NAME = "youtube.videos.list_by_user";
    
    private static String DEV_ID = "ijz-YcM6bGs";
    
    public static YoutubeUserProfile[] getProfile(String user_id) throws Exception{
        Hashtable input0 = new Hashtable();
        input0.put("user",user_id);
        return convertToUserProfileArr(parse(doRPCToYoutube(YOUTUBE_GET_PROFILE_METHOD_NAME,input0))); 
    }
    
    public static YoutubeVideoInfo[] getFavoriteVideos(String user_id) throws Exception{
        Hashtable input0 = new Hashtable();
        input0.put("user",user_id);
        return convertToVideoInfoArr(parse(doRPCToYoutube(YOUTUBE_GET_USER_FAVARITES_METHOD_NAME,input0))); 
    }
    
    public static YoutubeUserProfile[] getFriends(String user_id) throws Exception{
        Hashtable input0 = new Hashtable();
        input0.put("user",user_id);
        return convertToUserProfileArr(parse(doRPCToYoutube(YOUTUBE_GET_FRIENDS_METHOD_NAME,input0)));
    }
    
    public static YoutubeVideoDetail getVideoDetail(String video_id) throws Exception{
        Hashtable input0 = new Hashtable();
        input0.put("video_id",video_id);
        return convertToVideoDetail(parse(doRPCToYoutube(YOUTUBE_GET_DETAILS_METHOD_NAME,input0)));
    }

    //pageとper_pageは必要なければ-1を与えること
    public static YoutubeVideoInfo[] getVideoListByTag(String tag,int page,int per_page) throws Exception{
        Hashtable input0 = new Hashtable();
        input0.put("tag",tag);
        if(page!=-1){
            input0.put("page",new Integer(page));
        }
        if(per_page!=-1){
            input0.put("per_page",new Integer(per_page));
        }
        
        return convertToVideoInfoArr(parse(doRPCToYoutube(YOUTUBE_GET_VIDEO_LIST_BY_TAG_METHOD_NAME,input0)));
    }
    
    
    public static YoutubeVideoInfo[] getUploadedVideosByUser(String user_id) throws Exception{
        Hashtable input0 = new Hashtable();
        input0.put("user",user_id);
        return convertToVideoInfoArr(parse(doRPCToYoutube(YOUTUBE_GET_VIDEO_LIST_A_USER_UPLOADED_METHOD_NAME,input0)));
    }
    
    public static YoutubeVideoInfo[] getFeaturedVideoList() throws Exception{
        Hashtable input0 = new Hashtable();
        return convertToVideoInfoArr(parse(doRPCToYoutube(YOUTUBE_GET_FEATURED_METHOD_NAME,input0)));
    }
    
    private static String doRPCToYoutube(String method_name,Hashtable param){
        ArrayList params = new ArrayList();
        param.put("dev_id",DEV_ID);
        params.add(param);

        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(YOUTUBE_RPC_URL));
            
            XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);

            try {
                String result = (String)client.execute(method_name,params);
                return result;
            } catch (XmlRpcException ex) {
                ex.printStackTrace();
                return null;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    private static YoutubeUserProfile[] convertToUserProfileArr(ArrayList arr_list){
        int len = arr_list.size();
        YoutubeUserProfile profiles[] = new YoutubeUserProfile[len];
        for(int i=0;i<len;i++){
            HashMap entry = (HashMap)arr_list.get(i);
            YoutubeUserProfile new_profile = new YoutubeUserProfile();
            new_profile.about_me = (String)entry.get("about_me");
            new_profile.age = Integer.parseInt(((String)entry.get("age")!=null)?(String)entry.get("age"):"-1"); 
            new_profile.books = (String)entry.get("books");
            new_profile.city = (String)entry.get("city");
            new_profile.companies = (String)entry.get("companies");
            new_profile.country = (String)entry.get("country");
            new_profile.currently_on = Boolean.parseBoolean(((String)entry.get("currently_on")!=null)?(String)entry.get("currently_on"):"false");
            new_profile.favorite_video_count = Integer.parseInt(((String)entry.get("favorite_video_count")!=null)?(String)entry.get("favorite_video_count"):"-1");
            new_profile.first_name = (String)entry.get("first_name");
            new_profile.friend_count = Integer.parseInt(((String)entry.get("friend_count")!=null)?(String)entry.get("friend_count"):"-1");
            new_profile.gender = (String)entry.get("gender");
            new_profile.hobbies = (String)entry.get("hobbies");
            new_profile.homepage = (String)entry.get("homepage");
            new_profile.hometown = (String)entry.get("hometown");
            new_profile.last_name = (String)entry.get("last_name");
            new_profile.movies = (String)entry.get("movies");
            new_profile.occupations = (String)entry.get("occupations");
            new_profile.relationship = (String)entry.get("relationship");
            new_profile.video_upload_count = Integer.parseInt(((String)entry.get("video_upload_count")!=null)?(String)entry.get("video_upload_count"):"-1");
            new_profile.video_watch_count = Integer.parseInt(((String)entry.get("video_watch_count")!=null)?(String)entry.get("video_watch_count"):"-1");
            
            profiles[i] = new_profile;
        }
        
        return profiles;
    }
    
    private static YoutubeVideoDetail convertToVideoDetail(ArrayList arr_list){
        int len = arr_list.size();
        YoutubeVideoDetail details[] = new YoutubeVideoDetail[len];
        for(int i=0;i<len;i++){
            HashMap entry = (HashMap)arr_list.get(i);
            YoutubeVideoDetail new_details = new YoutubeVideoDetail();
            new_details.author = (String)entry.get("author");
            new_details.description = (String)entry.get("description");
            new_details.length_seconds = Integer.parseInt(((String)entry.get("length_seconds")!=null)?(String)entry.get("length_seconds"):"-1");
            new_details.rating_avg = Double.parseDouble(((String)entry.get("rating_avg")!=null)?(String)entry.get("rating_avg"):"-1");
            new_details.rating_count = Integer.parseInt(((String)entry.get("rating_count")!=null)?(String)entry.get("rating_count"):"-1");
            new_details.tags = (String)entry.get("tags");
            new_details.thumbnail_url = (String)entry.get("thumbnail_url");
            new_details.title = (String)entry.get("title");
            new_details.upload_time =  Integer.parseInt(((String)entry.get("upload_time")!=null)?(String)entry.get("upload_time"):"-1");
            new_details.update_time = Long.parseLong(((String)entry.get("update_time")!=null)?(String)entry.get("update_time"):"-1");
            new_details.view_count = Integer.parseInt(((String)entry.get("view_count")!=null)?(String)entry.get("view_count"):"-1");
            
            ArrayList comment_list = (ArrayList)entry.get("comment_list");
            int comment_len = comment_list.size();
            CommentEntry comments[] = new CommentEntry[comment_len];
            for(int j=0;j<comment_len;j++){
                HashMap comment_entry = (HashMap)comment_list.get(i);
                
                CommentEntry comment = new CommentEntry();
                comment.author = (String)comment_entry.get("author");
                comment.text = (String)comment_entry.get("text");
                comment.time = Long.parseLong(((String)comment_entry.get("time")!=null)?(String)comment_entry.get("time"):"0");
                
                comments[j] = comment;
            }
            new_details.comment_list = comments;
            
            ArrayList channel_list = (ArrayList)entry.get("channel_list");
            int channel_len = channel_list.size();
            String channnels[] = new String[channel_len];
            for(int j=0;j<channel_len;j++){
                String channel = (String)channel_list.get(j);
                channnels[j] = channel;
            }
            new_details.channel_list = channnels;
            
            details[i] = new_details;
        }
        
        return details[0];
    }
    
    private static YoutubeVideoInfo[] convertToVideoInfoArr(ArrayList arr_list){
        int len = arr_list.size();
        YoutubeVideoInfo infos[] = new YoutubeVideoInfo[len];
        for(int i=0;i<len;i++){
            HashMap entry = (HashMap)arr_list.get(i);
            YoutubeVideoInfo new_info = new YoutubeVideoInfo();
            new_info.author = (String)entry.get("author");
            new_info.comment_count = Integer.parseInt(((String)entry.get("comment_count")!=null)?(String)entry.get("comment_count"):"-1"); 
            new_info.description = (String)entry.get("description");
            new_info.id = (String)entry.get("id");
            new_info.length_seconds = Integer.parseInt(((String)entry.get("length_seconds")!=null)?(String)entry.get("length_seconds"):"-1");
            new_info.rating_avg = Double.parseDouble(((String)entry.get("rating_avg")!=null)?(String)entry.get("rating_avg"):"-1");
            new_info.rating_count = Integer.parseInt(((String)entry.get("rating_count")!=null)?(String)entry.get("rating_count"):"-1");
            new_info.tags = (String)entry.get("tags");
            new_info.thumbnail_url = (String)entry.get("thumbnail_url");
            new_info.title = (String)entry.get("title");
            new_info.upload_time =  Integer.parseInt(((String)entry.get("upload_time")!=null)?(String)entry.get("upload_time"):"-1");
            new_info.url = (String)entry.get("url");
            new_info.view_count = Integer.parseInt(((String)entry.get("view_count")!=null)?(String)entry.get("view_count"):"-1");

            infos[i] = new_info;
        }
        
        return infos;
    }
    
    private static ArrayList parse(String xml_body) throws Exception{
       DocumentBuilderFactory factory =  DocumentBuilderFactory.newInstance();
       try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xml_body
                    .getBytes("UTF-8")));

            NodeList top_list = doc.getElementsByTagName("ut_response");
            Node ut_response_node = top_list.item(0);

            Node each_element = ut_response_node.getFirstChild();
            String method_type = each_element.getNodeName();
            
            ArrayList result_arr = new ArrayList();
            if(method_type.equals("user_profile")){
                NodeList each_node = each_element.getChildNodes();
                int len = each_node.getLength();
                HashMap tmp_map = new HashMap();
                for(int i=0;i<len;i++){
                    if(each_node.item(i).getFirstChild()!=null){
                        tmp_map.put(each_node.item(i).getNodeName(),each_node.item(i).getFirstChild().getNodeValue());    
                    }else{
                        tmp_map.put(each_node.item(i).getNodeName(),"");
                    }
                        
                }
                result_arr.add(tmp_map);
            }else if(method_type.equals("video_list")){
                NodeList each_node = each_element.getChildNodes();
                int len = each_node.getLength();
                for(int i=0;i<len;i++){
                    Node a_video = each_node.item(i);
                    NodeList each_property = a_video.getChildNodes();
                    
                    HashMap tmp_map = new HashMap();
                    int prop_len = each_property.getLength();
                    for(int j=0;j<prop_len;j++){
                        if(each_property.item(j).getFirstChild()!=null){
                            tmp_map.put(each_property.item(j).getNodeName(),each_property.item(j).getFirstChild().getNodeValue());    
                        }else{
                            tmp_map.put(each_property.item(j).getNodeName(),"");
                        }
                    }
                    result_arr.add(tmp_map);
                }
            }else if(method_type.equals("friend_list")){
                NodeList each_node = each_element.getChildNodes();
                int len = each_node.getLength();
                for(int i=0;i<len;i++){
                    Node a_friend = each_node.item(i);
                    NodeList each_property = a_friend.getChildNodes();
                    
                    HashMap tmp_map = new HashMap();
                    int prop_len = each_property.getLength();
                    for(int j=0;j<prop_len;j++){
                        if(each_property.item(j).getFirstChild()!=null){
                            tmp_map.put(each_property.item(j).getNodeName(),each_property.item(j).getFirstChild().getNodeValue());    
                        }else{
                            tmp_map.put(each_property.item(j).getNodeName(),"");
                        }
                            
                    }
                    result_arr.add(tmp_map);
                }
            }else if(method_type.equals("video_details")){
                NodeList each_node = each_element.getChildNodes();
                int len = each_node.getLength();
                HashMap tmp_map = new HashMap();
                for(int i=0;i<len;i++){
                    if(each_node.item(i).getFirstChild()!=null){
                        if(each_node.item(i).getNodeName().equals("comment_list")){
                            ArrayList comment_arr = new ArrayList();
                            NodeList each_comment = each_node.item(i).getChildNodes();
                            int comment_len = each_comment.getLength();
                            HashMap comment_tmp_map = new HashMap();
                            for(int k=0;k<comment_len;k++){
                                NodeList each_comment_property = each_comment.item(k).getChildNodes();
                                int comment_property_len = each_comment_property.getLength();
                                for(int l=0;l<comment_property_len;l++){
                                    if(each_comment.item(l).getFirstChild()!=null){
                                        comment_tmp_map.put(each_comment.item(l).getNodeName(),each_comment.item(l).getFirstChild().getNodeValue());    
                                    }else{   
                                        comment_tmp_map.put(each_comment.item(l).getNodeName(),"");
                                    }
                                }
                                comment_arr.add(comment_tmp_map);
                            }
                            tmp_map.put("comment_list",comment_arr);
                        }else if(each_node.item(i).getNodeName().equals("channel_list")){
                            ArrayList channel_arr = new ArrayList();
                            NodeList each_channel = each_node.item(i).getChildNodes();
                            int channel_len = each_channel.getLength();
                            HashMap channel_tmp_map = new HashMap();
                            for(int k=0;k<channel_len;k++){
                                NodeList each_comment_property = each_channel.item(k).getChildNodes();
                                int comment_property_len = each_comment_property.getLength();
                                for(int l=0;l<comment_property_len;l++){
                                    if(each_channel.item(l).getFirstChild()!=null){
                                        channel_arr.add(each_channel.item(l).getFirstChild().getNodeValue());    
                                    }else{
                                        channel_arr.add("");
                                    }
                                }
                            }
                            tmp_map.put("channel_list",channel_arr);
                        }else{
                            tmp_map.put(each_node.item(i).getNodeName(),each_node.item(i).getFirstChild().getNodeValue());                                
                        }
                    }else{
                        tmp_map.put(each_node.item(i).getNodeName(),"");
                    }
                }
                result_arr.add(tmp_map);
            }else{
                throw new Exception("Youtubeから受け取ったレスポンスに知らないエレメント:" + method_type + ":があります!!");
            }
            return result_arr;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
}
