package jp.ryo.informationPump.server.util;

import java.io.Serializable;
import java.util.*;

//2�����̑a�s���\������N���X(�L�[��String�̂�,�l��Double�̂�)
public class SparseMatrix implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private ArrayList x_strs = new ArrayList();
    private ArrayList y_strs = new ArrayList();
    private ArrayList x_crawled_dates = new ArrayList();  //x�Ɠ��l��Index�ŃA�N�Z�X�B�Ή�����x�̗v�f�����N���[�����ꂽ����Date�I�u�W�F�N�g��ێ�����B
    private HashMap x_map = new HashMap();   //X�v�f���L�[�Ƃ��A����X��̗v�f���܂�Map��l�Ƃ��Ď���Map
    private HashMap y_map = new HashMap();   //���łɊY���̗v�f�����邩�m�F���邽�߂����̂���
    
    public synchronized void setValue(String x,String y,Double value){
        Object past_x = x_map.get(x);
        if(past_x==null){
            x_strs.add(x);
            x_crawled_dates.add(new Date());
            
            HashMap new_map = new HashMap();
            new_map.put(y,value);
            x_map.put(x,new_map);
        }else{
            ((HashMap)past_x).put(y,value);
            x_map.put(x,past_x);
        }
        
        Object past_y = y_map.get(y);
        if(past_y==null){
            y_strs.add(y);
            HashMap new_map = new HashMap();
//            new_map.put(x,value);
            y_map.put(y,new_map);
        }
//        else{
//            ((HashMap)past_y).put(x,value);
//            y_map.put(x,past_y);
//        }
    }
    
    public synchronized Double getValue(String x,String y){
        Object obj = x_map.get(x);
        if(obj==null){
            return null;
        }else{
            Object tmpObj = ((HashMap)obj).get(y);
            if(tmpObj!=null){
                return (Double)tmpObj;
            }else{
                return null;
            }
        }
    }
    
    public synchronized Double getValueByIndex(int x,int y){
        try{
            return getValue((String)x_strs.get(x),(String)y_strs.get(y));    
        }catch(Exception e){
            return null;
        }
    }
    
    public synchronized void setValueByIndex(int x,int y,Double value){
         setValue((String)x_strs.get(x),(String)y_strs.get(y),value);    
    }
    
    public synchronized String getXName(int x){
        Object obj = x_strs.get(x);
        if(obj!=null){
            return (String)obj;
        }else{
            return null;
        }
    }
    
    public synchronized String getYName(int y){
        Object obj = y_strs.get(y);
        if(obj!=null){
            return (String)obj;
        }else{
            return null;
        }
    }
    
    public synchronized int getXlength(){
        return x_strs.size();
    }
    
    public synchronized int getYlength(){
        return y_strs.size();
    }
    
    public synchronized int getXIndex(String str){
        return x_strs.indexOf(str);
    }
    
    public synchronized int getYIndex(String str){
        return y_strs.indexOf(str);
    }
    
    public synchronized Date getXCrawledDate(int x){
        Object obj = x_crawled_dates.get(x);
        if(obj!=null){
            return (Date)obj;    
        }else{
            try {
                throw new Exception("objective crawled date is nothing");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
    
    //�w�肵�����ɂ��ȏ�(1���w�肵����A1���O���܂܂��)�O�̓��ɂ��ɃN���[�����ꂽ�G���g���Ɋւ������S�ď���
    public void removeOldEntries(int before_date){
        int len = x_crawled_dates.size();
        Date now = new Date();
        
        ArrayList to_be_removes = new ArrayList();
        for(int i=0;i<len;i++){
            Date date = (Date)x_crawled_dates.get(i);
            if((now.getTime() - date.getTime()) > (86400000*before_date)){
                to_be_removes.add(new Integer(i));
            }
        }
        
        synchronized (this) {
            int remove_len = to_be_removes.size();
            for(int i=0;i<remove_len;i++){
                x_map.remove(getXName(((Integer)to_be_removes.get(i)).intValue()));
                x_crawled_dates.remove(((Integer)to_be_removes.get(i)).intValue());
                x_strs.remove(((Integer)to_be_removes.get(i)).intValue());
            }
        }
    }
    
    public int getTargetXElementLength(int x){
        return getTargetXElementLength(getXName(x));
    }
    
    public int getTargetXElementLength(String x_str){
        Object obj = x_map.get(x_str);
        if(obj!=null){
            return ((HashMap)obj).size();
        }else{
            return -1;
        }
    }
    
    
    //x�Ԗڂ̃G���g�������N���[���������Blong��getTime�̌��ʂ�Ԃ�
    public long getCrawledTime(int x){
        Object obj = getXCrawledDate(x);
        if(obj!=null){
           return  ((Date)obj).getTime();
        }else{
            return -1;
        }
    }
    
    //x_strs�̗v�f�𖳗����ǉ�����B�J�ڍs�����鎞�ȊO�Ɏg��Ȃ��悤�ɁB
    public void addXstr(String x_str){
        this.x_strs.add(x_str);
        this.x_map.put(x_str,new HashMap());
    }
    
//  y_strs�̗v�f�𖳗����ǉ�����B�J�ڍs�����鎞�ȊO�Ɏg��Ȃ��悤�ɁB
    public void addYstr(String y_str){
        this.y_strs.add(y_str);
        this.y_map.put(y_str,new HashMap());
    }
}
