package jp.ryo.informationPump.server.debug.profile;
import java.util.TimerTask;


public class UpdateTask extends TimerTask {
    private ThreadMXBeanTest bean; 
    
    
    public UpdateTask(ThreadMXBeanTest bean) {
        super();
        this.bean = bean;
    }


    public void run() {
        bean.clearInfoTab();
        bean.updateInfo();
    }

}
