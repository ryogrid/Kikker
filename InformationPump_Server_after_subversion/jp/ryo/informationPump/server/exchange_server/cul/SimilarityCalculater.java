//�Q�̕����x�N�g���̗ގ��x���v�Z����N���X
package jp.ryo.informationPump.server.exchange_server.cul;

import java.util.*;

import jp.ryo.informationPump.server.data.UserProfile;
import jp.ryo.informationPump.server.data.WebData;

public class SimilarityCalculater {
    public static final double SIMIRARITY_THRESHOLD = 0.8;
    public static final double HATEBU_THRESHOLD=0.2;
    public static final double USER_THRESHOLD=0.7;
    
    //0�`1�ɐ��K�����ꂽ�ގ��x��Ԃ�
    //�^����HashMap��key=String,Value=Double�łȂ���΂Ȃ�Ȃ�
    public static double calculateSimilarity(HashMap map_a,HashMap map_b){
        //cos_distance = ��(ai*bi)/��((��(ai)^2)*(��(bi)^2))�ƃR�T�C�����������߂�
        Set map_a_set = map_a.entrySet();
        Iterator map_a_itr = map_a_set.iterator();
        
        double innerProduct = 0;
        double to_a_abs = 0;   //a�x�N�g���̐�Βl���v�Z���邽�߂̈ꎞ�ϐ�
        double to_b_abs = 0;   //b�x�N�g���̐�Βl���v�Z���邽�߂̈ꎞ�ϐ�
        
        HashMap map_b_clone = (HashMap)map_b.clone();
        
        while(map_a_itr.hasNext()){
            Map.Entry entry  = (Map.Entry)map_a_itr.next();
            String keyword = (String)entry.getKey();
            double tfidf_a = ((Double)entry.getValue()).doubleValue();
            
            Object obj = map_b.get(keyword);
            if(obj!=null){//�Ή�����L�[���[�h��b�x�N�g���ɂ��������ꍇ
                double tfidf_b = ((Double)obj).doubleValue();
                
                innerProduct += tfidf_a*tfidf_b;
                to_a_abs += tfidf_a*tfidf_a;
                to_b_abs += tfidf_b*tfidf_b;
                
                map_b_clone.remove(keyword);
            }else{  //�Ή�����L�[���[�h���Ȃ������ꍇ
                to_a_abs += tfidf_a*tfidf_a;
                
                //innerProduct��to_b_abs�͌v�Z���Ă��l��0�Ȃ̂Ōv�Z���Ȃ��Ă悢
            }
        }
        
        //b�x�N�g���ɂ�����a�x�N�g���ɂȂ������L�[���[�h�ɂ��Čv�Z����
        Set map_b_set = map_b_clone.entrySet();
        Iterator map_b_itr = map_b_set.iterator();
        while(map_b_itr.hasNext()){
            Map.Entry entry  = (Map.Entry)map_b_itr.next();
            double tfidf_b = ((Double)entry.getValue()).doubleValue();
            
            to_b_abs += tfidf_b*tfidf_b;
        }
        
        //��̌v�Z��NaN���o���Ă��܂��̂�����邽�߁A�����Ōv�Z���ʂ��Z�o
        if((innerProduct==0)||(to_a_abs==0)||(to_b_abs==0)){
            return 0;
        }
        
        //���ۂɃR�T�C�����������߂�
        double cos_distance = innerProduct/Math.sqrt(to_a_abs*to_b_abs);
        
        //�R�T�C����������p�x(0�`pi)�𓾂�
        double angle = Math.acos(cos_distance);
        
        //���K�����s��0�`1�̒l�ɂ���
        double similarity = (Math.PI -angle) / Math.PI;
        
        return similarity;
    }
    
    //�w�肵��Web�ƃ��[�U�[�̚n�D�͎��ʂ��Ă���̂��ǂ�����Ԃ�
    public static boolean isSimilar(WebData web,UserProfile user){
        double simirarity = getSimirarityBetweenWebUser(web, user);
        
        if(simirarity>=SIMIRARITY_THRESHOLD){
            return true;
        }else{
            return false;
        }
    }

    private static double getSimirarityBetweenWebUser(WebData web, UserProfile user) {
        double simirarity = calculateSimilarity(web.getKeyword_vector(),user.getTasteVector());
        return simirarity;
    }
    
//  �w�肵��Web�ƃ��[�U�[�̚n�D�͎��ʂ��Ă���̂��ǂ�����Ԃ�
    public static boolean isSimilar(WebData web_a,WebData web_b){
        double simirarity = getSimirarityBetween2Web(web_a, web_b);
        
        if(simirarity>=SIMIRARITY_THRESHOLD){
            return true;
        }else{
            return false;
        }
    }
    
//  �w�肵��Web�Ɨ^�����n�D�͎��ʂ��Ă���̂��ǂ�����Ԃ�
    public static boolean isSimilar(WebData web_a,HashMap taste_vector){
        double simirarity = calculateSimilarity(web_a.getKeyword_vector(),taste_vector);
        debugOutput(web_a.getAddress(),simirarity);
        
        if(simirarity>=SIMIRARITY_THRESHOLD){
            return true;
        }else{
            return false;
        }
    }

    private static double getSimirarityBetween2Web(WebData web_a, WebData web_b) {
        double simirarity = calculateSimilarity(web_a.getKeyword_vector(),web_b.getKeyword_vector());
        return simirarity;
    }
    
    private static void debugOutput(String file_name,double similarity){
        System.out.println(file_name + ":" + similarity);
    }
}
