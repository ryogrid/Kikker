package jp.ryo.informationPump.server.debug;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.htmlparser.util.ParserException;

import jp.ryo.informationPump.server.collab.CollaborativeInformatoinManager;
import jp.ryo.informationPump.server.data.CollabSortBox;
import jp.ryo.informationPump.server.helper.HatebuHelper;
import jp.ryo.informationPump.server.persistent.PersistentManager;
import jp.ryo.informationPump.server.util.SparseMatrix;

public class MakeFirstCollabManager {

    public static void main(String[] args) {
        PersistentManager p_manager = PersistentManager.getInstance();

        File test_file = new File(PersistentManager.COLLAB_DATA_PATH + PersistentManager.COLLAB_MANAGER_DATA_FILE);
        CollaborativeInformatoinManager collab_manager = null;
        if(test_file.exists()){
            collab_manager = (CollaborativeInformatoinManager)p_manager.readObjectFromFile(PersistentManager.COLLAB_DATA_PATH + PersistentManager.COLLAB_MANAGER_DATA_FILE);
        }else{
            SparseMatrix matrix=null;
            try {
                matrix = HatebuHelper.crawlForCollab(10);
            } catch (ParserException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            collab_manager = CollaborativeInformatoinManager.getInstance();
            collab_manager.setMatrix(matrix);
            p_manager.writeObjectToFile(PersistentManager.COLLAB_DATA_PATH + PersistentManager.COLLAB_MANAGER_DATA_FILE,collab_manager);
        }
        
////        collab_manager.removeOldEntries();
//        collab_manager.expireCaches();
        ArrayList result = collab_manager.getSuggestedDocumentes("otsune");
        System.out.println();
////        p_manager.writeObjectToFile(PersistentManager.COLLAB_DATA_PATH + PersistentManager.COLLAB_MANAGER_DATA_FILE,collab_manager);
//        
//
//        int len = result.size();
//        for(int i=0;i<20;i++){
//            CollabSortBox tmp =  (CollabSortBox)result.get(i);
//            System.out.println(tmp.url + ":" + tmp.eval_point);
//        }
    }
}
