package jp.ryo.informationPump.server.util;

//ブラウザからのリクエストがいくつ溜まっているのかを管理する
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
    
    //通信を引き起こすようなタスクを行っていいかチェック
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
