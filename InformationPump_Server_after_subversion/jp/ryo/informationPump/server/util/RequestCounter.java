package jp.ryo.informationPump.server.util;

//�u���E�U����̃��N�G�X�g���������܂��Ă���̂����Ǘ�����
public class RequestCounter {
    private static int request_count = 0;
    private static Object lock_object = new Object();
    
    public static synchronized void incRequestCount(){
        request_count++;
    }
    
    public static synchronized void decRequestCount(){
        request_count--;
        
        synchronized(lock_object){
            lock_object.notifyAll();    
        }
        
        if(request_count < 0){
            throw new RuntimeException();
        }
    }
    
    //�ʐM�������N�����悤�ȃ^�X�N���s���Ă������`�F�b�N
    public static synchronized void checkCanDoCommunicate(){
        while(request_count > 0){
            try {
                lock_object.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}