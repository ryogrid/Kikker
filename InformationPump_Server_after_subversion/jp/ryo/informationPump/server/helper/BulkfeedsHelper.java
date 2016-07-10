/*
 * �쐬��: 2006/02/06
 *
 * ���̐������ꂽ�R�����g�̑}�������e���v���[�g��ύX���邽��
 * �E�B���h�E > �ݒ� > Java > �R�[�h���� > �R�[�h�ƃR�����g
 */
package jp.ryo.informationPump.server.helper;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.*;

import jp.ryo.informationPump.server.util.Util;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.hsqldb.lib.StringInputStream;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class BulkfeedsHelper {
    private static final String BULKFEEDS_URL = "http://bulkfeeds.net/app/terms.xml";
    private static final String API_KEY = "319dc4bd9272d69b148f52ec1ac160eb";
    
    //�^����String�̒��ŏd�v�x�̍����L�[���[�h�̔z���Ԃ��BIndex�������������d�v�ȃL�[���[�h
    public static String[] getImportantWords(String contens){
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            contens = contens.replaceAll(new String(new byte[]{0x1b}),"");
            Document doc = builder.parse(new ByteArrayInputStream((doPostToBulkfeeds(BULKFEEDS_URL,contens)).getBytes("utf-8")));
            NodeList term_list = doc.getElementsByTagName("term");
            
            String result[] = new String[term_list.getLength()];
            
            int counter=0;
            for(int i=0;i<term_list.getLength();i++){
                Node tmp_node = term_list.item(i);
                
                String word=null;
                try{
                    word = tmp_node.getFirstChild().getNodeValue();
                }catch(Exception e){
                    continue;
                }
                
                if((Util.check(word)==true)&&(Util.checkIgnoreKeyword(word.trim())==false)){
                    result[counter] = word;     
                    counter++;
                }
            }
            
            return result;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            //����
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    //�^�������͂�content�p�����[�^�Ƃ��đΏۂ�URL��Post���s���AXML�Ō��ʂ𓾂�
    private static String doPostToBulkfeeds(String url,String contents){
        HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());
        client.getParams().setParameter("http.protocol.content-charset","UTF-8");
        client.getHttpConnectionManager().getParams().setConnectionTimeout(30000);
        PostMethod post = new PostMethod(url);
        post.setParameter("apikey",API_KEY);
        post.setParameter("content",contents);
        
        try {
            int ipostResultCode = client.executeMethod(post);
            
            final String strGetResponseBody = post.getResponseBodyAsString();
            return strGetResponseBody;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            post.releaseConnection();
        }
        return null;
    }
}