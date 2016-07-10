package jp.ryo.informationPump.server.debug;

import java.util.ArrayList;

import jp.ryo.informationPump.server.collab.ClusteringHelper;
import jp.ryo.informationPump.server.persistent.PersistentManager;
import jp.ryo.informationPump.server.util.SparseMatrix;

public class FindCommunityWithGraphClustering {

    public static void main(String[] args) {
        PersistentManager p_manager = PersistentManager.getInstance();
        
        SparseMatrix adjacency_matrix = (SparseMatrix)p_manager.readObjectFromFile(PersistentManager.COLLAB_DATA_PATH + "adjacency_matrix.persistent");
        ClusteringHelper c_helper = new ClusteringHelper(adjacency_matrix);
        
        String user_name = "kanbayashi";
        int k = 10;
        
//        ArrayList community_set = c_helper.findCommunityAroundAUser(user_name,k);
        ArrayList community_set = c_helper.findCommunityAroundAUserOriginal(user_name,k);
        System.out.println(community_set);
        GraphOutputFrame g_frame = new GraphOutputFrame(user_name + "ÇÃé¸ÇËÇÃêlíB",adjacency_matrix,community_set);
        g_frame.setVisible(true);
    }
}
