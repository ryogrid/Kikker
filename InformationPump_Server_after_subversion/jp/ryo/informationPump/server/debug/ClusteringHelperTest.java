package jp.ryo.informationPump.server.debug;

import java.io.File;
import java.util.*;

import jp.ryo.informationPump.server.collab.ClusteringHelper;
import jp.ryo.informationPump.server.collab.CollaborativeInformatoinManager;
import jp.ryo.informationPump.server.data.ClusteringResult;
import jp.ryo.informationPump.server.helper.HatebuHelper;
import jp.ryo.informationPump.server.persistent.PersistentManager;
import jp.ryo.informationPump.server.util.SparseMatrix;

public class ClusteringHelperTest {

    public static void main(String[] args) {
        PersistentManager p_manager = PersistentManager.getInstance();

        File test_file = new File(PersistentManager.COLLAB_DATA_PATH + "cluster_map.persistent");
        HashMap collab_manager = null;
        if(test_file.exists()){
            collab_manager = (HashMap)p_manager.readObjectFromFile(PersistentManager.COLLAB_DATA_PATH + "cluster_map.persistent");
        }else{
//            SparseMatrix matrix =  HatebuHelper.crawlForCollab(10);
//            collab_manager = CollaborativeInformatoinManager.getInstance();
//            collab_manager.setMatrix(matrix);
//            p_manager.writeObjectToFile(PersistentManager.COLLAB_DATA_PATH + "cluster_map.persistent",collab_manager);
        }
        
        CollaborativeInformatoinManager c_manager = (CollaborativeInformatoinManager)p_manager.readObjectFromFile(PersistentManager.COLLAB_DATA_PATH + PersistentManager.COLLAB_MANAGER_DATA_FILE);
        Iterator itr = collab_manager.values().iterator();
        int all_count=0;
        int not_zero_count = 0;
        int good_users = 0;
        while(itr.hasNext()){
            ArrayList list = (ArrayList)itr.next();
            if(list.size()>2){
                System.out.println(list);
                good_users+= list.size();
                not_zero_count++;
            }
            all_count++;
        }
        System.out.println(not_zero_count + "/" + all_count);
        System.out.println("goog users :" + good_users);
        System.out.println("avr :" + good_users/not_zero_count);
//        ClusteringHelper c_helper = new ClusteringHelper(c_manager.getMatrix());
//        ClusteringResult result = c_helper.clustering();
////        System.out.println(result.clusters);
//        
//        p_manager.writeObjectToFile(PersistentManager.COLLAB_DATA_PATH + "cluster_map.persistent",result.clusters);
    }

}
