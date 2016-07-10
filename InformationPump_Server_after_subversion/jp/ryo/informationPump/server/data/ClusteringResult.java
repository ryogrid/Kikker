package jp.ryo.informationPump.server.data;

import java.util.HashMap;

public class ClusteringResult {
//  どの番号のユーザがどのクラスタに所属するかの番号を保持
    public int cluster_map[];
// key=クラスタ番号 Integer,Value=所属するユーザの番号(Integer)を保持するArrayList    
    public HashMap clusters;
    
    private ClusteringResult(){
    
    }
    
    public ClusteringResult(int[] cluster_map, HashMap clusters) {
        this.cluster_map = cluster_map;
        this.clusters = clusters;
    }
}
