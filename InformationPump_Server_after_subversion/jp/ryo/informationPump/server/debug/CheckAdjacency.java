package jp.ryo.informationPump.server.debug;

import jp.ryo.informationPump.server.persistent.PersistentManager;
import jp.ryo.informationPump.server.util.SparseMatrix;

public class CheckAdjacency {

    /**
     * @param args
     */
    public static void main(String[] args) {
        PersistentManager p_manager = PersistentManager.getInstance();
        SparseMatrix adjacency_matrix = (SparseMatrix)p_manager.readObjectFromFile(PersistentManager.COLLAB_DATA_PATH + "adjacency_matrix.persistent");
        
        int len = adjacency_matrix.getXlength();
        for(int i=0;i<len;i++){
            for(int j=0;j<len;j++){
                Object obj1 = adjacency_matrix.getValueByIndex(i,j);
                Object obj2 = adjacency_matrix.getValueByIndex(j,i);
                if(obj1!=null){
                    System.out.println( obj1 + "," + obj2);    
                }
            }
        }
    }

}
