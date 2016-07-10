package jp.ryo.informationPump.server.crawler.analyze;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import jp.ryo.informationPump.server.crawler.*;

import jp.ryo.informationPump.server.data.DoGetResultWithCharset;
import jp.ryo.informationPump.server.data.KeyAndDoubleTFIDF;
import jp.ryo.informationPump.server.util.*;
import net.java.sen.StringTagger;
import net.java.sen.Token;

//�^����URL�̃T�C�g����͂��ē����x�N�g�����I�����ɃZ�b�g���ɍs���X���b�h
public class InDependentDocumentAnalyzer extends DocumentAnalyzer {
    private static final double UNIQUE_BONUS = 1.5;
    private static final int KEYWORD_LIMIT = 10; //��͌��ʂ̂�����ʂ����܂ł�ێ����邩
    private ArrayList keywords;
    private String targetURL;
    private String already_known_keywords[];
    private int doc_type;
    private static int all_instance_count = 0;
    private static Object rock_object = new Object();
    
    private static final int SIMONTANEOUS_RUNNNING_LIMIT = 10;
    
    public InDependentDocumentAnalyzer(){
    }
    
    public InDependentDocumentAnalyzer(String url,String already_known[],int doc_type) {
        this.targetURL = url;
        this.already_known_keywords = already_known;
        this.doc_type = doc_type;
    }
    
    public static void giveDocumentToEngine(String url,String already_known[],int doc_type) {
        //�G���W���̏����͕ʃX���b�h�ōs��
        Thread th = new Thread((new InDependentDocumentAnalyzer(url,already_known,doc_type)),"InDependentDocumentAnalyzer");
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
//          �g�[�N���i�C�Y
            Token[] token = null;
            try {
                StringTagger tagger = StringTagger.getInstance();
                token = tagger.analyze(str_body);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            // ��Ő����J�E���g���邽�߂�Map��
            HashMap tmpMap = new HashMap();
            // ��Ō`�ԑf�̗v�f�̃R�X�g�𓾂邽�߂�Map
            HashMap token_map = new HashMap();
            ArrayList keyword_list = new ArrayList();
            for (int i = 0; i < token.length; i++) {
                if ((token[i].getPos().startsWith("���m��") || (token[i].getPos().startsWith("����")))
                        && filterKeyword(token[i].getBasicString())&& !Util.checkIgnoreKeyword(token[i].getBasicString())) {
                    // �ЂƂ܂��L�[���[�h��Map�֍X�V
                    Object obj = tmpMap.get(token[i].getBasicString());
                    if (obj != null) {
                        Integer keyword_count = (Integer) obj;
                        tmpMap.put(token[i].getBasicString().intern(), Integer
                                .valueOf(Integer
                                        .toString(keyword_count.intValue() + 1)));
                    } else {
                        tmpMap.put(token[i].getBasicString(), Integer.valueOf("1"));
                        keyword_list.add(token[i].getBasicString().intern());
                        token_map.put(token[i].getBasicString().intern(), token[i]);
                    }
                }
            }
            token = null;

            String key_strs[] = new String[keyword_list.size()];
            int len = key_strs.length;
            for (int i = 0; i < len; i++) {
                key_strs[i] = (String) keyword_list.get(i);
            }

//���O�Ō��o�����L�[���[�h�ɂ��Ă͌v�Z���Ȃ��̂ŃR�����g�A�E�g            
//            // �e�L�[���[�h�ɂ���tf/idf�̌v�Z
//            ArrayList tmpList = new ArrayList();
//            for (int i = 0; i < key_strs.length; i++) {
//                String keyword = key_strs[i];
//                Integer count = (Integer) tmpMap.get(key_strs[i]);
//
//                int keyCount = count.intValue();
//
//                double tfidf;
//                Token tmp_token = (Token) token_map.get(key_strs[i]);
//                if(tmp_token.getPos().startsWith("���m��")){
//                    tfidf = (1 + Math.log(((float) keyCount)+1))
//                    * (tmp_token.getCost()*UNIQUE_BONUS) / 1000000.0;
//                    tmpList.add(new KeyAndDoubleTFIDF(keyword, Double.valueOf(String
//                            .valueOf(tfidf))));
//                }else{
//                    tfidf = (1 + Math.log(((float) keyCount)+1))
//                    * (tmp_token.getCost()) / 1000000.0;
//                    tmpList.add(new KeyAndDoubleTFIDF(keyword, Double.valueOf(String
//                            .valueOf(tfidf))));    
//                }
//            }
//            Collections.sort(tmpList, new DoubleComp());
            
            //���łɂ��������̂ɑ΂��Ă��v�Z����
            int already_len = already_known_keywords.length;
            KeyAndDoubleTFIDF already_exists[] = new KeyAndDoubleTFIDF[already_len];
            for(int i=0;i<already_len;i++){
                try {
                    StringTagger tagger = StringTagger.getInstance();
                    Token tokens[] = tagger.analyze(already_known_keywords[i]);
                    if((tokens!=null)&&(tokens.length > 0)){
                        already_exists[i] = new KeyAndDoubleTFIDF(already_known_keywords[i],new Double((1 + Math.log(((float) Util.countStr(str_body, already_known_keywords[i]))+1)) * (tokens[0].getCost()*UNIQUE_BONUS) / 1000000.0));
                    }else{
                        already_exists[i] = new KeyAndDoubleTFIDF(already_known_keywords[i],new Double(500));
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // ���ʂ𐶐�
            
            //�܂��A���łɂ��������̂���ǉ�
            HashMap resultVecMap = new HashMap();
            int first_put_count = 0;
            for(int i=0;i<((already_len > KEYWORD_LIMIT) ? KEYWORD_LIMIT:already_len); i++){
                KeyAndDoubleTFIDF key_tfidf = already_exists[i];
                resultVecMap.put(key_tfidf.keyword, key_tfidf.tfidf);
                first_put_count++;
            }

//          ���O�Ō��o�����L�[���[�h�ɂ��Ă͌v�Z���Ȃ��̂ŃR�����g�A�E�g              
//            //���ɐV������͂������̂�ǉ�
//            int len2 = key_strs.length;
//            for (int i = 0; i < ((len2 > (KEYWORD_LIMIT-first_put_count)) ? (KEYWORD_LIMIT-first_put_count):len2); i++) {
//                KeyAndDoubleTFIDF key_tfidf = (KeyAndDoubleTFIDF) tmpList.get(i);
//                resultVecMap.put(key_tfidf.keyword, key_tfidf.tfidf);
//            }
            
            //keywords�͂����ƒl���Z�b�g���Ă��Ӗ����Ȃ����ۂ�
            keywords = new ArrayList();
            
            return resultVecMap;
    }
    
    public HashMap analyzeByURLNotAsThread(String url){
        already_known_keywords = new String[0];
        return analyzeByURL(url);
    }
    
    public HashMap analyzeByURL(String url){

        DoGetResultWithCharset result_w_charset = Util.doGetWithCharset(url);
        
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
        
            return taste_vec;
        }else{
            return new HashMap();
        }
    }
    
    public static void tryMoving(){
        synchronized(rock_object){
            while(all_instance_count > SIMONTANEOUS_RUNNNING_LIMIT){
                try {
                    rock_object.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            all_instance_count++;
        }
    }
    
    public static void returnMovingLight(){
        synchronized (rock_object) {
            all_instance_count--;
            rock_object.notifyAll();
        }
    }
    
    public void run() {
      try {
            tryMoving();
            System.out.println("InDependentDocumentAnalyzer instance count is"
                    + all_instance_count + " now");


            HashMap taste_vec = analyzeByURL(targetURL);
            if (taste_vec != null) {
                CrawledDataManager c_manager = DBCrawledDataManager
                        .getInstance();
                //��͌��ʂŉ�͂���Ă��Ȃ���Ԃ���X�V����
                c_manager.setAnalyzedResults(targetURL, taste_vec, keywords,
                        doc_type);
                //            System.out.println(taste_vec);
            } else {
                CrawledDataManager c_manager = DBCrawledDataManager
                        .getInstance();
                //��͂��ꂽ����������������
                c_manager.setAnalyzedResults(targetURL, new HashMap(),
                        keywords, doc_type);
            }
            
        }catch(Throwable e){ 
            e.printStackTrace();
        }finally{
            returnMovingLight();
        }        
     }
}
