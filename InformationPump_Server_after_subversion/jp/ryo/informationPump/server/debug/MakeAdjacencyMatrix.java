package jp.ryo.informationPump.server.debug;

import java.io.File;

import jp.ryo.informationPump.server.collab.CollaborativeInformatoinManager;
import jp.ryo.informationPump.server.helper.HatebuHelper;
import jp.ryo.informationPump.server.persistent.PersistentManager;
import jp.ryo.informationPump.server.util.SparseMatrix;

public class MakeAdjacencyMatrix {

    public static void main(String[] args) {
        PersistentManager p_manager = PersistentManager.getInstance();

        File test_file = new File(PersistentManager.COLLAB_DATA_PATH + PersistentManager.COLLAB_MANAGER_DATA_FILE);
        CollaborativeInformatoinManager collab_manager = null;
        if(test_file.exists()){
            collab_manager = (CollaborativeInformatoinManager)p_manager.readObjectFromFile(PersistentManager.COLLAB_DATA_PATH + PersistentManager.COLLAB_MANAGER_DATA_FILE);
            
            SparseMatrix user_and_doc_matrix = collab_manager.getMatrix(); 
            
            SparseMatrix adjacency_matrix = new SparseMatrix();
            
            Double ONE = new Double(1);
            
            int user_len = user_and_doc_matrix.getYlength();
            int document_len = user_and_doc_matrix.getXlength();
            
            //x,y軸ともに同じ順序でユーザを並べるための苦肉の策
            for(int i=0;i<user_len;i++){
                adjacency_matrix.addXstr(user_and_doc_matrix.getYName(i));
                adjacency_matrix.addYstr(user_and_doc_matrix.getYName(i));
                
            }

            //コピー元行列から値をセットしていく
            for(int i=0;i<user_len;i++){
                System.out.println(i + "/" + user_len);
                String user_name = user_and_doc_matrix.getYName(i);
                for(int j=0;j<document_len;j++){
                    Object obj1 = user_and_doc_matrix.getValueByIndex(j,i);
                    if(obj1!=null){
                        //対象のドキュメントを見ているユーザを探す
                        for(int k=0;k<user_len;k++){
                            Object obj2 = user_and_doc_matrix.getValueByIndex(j,k);
                            if(obj2!=null){
                                String adjacency_user_name = user_and_doc_matrix.getYName(k);
                                //自分じゃなければ
                                if(!user_name.equals(adjacency_user_name)){
                                    Object obj3 = adjacency_matrix.getValue(user_name,adjacency_user_name); 
                                    if(obj3!=null){
                                        //すでに隣接していたことが確認されており、値があった場合
                                        Double past_value = (Double)obj3;
                                        adjacency_matrix.setValue(user_name,adjacency_user_name,new Double(past_value.doubleValue()+1));
                                    }else{
                                        adjacency_matrix.setValue(user_name,adjacency_user_name,ONE);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            p_manager.writeObjectToFile(PersistentManager.COLLAB_DATA_PATH + "adjacency_matrix.persistent",adjacency_matrix);
        }
    }

}
