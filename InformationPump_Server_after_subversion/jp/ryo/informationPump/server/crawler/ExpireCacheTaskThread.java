package jp.ryo.informationPump.server.crawler;

import jp.ryo.informationPump.server.collab.CollaborativeInformatoinManager;
import jp.ryo.informationPump.server.db.abstractor.*;
import jp.ryo.informationPump.server.persistent.PersistentManager;
import jp.ryo.informationPump.server.util.DBUtil;

public class ExpireCacheTaskThread extends Thread {
    
    public ExpireCacheTaskThread(String name){
        super(name);
    }
    public void run() {
        try {
//          �N���[�����h�L�������g�̃��N�G�X�g��M�̖W���ɂȂ�Ȃ��悤�ɗD��x��������
            Thread th = Thread.currentThread();
            th.setPriority(Thread.MIN_PRIORITY);
            
            CrawledDataManager manager = DBCrawledDataManager.getInstance();
            
            //�L���b�V������|����(User�̂���|���邪���Ȃ�)            
            DBCacheManager.removeAllCahe();
            
            System.gc();
            
////            //�������L���b�V���ɍڂ���
////            UserDBAbstractor u_abstractor = UserDBAbstractor.getInstace();
////            u_abstractor.getAllUser();
//            
//            CrawledDataDBAbstractor abstractor = CrawledDataDBAbstractor.getInstance();
//            
//            InvertedMap.setIsCanUse(false);
//            InvertedMap.makeInvertedMap(abstractor.getAllEntries(DBUtil.HATEBU_TYPE),DBUtil.HATEBU_TYPE); 
//            InvertedMap.makeInvertedMap(abstractor.getAllEntries(DBUtil.CEEK_NEWS_TYPE),DBUtil.CEEK_NEWS_TYPE);
//            //�]�u�}�b�v���g�p�\��
//            InvertedMap.setIsCanUse(true);
//            
////            abstractor.getAllEntries(DBUtil.YOUTUBE_TYPE);
////            
//            PersistentManager p_manager = PersistentManager.getInstance();
//            
//            //�R���{���C�e�B�u�t�B���^�̃L���b�V����expire������
//            CollaborativeInformatoinManager collab_manaber = CollaborativeInformatoinManager.getInstance();
//            collab_manaber.expireCaches();
//            
//            //�e�I�u�W�F�N�g�̕ۑ����s��
//            p_manager.escapeObjectsToFile();
                                    
            try {
                System.out.println("Updating Bookmark Counts start!!");
//              ��Ɋ����̃G���g���̃u�b�N�}�[�N�����X�V����            
                manager.updateBookmarkCounts(DBUtil.HATEBU_TYPE);
                System.out.println("updating Bookmark Counts finished!!");
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
