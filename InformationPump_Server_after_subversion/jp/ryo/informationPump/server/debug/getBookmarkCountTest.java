/*
 * �쐬��: 2006/04/17
 *
 * ���̐������ꂽ�R�����g�̑}�������e���v���[�g��ύX���邽��
 * �E�B���h�E > �ݒ� > Java > �R�[�h���� > �R�[�h�ƃR�����g
 */
package jp.ryo.informationPump.server.debug;

import java.util.Date;

import jp.ryo.informationPump.server.data.DocumentEntry;
import jp.ryo.informationPump.server.data.StoreBoxForDocumentEntry;
import jp.ryo.informationPump.server.helper.HatebuHelper;
import jp.ryo.informationPump.server.util.DBUtil;

/**
 * @author ryo
 *
 * ���̐������ꂽ�R�����g�̑}�������e���v���[�g��ύX���邽��
 * �E�B���h�E > �ݒ� > Java > �R�[�h���� > �R�[�h�ƃR�����g
 */
public class getBookmarkCountTest {

    public static void main(String[] args) {
        String urls[] = new String[4];
//        StoreBoxForDocumentEntry entries[] = new StoreBoxForDocumentEntry[200];
        urls[0] = "http://ryogrid.myhome.cx/osekkai/";
        urls[1]= "http://www.google.com";
        urls[2]= "http://d.hatena.ne.jp/tyouiifan";
        urls[3] = "http://d.hatena.ne.jp/tyouiifan";
        
//        for(int i=0;i<200;i++){
//            DocumentEntry entry = new DocumentEntry("http://google.com" + i,new Date(),null,null,null,0,null,DBUtil.HATEBU_TYPE);
//            entries[i] = new StoreBoxForDocumentEntry(entry,new Date());
//        }
//        
//        
//        int results[] = HatebuHelper.getBookmarkedCount(entries);
        
//        for(int i = 0;i<urls.length;i++){
//            urls[i] = "http://ryogrid.myhome.cx" + i;
//        }
//        
        int results[] = HatebuHelper.getBookmarkedCount(urls); 
        
        for(int i=0;i<results.length;i++){
            System.out.println(i + ":" + results[i]);
        }
        
    }
}