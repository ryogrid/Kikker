package jp.ryo.informationPump.server.data;

import java.util.HashMap;

public class ClusteringResult {
//  �ǂ̔ԍ��̃��[�U���ǂ̃N���X�^�ɏ������邩�̔ԍ���ێ�
    public int cluster_map[];
// key=�N���X�^�ԍ� Integer,Value=�������郆�[�U�̔ԍ�(Integer)��ێ�����ArrayList    
    public HashMap clusters;
    
    private ClusteringResult(){
    
    }
    
    public ClusteringResult(int[] cluster_map, HashMap clusters) {
        this.cluster_map = cluster_map;
        this.clusters = clusters;
    }
}
