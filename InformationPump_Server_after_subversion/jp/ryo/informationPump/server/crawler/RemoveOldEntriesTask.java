package jp.ryo.informationPump.server.crawler;

import java.util.TimerTask;

import jp.ryo.informationPump.server.collab.CollaborativeInformatoinManager;
import jp.ryo.informationPump.server.db.abstractor.*;
import jp.ryo.informationPump.server.util.DBUtil;

public class RemoveOldEntriesTask extends TimerTask {
    public static final int REMOVE_PERIOD_SECONDS =3600;
    
    public void run() {
        try {
//          �N���[�����h�L�������g�̃��N�G�X�g��M�̖W���ɂȂ�Ȃ��悤�ɗD��x��������
            Thread th = Thread.currentThread();
            th.setPriority(Thread.MIN_PRIORITY);
            
            CrawledDataManager manager = DBCrawledDataManager.getInstance();
            //�Â��G���g�����폜����
            manager.removeOldEntries(DBUtil.HATEBU_TYPE);
            manager.removeOldEntries(DBUtil.CEEK_NEWS_TYPE);
            manager.removeOldEntries(DBUtil.YOUTUBE_TYPE);
            
            //�R���{���C�e�B�u�t�B���^�p�̏����Â����̂�����
            CollaborativeInformatoinManager collab_manager = CollaborativeInformatoinManager.getInstance();
            collab_manager.removeOldEntries();
            
//            //�L���b�V������|����(User�̂���|���邪���Ȃ�)            
//            DBCacheManager.removeAllCahe();
//            
//            //�������L���b�V���ɍڂ���
//            UserDBAbstractor u_abstractor = UserDBAbstractor.getInstace();
//            u_abstractor.getAllUser();
//            
//            CrawledDataDBAbstractor abstractor = CrawledDataDBAbstractor.getInstance();
//            abstractor.getAllEntries(DBUtil.HATEBU_TYPE);
//            abstractor.getAllEntries(DBUtil.CEEK_NEWS_TYPE);
//            abstractor.getAllEntries(DBUtil.YOUTUBE_TYPE);
            
//            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
