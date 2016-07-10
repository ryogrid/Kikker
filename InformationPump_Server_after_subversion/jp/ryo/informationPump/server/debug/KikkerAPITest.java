package jp.ryo.informationPump.server.debug;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class KikkerAPITest {

    public static void main(String[] args) {
//        String url = "http://localhost:7777/";
        String url = "http://localhost:1234/";
        String methodName = "KikkerWebAPI.search";
        Vector params = new Vector();
        params.add(new Integer(2));
        ArrayList keywordList = new ArrayList();
        keywordList.add("web");
        ArrayList tasteValueList = new ArrayList();
        tasteValueList.add(new Double(1000));

        params.add(keywordList);
        params.add(tasteValueList);
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(url));

            XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);
            try {
                HashMap result = (HashMap) client.execute(methodName,
                        params);
                Object[] titleList = (Object[])result.get("titles");
                Object[] urlList = (Object[])result.get("urls");
                Object[] evalPointList = (Object[])result.get("eval_points");
                
                int len = titleList.length;
                for(int i=0;i<len;i++){
                    System.out.println(titleList[i] + ":" + urlList[i] + ":" + evalPointList[i] + "point");
                }
            } catch (XmlRpcException ex) {
                ex.printStackTrace();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
