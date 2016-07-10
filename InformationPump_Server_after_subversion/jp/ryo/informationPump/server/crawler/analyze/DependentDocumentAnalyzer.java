package jp.ryo.informationPump.server.crawler.analyze;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import jp.ryo.informationPump.server.crawler.*;

import jp.ryo.informationPump.server.data.DoGetResultWithCharset;
import jp.ryo.informationPump.server.data.KeyAndDoubleTFIDF;
import jp.ryo.informationPump.server.helper.BulkfeedsHelper;
import jp.ryo.informationPump.server.helper.YahooHelper;
import jp.ryo.informationPump.server.util.*;
import net.java.sen.StringTagger;
import net.java.sen.Token;

import org.htmlparser.filters.StringFilter;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.ParserException;

//�^����URL�̃T�C�g����͂��ē����x�N�g�����I�����ɃZ�b�g���ɍs���X���b�h
public class DependentDocumentAnalyzer extends DocumentAnalyzer {
    private ArrayList keywords;
    private String targetURL;
    private String already_known_keywords[];
    private int doc_type;
    
    public DependentDocumentAnalyzer(String url,String already_known[],int doc_type) {
        this.targetURL = url;
        this.already_known_keywords = already_known;
        this.doc_type = doc_type;
    }
    
    public static void giveDocumentToEngine(String url,String already_known[],int doc_type) {
        //�G���W���̏����͕ʃX���b�h�ōs��
        Thread th = new Thread((new DependentDocumentAnalyzer(url,already_known,doc_type)));
        th.setPriority(Thread.MIN_PRIORITY);
        th.start();
    }
    
    //  �h�L�������g�̎�ނɉ����ăp�[�X���ăL�[���[�h�𒊏o���ĕԂ�
    //  �ԋp�����̂�key=�L�[���[�h Value=�]���l(Double)��HashMap
    //  keywords�ɕ]���l���傫�����Ƀ\�[�g���ꂽ�L�[���[�h(String)��v�f�Ƃ��Ď���ArrayList���Z�b�g����
    //  ���s�����ꍇ��null��Ԃ�
    
    //��͂���O�ɒm���Ă���L�[���[�h��^�O��tfidf���v�Z���邽�߂�tag_keywords[]��
    //�n���Ă��悢
    public HashMap parseDocument(String str_body,String tag_keywords[]){
                //�g�[�N���i�C�Y
            Token[] token=null;
            try {
                StringTagger tagger = StringTagger.getInstance();
                token = tagger.analyze(str_body);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            if(token==null){
                return null;    //���̃h�L�������g�͖���
            }
            
            //��Ő����J�E���g���邽�߂�Map��
            HashMap tmpMap = new HashMap();
            for(int i=0; i<token.length; i++){
                  if((token[i].getPos().startsWith("���m��")||(token[i].getPos().startsWith("����")))&&filterKeyword(token[i].getBasicString())){
                      //�ЂƂ܂��L�[���[�h��Map�֍X�V
                      Object obj= tmpMap.get(token[i].getBasicString());
                      if(obj!=null){
                          Integer keyword_count = (Integer)obj;
                          tmpMap.put(token[i].getBasicString(),Integer.valueOf(Integer.toString(keyword_count.intValue() + 1)));
                      }else{
                          tmpMap.put(token[i].getBasicString(),Integer.valueOf("1"));
                      }
                }
            }
            
            //bulkfeeds�ɃA�N�Z�X���ăL�[���[�h���o�̑O����
            String key_strs[] =  BulkfeedsHelper.getImportantWords(str_body);
            
            if(key_strs==null){
                return null;//���̃h�L�������g�͖���
            }
            
            //���O�ɒ��ׂĂ���L�[���[�h���ǉ�
            String marged_key_str[] = new String[key_strs.length+tag_keywords.length];
            for(int i=0;i<key_strs.length;i++){
                marged_key_str[i] = key_strs[i];
            }
            for(int i=key_strs.length;i<marged_key_str.length;i++){
                marged_key_str[i] = tag_keywords[i-key_strs.length];
            }
                
            //�e�L�[���[�h�ɂ���tf/idf�̌v�Z
            ArrayList tmpList = new ArrayList();
            for(int i=0;i<key_strs.length;i++){
                String keyword = key_strs[i];
                Integer count = (Integer)tmpMap.get(key_strs[i]);
                
                //bulkfeeds����Ԃ��Ă����L�[���[�h������or���m��łȂ��ꍇ�͖���
                if(count==null){
                    continue;
                }
                
                int keyCount = count.intValue();
                
                double tfidf;
                try {
                    tfidf = ((float)keyCount) * Math.log(19200000000.0/(double)YahooHelper.getContainsPageCount(new String[]{keyword}));
                    tmpList.add(new KeyAndDoubleTFIDF(keyword,Double.valueOf(String.valueOf(tfidf))));
                } catch (IOException e) {
                    e.printStackTrace();//�����Ɏ��s�����̂ł��̃L�[���[�h�͒ǉ����Ȃ�
                }   
            }
            Collections.sort(tmpList,new DoubleComp());
            
            keywords = new ArrayList();
            //����Tag�̏��Ƃ��ĕێ�(���ʕt������)
            for(int i=0;i<tmpList.size();i++){
                keywords.add(i,((KeyAndDoubleTFIDF)tmpList.get(i)).keyword);
            }
            
            HashMap resultVecMap = new HashMap();

            //���ʂ𐶐�
            int len = keywords.size();
            for(int i=0;i<len;i++){
                KeyAndDoubleTFIDF key_tfidf = (KeyAndDoubleTFIDF)tmpList.get(i);
                resultVecMap.put(key_tfidf.keyword,key_tfidf.tfidf);
            }
            
            return resultVecMap;
    }

    
    public void run() {

        DoGetResultWithCharset result_w_charset = Util.doGetWithCharset(targetURL);
        
        if(result_w_charset!=null){
            String correct_encoded=null;
            try {
                if (result_w_charset.charset != null) {
                    correct_encoded = getEncodedStr(result_w_charset.body,result_w_charset.charset);
                } else {
                    correct_encoded = getCorrectEncodeHTML(result_w_charset.body);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            
//            System.err.println(correct_encoded);
//          �R�����g�^�O(�����炭JavaScript�������Ă��邾�낤)����菜��
            correct_encoded = Util.replaceCommentTags(correct_encoded);
            
            
            correct_encoded = getTextPartOfHTML(correct_encoded);
            
            correct_encoded = Util.replaseSpecialChars(correct_encoded);
            correct_encoded = correct_encoded.replaceAll("\r","");
            correct_encoded = correct_encoded.replaceAll("\n","");
            
            HashMap taste_vec = parseDocument(correct_encoded,already_known_keywords);
            if(taste_vec!=null){
                CrawledDataManager c_manager = DBCrawledDataManager.getInstance();
                //��͌��ʂŉ�͂���Ă��Ȃ���Ԃ���X�V����
                c_manager.setAnalyzedResults(targetURL,taste_vec,keywords,doc_type);
//                System.out.println(taste_vec);
            }else{ 
                CrawledDataManager c_manager = DBCrawledDataManager.getInstance();
                //��͂��ꂽ����������������
                c_manager.setAnalyzedResults(targetURL,new HashMap(),keywords,doc_type);
            }
        }
     }
}