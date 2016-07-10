package youtube;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class YoutubeAPITest {
    private static String YOUTUBE_RPC_URL = "http://www.youtube.com/api2_xmlrpc";
    private static String YOUTUBE_METHOD_NAME = "youtube.videos.list_featured";
    private static String DEV_ID = "ijz-YcM6bGs";
    private static String YOUTUBE_USER_NAME = "i041184";
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        getRecentFeaturedVideos();
    }
    
    public static void getRecentFeaturedVideos(){
        
        ArrayList params = new ArrayList();
        Hashtable profile = new Hashtable();
        profile.put("dev_id",DEV_ID);
        params.add(profile);

        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(YOUTUBE_RPC_URL));
            
            XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);

            try {
                String result = (String)client.execute(YOUTUBE_METHOD_NAME,params);
                System.out.println(result);
            } catch (XmlRpcException ex) {
                ex.printStackTrace();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
