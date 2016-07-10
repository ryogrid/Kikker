package jp.ryo.informationPump.server.collab;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.*;

import org.htmlparser.util.ParserException;

import jp.ryo.informationPump.server.data.CollabSortBox;
import jp.ryo.informationPump.server.helper.HatebuHelper;
import jp.ryo.informationPump.server.persistent.PersistentManager;
import jp.ryo.informationPump.server.util.DoubleComp;
import jp.ryo.informationPump.server.util.SparseMatrix;

//���[�U���ǂ̃y�[�W���u�N�}�����̂��Ƃ��������Ǘ�����N���X
public class CollaborativeInformatoinManager implements Serializable{
    private static final long serialVersionUID = 1L;
    private static CollaborativeInformatoinManager instance;
    private final int REMOVE_ENTRIES_BEFORE_DATE = 5; //�Â������������ɉ����O�̂��̂܂ŏ�����
    private final int SUGGEST_TARGET_ENTRIES_DATE_START = 2; //�����Ɏw�肳�ꂽ���ɂ��܂ł̃G���g���𐄑E����
    private final int SUGGEST_TARGET_ENTRIES_DATE_END = 1; //�����Ɏw�肳�ꂽ���ɂ�+1�ȏ�̓��܂ł̃G���g���𐄑E����
    
    private HashMap avr_cache = new HashMap();
    private HashMap Cij_cache = new HashMap();
    private SparseMatrix matrix;
    
    private int LARGE_NUMBER_FOR_CACHE_INDEX = 100000;
    
    public static CollaborativeInformatoinManager getInstance(){
        if(instance == null){
            instance = new CollaborativeInformatoinManager();
        }
        return instance;
    }
    
    public static void setInstance(CollaborativeInformatoinManager instance){
        CollaborativeInformatoinManager.instance = instance;
    }
    
    public ArrayList getSuggestedDocumentes(String user_id){
        expireCaches();
        
        if(matrix==null){
            return new ArrayList();
        }
        
        int i = matrix.getYIndex(user_id);
        
        ArrayList suggest_urls = new ArrayList();
        int len = matrix.getXlength();
        
        double base_user_bookmarked[] = new double[len];
        for(int j=0;j<len;j++){
            Object obj = matrix.getValueByIndex(j,i);
            if(obj!=null){
                base_user_bookmarked[j] = ((Double)obj).doubleValue();
            }else{
                base_user_bookmarked[j] = 0;
            }
        }
        
        //���E����ΏۂƂ���G���g�����n�܂��Ă���n�_�����߂�
        int start_point = 0;

        long now = (new Date()).getTime(); 
        for(int x=0;x<len;x++){
            
            if((now - matrix.getCrawledTime(x)) < (86400000l*SUGGEST_TARGET_ENTRIES_DATE_START)){
                break;
            }
            start_point++;
        }
        
        //���E����Ώۂ��I����Ă���n�_�����߂�
        int end_point = start_point;
        for(int x=start_point+1;x<len;x++){
//            System.out.println((now - matrix.getCrawledTime(x)));
//            if((now - matrix.getCrawledTime(x)) < 43200000*SUGGEST_TARGET_ENTRIES_DATE_END){
//                break;
//            }
            end_point++;
        }
        
        for(int x=start_point;x<end_point;x++){
            double taste = calculateADocumentTaste(i,x,base_user_bookmarked);
//            System.out.println(x + "/" + len + " : " + taste);
            //��߂�ꂽ�ȏ�̚n�D�̓x�����ł��A���łɃu�N�}���Ă����肵�Ȃ��G���g��
//            if((taste > SUGGEST_THRESHOLD)&&(matrix.getValueByIndex(x,i)==null)){
//            if(taste > SUGGEST_THRESHOLD){
            
            //�{�l���u�N�}���Ă��Ȃ��G���g���ł����
            if(matrix.getValueByIndex(x,i)==null){
                suggest_urls.add(new CollabSortBox(matrix.getXName(x),new Double(taste),matrix.getXCrawledDate(x),matrix.getTargetXElementLength(x)));
            }
        }
        
        Collections.sort(suggest_urls,new DoubleComp());
        int len2 = suggest_urls.size();

        return suggest_urls;
    }
    
    
    public double culculateADocumentTaste(String user_id,int x,double base_user_bookmarked[]){
        return calculateADocumentTaste(matrix.getYIndex(user_id), x,base_user_bookmarked);
    }

//  i�Ԗڂ̃��[�U��x�Ԗڂ̃h�L�������g�͂ǂ̒��x���E�߂��̒l�𓾂�
    public double calculateADocumentTaste(int i,int x,double base_user_bookmarked[]){
        int len = matrix.getYlength();
        double denominator = 0;
        double all_target_document_viewrs = 0;   //�Ώۂ̃h�L�������g���u�N�}�����l��
        double similar_user_count = 0;   //�����Ȃ�Ƃ����ւ̂��郆�[�U��
        double target_document_view_by_similar = 0; //���ւ̂��郆�[�U���Ώۂ̃h�L�������g���u�N�}������

        synchronized(matrix){
            for(int j=0;j<len;j++){
                if(j==i){
                    continue;
                }
                

                
                double Cij = calculateCoefficient(i,j,base_user_bookmarked);
                if(Cij!=0){
                    double Mjx = 0;
                    Object obj1 = matrix.getValueByIndex(x,j);
                    if(obj1!=null){
                        target_document_view_by_similar++;
//                        all_target_document_viewrs++;
                        Mjx = ((Double)obj1).doubleValue();
                    }

                    similar_user_count++;
                    
                    double Mj = getAvr(j);
                    denominator += Cij*(Mjx - Mj);
                }
            }
        }
        all_target_document_viewrs = matrix.getTargetXElementLength(x);
//        
//        if(all_target_document_viewrs != all_target_document_viewrs2){
//            System.out.println("all_target_document_viewrs is not match : " + all_target_document_viewrs + " and " + all_target_document_viewrs2);
//        }
            
        //�S�̂̒��őΏۂ̃h�L�������g���u�N�}���ꂽ�䗦
        double whole_ratio = all_target_document_viewrs / len;
        //���ւ̂��郁���o�[�̒��őΏۂ̃h�L�������g���u�N�}���ꂽ�䗦
        double in_similar_users_ratio = target_document_view_by_similar / similar_user_count;
        double peculiar_bonus = in_similar_users_ratio / whole_ratio;
        
        return peculiar_bonus*denominator;
    }
    
    private double getAvr(int arg_i){
        if(avr_cache.containsKey(new Integer(arg_i))){
            return ((Double)avr_cache.get(new Integer(arg_i))).doubleValue();
        }else{
            double sum_for_Mi = 0;
            int x_len = matrix.getXlength();
//            int true_len = 0;
            for(int x=0;x<x_len;x++){
                Object obj = matrix.getValueByIndex(x,arg_i);
                if(obj!=null){
                    sum_for_Mi+= ((Double)obj).doubleValue();
//                    true_len++;
                }
            }
            
//            avr_cache.put(new Integer(arg_i),new Double(sum_for_Mi/true_len));
//            return sum_for_Mi/true_len;
            avr_cache.put(new Integer(arg_i),new Double(sum_for_Mi/x_len));
            return sum_for_Mi/x_len;
        }
    }
    
    
//  i�s��j�s�̑��֌W�������߂�
    public double calculateCoefficient(int arg_i,int arg_j,double base_user_bookmark[]){
        if(Cij_cache.containsKey(new Integer(arg_i*LARGE_NUMBER_FOR_CACHE_INDEX+arg_j))){
            return ((Double)Cij_cache.get(new Integer(arg_i*LARGE_NUMBER_FOR_CACHE_INDEX+arg_j))).doubleValue();
        }else if(Cij_cache.containsKey(new Integer(arg_j*LARGE_NUMBER_FOR_CACHE_INDEX+arg_i))){
            return ((Double)Cij_cache.get(new Integer(arg_j*LARGE_NUMBER_FOR_CACHE_INDEX+arg_i))).doubleValue();
        }else{
            //��v���Ă���������߂�
            int x_len = matrix.getXlength();
            
            int tmp_result = 0;
            for(int x=0;x<x_len;x++){
//                Object obj1 = matrix.getValueByIndex(x,arg_i);
                if(base_user_bookmark[x]!=0){
                    Object obj2 = matrix.getValueByIndex(x,arg_j);
                    if(obj2!=null){
                        tmp_result++;
                    }    
                }
            }
            
            if((tmp_result)==0){
                Cij_cache.put(new Integer(arg_i*LARGE_NUMBER_FOR_CACHE_INDEX+arg_j),new Double(0));
                return 0;
            }
            
            Cij_cache.put(new Integer(arg_i*LARGE_NUMBER_FOR_CACHE_INDEX+arg_j),new Double((double)tmp_result));
//            System.out.println("coefficient:" + tmp_result);

            return (double)tmp_result;
        }
    }
    
    public void setMatrix(SparseMatrix matrix){
        this.matrix = matrix;
    }
    
    public static void escapeObjectToFile() {
        PersistentManager p_manager = PersistentManager.getInstance();
        p_manager.writeObjectToFile(PersistentManager.COLLAB_DATA_PATH + PersistentManager.COLLAB_MANAGER_DATA_FILE,CollaborativeInformatoinManager.getInstance());
    }
    
    public void removeOldEntries(){
        if(matrix!=null){
            matrix.removeOldEntries(REMOVE_ENTRIES_BEFORE_DATE);    
        }
    }
    
    public void expireCaches(){
        avr_cache = new HashMap();
        Cij_cache = new HashMap();
    }
    
    public SparseMatrix getMatrix(){
        return matrix;
    }
    
    public void updateBookmarkCounts() throws ParserException, MalformedURLException, IOException{
        if(matrix != null){
//          �X�V����ΏۂƂ���G���g�����n�܂��Ă���n�_�����߂�
            int start_point = 0;
            long now = (new Date()).getTime();
            int len = matrix.getXlength();
            
            for(int x=0;x<len;x++){
                if((now - matrix.getCrawledTime(x)) < 43200000*SUGGEST_TARGET_ENTRIES_DATE_START){
                    break;
                }
                start_point++;
            }
            
            String url_buf[] = new String[50];
            for(int i=0;i<50;i++){
                url_buf[i] = "";
            }
            
            int counter = 0;
            for(int x=start_point;x<len;x++){
                try {
                    url_buf[counter] = matrix.getXName(x);
                    counter++;
                    if (counter == 50) {
                        //                  url����萔���߂��̂ōX�V���ׂ����`�F�b�N����
                        counter = 0;
                        checkUpdated(url_buf, 50);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }                
            }
            try {
                //50�ɖ����Ȃ��������̂ɑ΂��Ă��s��
                checkUpdated(url_buf, counter);
            } catch (Exception e) {
                e.printStackTrace();
            }            
        }
    }

    private void checkUpdated(String[] url_buf,int limit) throws ParserException, MalformedURLException, IOException {
        int result[] = HatebuHelper.getBookmarkedCount(url_buf);
        for(int i=0;i<limit;i++){
            //�����Ă���f�[�^�Ɣ�ׂău�b�N�}�[�N���������Ă���΍X�V����
            if(matrix.getTargetXElementLength(url_buf[i]) != result[i]){    
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                HatebuHelper.crawlEachBookmarkPageForUpdate(url_buf[i],matrix);
            }
        }
    }
}
