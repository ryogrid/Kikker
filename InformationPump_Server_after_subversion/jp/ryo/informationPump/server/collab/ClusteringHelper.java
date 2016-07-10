package jp.ryo.informationPump.server.collab;

import java.util.ArrayList;
import java.util.HashMap;

//import sun.security.krb5.internal.aj;

import jp.ryo.informationPump.server.data.ClusteringResult;
import jp.ryo.informationPump.server.util.SparseMatrix;

public class ClusteringHelper {
    private HashMap distance_cache = new HashMap();
    private final double CLUSTER_THRESHOLD = 1;
    private SparseMatrix matrix;
    
    private int LARGE_NUMBER_FOR_CACHE_INDEX = 100000;
    
    //�O���t�ł̃N���X�^�����O�̏ꍇ�p
      //�Ԃ�l��n�����߂̕ϐ�
      double y_return = 0;
      double z_return = 0;
      
      double dt_return = 0;
      
      //�I���W�i���ł�vj�̗אڃm�[�h�̃C���f�b�N�X��Ԃ����߂̕ϐ�
      int adjacency_index_return = 0;
      
    private ClusteringHelper(){
        
    }
    
    public ClusteringHelper(SparseMatrix matrix){
        this.matrix = matrix;
    }
    
    public ClusteringResult clusteringWithSimple(){
        int user_len = matrix.getYlength();
        
        int cluster_count = 0;
        ArrayList cluster_heads = new ArrayList();
        
//      �ǂ̔ԍ��̃��[�U���ǂ̃N���X�^�ɏ������邩�̔ԍ���ێ�
        int cluster_map[] = new int[user_len];
//      key=�N���X�^�ԍ� Integer,Value=�������郆�[�U�̔ԍ�(Integer)��ێ�����ArrayList
        HashMap clusters = new HashMap(); 
        
        //��ڂ̃N���X�^��ݒ�
        cluster_heads.add(new Integer(0));
        cluster_map[0] = cluster_count;
        ArrayList belongs = new ArrayList();
        belongs.add(new Integer(0));
        clusters.put(new Integer(cluster_count++),belongs);

        int x_length = matrix.getXlength();
        for(int i=1;i<user_len;i++){
            System.out.println("now moving user number:" + i + "/" + user_len + ",cluster count=" + cluster_count);
            int cluster_len = cluster_heads.size();
            
            //�l�̎Q�Ƃ𑁂����邽�߂ɁA��ƂȂ�x�N�g���̒l��z��ɒl�����o���Ă���
            double base_vector[] = new double[x_length];
            for(int j=0;j<x_length;j++){
                Object obj1 = matrix.getValueByIndex(j,i);
                if(obj1!=null){
                    base_vector[j] = ((Double)obj1).doubleValue();    
                }else{
                    base_vector[j] = 0;
                }
            }
            
            int max_head_index = -1; //����ň�ԋ߂��N���X�^�̑�\�_��Index
            double max_distance = 0;  //����ň�ԋ߂��N���X�^�̑�\�_�Ƃ̋���
            for(int j=0;j<cluster_len;j++){
                int cluster_head_index = ((Integer)cluster_heads.get(j)).intValue();
                double distance = calculateEseDistance(i,cluster_head_index,base_vector);
                
                //�V���Ȓ��߂̃N���X�^�𔭌�������
                if(max_head_index == -1){
                    if(distance > CLUSTER_THRESHOLD){
                        max_head_index = cluster_head_index;
                        max_distance = distance;
                    }
                }else{ 
                    if(distance > max_distance){
                        max_head_index = cluster_head_index;
                        max_distance = distance;
                    }
                }
            }
            
            if(max_head_index == -1){
                //���臒l���߂��N���X�^��������Ȃ������ꍇ
                cluster_heads.add(new Integer(i));
                cluster_map[i] = cluster_count;
                ArrayList new_belongs = new ArrayList();
                new_belongs.add(new Integer(i));
                clusters.put(new Integer(cluster_count++),new_belongs);
            }else{
                //�N���X�^�����������ꍇ
                int cluster_index = cluster_map[max_head_index];
                cluster_map[i] = cluster_index;
                ArrayList past_belongs = (ArrayList)clusters.get(new Integer(cluster_index));
                past_belongs.add(new Integer(i));
                clusters.put(new Integer(cluster_index),past_belongs);
            }
        }
        
        return new ClusteringResult(cluster_map,clusters);
    }
    
//  i�s��j�s�̋��������߂�
    public double calculateDistance(int arg_i,int arg_j,double base_user_bookmark[]){
        if(distance_cache.containsKey(new Integer(arg_i*LARGE_NUMBER_FOR_CACHE_INDEX+arg_j))){
            return ((Double)distance_cache.get(new Integer(arg_i*LARGE_NUMBER_FOR_CACHE_INDEX+arg_j))).doubleValue();
        }else if(distance_cache.containsKey(new Integer(arg_j*LARGE_NUMBER_FOR_CACHE_INDEX+arg_i))){
            return ((Double)distance_cache.get(new Integer(arg_j*LARGE_NUMBER_FOR_CACHE_INDEX+arg_i))).doubleValue();
        }else{
            //��v���Ă���������߂�
            int x_len = matrix.getXlength();
            
            int tmp_result = 0;
            for(int x=0;x<x_len;x++){
                double value2 = 0;
                
                Object obj2 = matrix.getValueByIndex(x,arg_j);
                if(obj2!=null){
                    value2 = ((Double)obj2).doubleValue();
                }
                
                tmp_result+= (base_user_bookmark[x] - value2)*(base_user_bookmark[x] - value2);
            }
            
            if(tmp_result==0){
                distance_cache.put(new Integer(arg_i*LARGE_NUMBER_FOR_CACHE_INDEX+arg_j),new Double(0));
                return 0;
            }
            
            distance_cache.put(new Integer(arg_i*LARGE_NUMBER_FOR_CACHE_INDEX+arg_j),new Double(Math.sqrt((double)tmp_result)));
            
            System.out.println(Math.sqrt((double)tmp_result));
            return Math.sqrt((double)tmp_result);
        }
    }
    
//  i�s��j�s�̋��������߂�
    public double calculateEseDistance(int arg_i,int arg_j,double base_user_bookmark[]){
//        if(distance_cache.containsKey(new Integer(arg_i*LARGE_NUMBER_FOR_CACHE_INDEX+arg_j))){
//            return ((Double)distance_cache.get(new Integer(arg_i*LARGE_NUMBER_FOR_CACHE_INDEX+arg_j))).doubleValue();
//        }else if(distance_cache.containsKey(new Integer(arg_j*LARGE_NUMBER_FOR_CACHE_INDEX+arg_i))){
//            return ((Double)distance_cache.get(new Integer(arg_j*LARGE_NUMBER_FOR_CACHE_INDEX+arg_i))).doubleValue();
//        }else{
            //��v���Ă���������߂�
            int x_len = matrix.getXlength();
            
            int tmp_result = 0;
            for(int x=0;x<x_len;x++){
                if(base_user_bookmark[x]!=0){
                    Object obj2 = matrix.getValueByIndex(x,arg_j);
                    if(obj2!=null){
                        tmp_result++;
                    }    
                }
            }
            
//            if((tmp_result)==0){
//                distance_cache.put(new Integer(arg_i*LARGE_NUMBER_FOR_CACHE_INDEX+arg_j),new Double(0));
//                return 0;
//            }
//            
//            distance_cache.put(new Integer(arg_i*LARGE_NUMBER_FOR_CACHE_INDEX+arg_j),new Double((double)tmp_result));
            
            if(tmp_result!=0){
                System.out.println(tmp_result);    
            }
            return (double)tmp_result;
//        }
    }
    
    //�w�肵�����[�U�̎����k�l�̃R�~���j�e�B�W����\������ArrayList��Ԃ�
    //�Ԃ�l��ArrayList���܂ނ̂̓m�[�h��(String)
    public ArrayList findCommunityAroundAUser(String user_name,int k){
        int T = 0;
        
        
        //�W��C���̃m�[�h��U���ɂ����̃����N�������Ă��邩�̔z��
        int adjacency_in_u_count_of_c_arr[] = new int[k+1];
        
        //�W���̓m�[�h�̖��O��ێ�
        ArrayList B_set = new ArrayList();
        ArrayList C_set = new ArrayList();
        ArrayList U_set = new ArrayList();
        
        //�w�胆�[�U�������m�[�h�Ƃ��Ēǉ�
        C_set.add(user_name);
        B_set.add(user_name);
        
        //�����m�[�h�̗אڃm�[�h���W��U�ɒǉ�
        int user_len = matrix.getXlength();
        int target_user_index = matrix.getXIndex(user_name);
        int init_adjacency_node_count = 0;
        for(int i=0;i<user_len;i++){
            Object obj = matrix.getValueByIndex(target_user_index,i);
            if(obj!=null){
                init_adjacency_node_count++;
                U_set.add(matrix.getXName(i));
                
                T+= ((Double)obj).intValue();
            }
        }
//        double R = 1.0/((double)T);
        double R = 0.0;
        adjacency_in_u_count_of_c_arr[0] = init_adjacency_node_count;
        
        //�w�肳�ꂽ��,R���X�V���A�W��C�����肵�Ă���
        for(int repeat_num = 0;repeat_num < k;repeat_num++){
            System.out.println(repeat_num + "/" + k);
            System.out.println("  C:" + C_set);
            System.out.println("  B:" + B_set);
            System.out.println("  U:" + U_set);
            System.out.println("   R is " + R);
            System.out.println("   T is " + T);
            
            int u_len = U_set.size();
            
            //�ő�l�����߂邽�߂̏����l�Ƃ���0�Ԗڂ̃m�[�h�̒l��ݒ�
            String node_num_achive_max_dr = (String)U_set.get(0);
            double max_dr = calculateDeltaR(node_num_achive_max_dr,B_set,C_set,U_set,T,R);
            double max_dt = dt_return;
            
            //�ő�l�����߂�
            for(int i=1;i<u_len;i++){
                String node_vj = (String)U_set.get(i);
                double dr = calculateDeltaR(node_vj,B_set,C_set,U_set,T,R);
                if(dr > max_dr){
                    max_dr = dr;
                    node_num_achive_max_dr = node_vj;
                    max_dt = dt_return;
                }
            }
            //���������m�[�hvj���W��C�֒ǉ�
            C_set.add(node_num_achive_max_dr);
            U_set.remove(node_num_achive_max_dr);
            
            //�ǉ������m�[�h�̗אڃm�[�h���W��U�֒ǉ�
            int addd_node_index = matrix.getXIndex(node_num_achive_max_dr);
            boolean is_have_adjacency_node_in_u = false;
            int add_candidate_adjacency_in_u = 0; //�W��U���Ɏ����ƂɂȂ�m�[�h��
            for(int i=0;i<user_len;i++){
                Object obj = matrix.getValueByIndex(addd_node_index,i);
                if(obj!=null){
                    String add_candidate_node_name = matrix.getXName(i);
                    int u_set_contain_index = U_set.indexOf(add_candidate_node_name);
                    int c_set_contain_index = C_set.indexOf(add_candidate_node_name);
                    
                    if((u_set_contain_index == -1)&&(c_set_contain_index == -1)){
                        is_have_adjacency_node_in_u = true;
                        U_set.add(matrix.getXName(i));
                        add_candidate_adjacency_in_u++;
                    }else if(c_set_contain_index!=-1){
                        //�אڃm�[�h���W��C�܂�W��B�ɓ����Ă���̂ŁAB�Ɉړ����鎞��
                        //adjacency_in_u_count_of_c_arr���X�V���Ȃ���΂Ȃ�Ȃ�
                        adjacency_in_u_count_of_c_arr[c_set_contain_index]--;
                        if(adjacency_in_u_count_of_c_arr[c_set_contain_index] == 0){
                            //�m�[�h�̈ړ��ɂ���ďW��B���ɂ����Ȃ��Ȃ�m�[�h�𔭌������̂�
                            //B�����菜��
                            B_set.remove(add_candidate_node_name);
                        }
                    }else{ //�������W��U�Ɍ���������Ă�����
                        is_have_adjacency_node_in_u = true;
                        add_candidate_adjacency_in_u++;
                    }
                }
            }
            
            //�W��C�ɒǉ������m�[�h��U���ɗאڃm�[�h�������Ă���ꍇB�ɂ��ǉ�
            if(is_have_adjacency_node_in_u){
                B_set.add(node_num_achive_max_dr);
                adjacency_in_u_count_of_c_arr[repeat_num+1] = add_candidate_adjacency_in_u;
            }
            R += max_dr;
            T += max_dt;
        }
        System.out.println("at last,local modurality is " + R);
        return C_set;
    }
    
//  �w�肵�����[�U�̎����k�l�̃R�~���j�e�B�W����\������ArrayList��Ԃ�
    //�Ԃ�l��ArrayList���܂ނ̂̓m�[�h��(String)
    public ArrayList findCommunityAroundAUserOriginal(String user_name,int k){
        int T = 0;
        
        
        //�W��C���̃m�[�h��U���ɂ����̃����N�������Ă��邩�̔z��
        int adjacency_in_u_count_of_c_arr[] = new int[k+1];
        
        //�W���̓m�[�h�̖��O��ێ�
        ArrayList B_set = new ArrayList();
        ArrayList C_set = new ArrayList();
        ArrayList U_set = new ArrayList();
        
        double C_set_distances_from_start[] = new double[k+1];
        
        //�w�胆�[�U�������m�[�h�Ƃ��Ēǉ�
        C_set.add(user_name);
        C_set_distances_from_start[0] = 2.0;
        B_set.add(user_name);
        
        
        //�����m�[�h�̗אڃm�[�h���W��U�ɒǉ�
        int user_len = matrix.getXlength();
        int target_user_index = matrix.getXIndex(user_name);
        int init_adjacency_node_count = 0;
        for(int i=0;i<user_len;i++){
            Object obj = matrix.getValueByIndex(target_user_index,i);
            if(obj!=null){
                init_adjacency_node_count++;
                U_set.add(matrix.getXName(i));
                T+= ((Double)obj).intValue();
            }
        }
//        double R = 1.0/((double)T);
        double R = 0.0;
        adjacency_in_u_count_of_c_arr[0] = init_adjacency_node_count;
        
        //�w�肳�ꂽ��,R���X�V���A�W��C�����肵�Ă���
        for(int repeat_num = 0;repeat_num < k;repeat_num++){
            System.out.println(repeat_num + "/" + k);
            System.out.println("  C:" + C_set);
            System.out.println("  B:" + B_set);
            System.out.println("  U:" + U_set);
            
            int u_len = U_set.size();
            
            //�ő�l�����߂邽�߂̏����l�Ƃ���0�Ԗڂ̃m�[�h�̒l��ݒ�
            String node_num_achive_max_dr = (String)U_set.get(0);
            double max_dx = cal_x_with_normalization(node_num_achive_max_dr,B_set,C_set,U_set);
            int adjacency_index = adjacency_index_return;
            double max_eval_point =  max_dx/(Math.log(C_set_distances_from_start[adjacency_index]));
//            double max_eval_point =  max_dx;
            
            //�ő�l�����߂�
            for(int i=1;i<u_len;i++){
                String node_vj = (String)U_set.get(i);
                double dx = cal_x_with_normalization(node_vj,B_set,C_set,U_set);
                if((dx/(Math.log(C_set_distances_from_start[adjacency_index_return]))) > max_eval_point){
//                if(dx > max_eval_point){    
                    max_eval_point = dx/(Math.log(C_set_distances_from_start[adjacency_index_return]));
                    max_dx = dx;
                    node_num_achive_max_dr = node_vj;
                    adjacency_index = adjacency_index_return;
                }
            }
            //���������m�[�hvj���W��C�֒ǉ�
            C_set.add(node_num_achive_max_dr);
            C_set_distances_from_start[repeat_num+1] = C_set_distances_from_start[adjacency_index] + 1;
            U_set.remove(node_num_achive_max_dr);
            
            //�ǉ������m�[�h�̗אڃm�[�h���W��U�֒ǉ�
            int addd_node_index = matrix.getXIndex(node_num_achive_max_dr);
            boolean is_have_adjacency_node_in_u = false;
            int add_candidate_adjacency_in_u = 0; //�W��U���Ɏ����ƂɂȂ�m�[�h��
            for(int i=0;i<user_len;i++){
                Object obj = matrix.getValueByIndex(addd_node_index,i);
                if(obj!=null){
                    String add_candidate_node_name = matrix.getXName(i);
                    int u_set_contain_index = U_set.indexOf(add_candidate_node_name);
                    int c_set_contain_index = C_set.indexOf(add_candidate_node_name);
                    
                    if((u_set_contain_index == -1)&&(c_set_contain_index == -1)){
                        is_have_adjacency_node_in_u = true;
                        U_set.add(matrix.getXName(i)); 
                        add_candidate_adjacency_in_u++;
                    }else if(c_set_contain_index!=-1){
                        //�אڃm�[�h���W��C�܂�W��B�ɓ����Ă���̂ŁAB�Ɉړ����鎞��
                        //adjacency_in_u_count_of_c_arr���X�V���Ȃ���΂Ȃ�Ȃ�
                        adjacency_in_u_count_of_c_arr[c_set_contain_index]--;
                        if(adjacency_in_u_count_of_c_arr[c_set_contain_index] == 0){
                            //�m�[�h�̈ړ��ɂ���ďW��B���ɂ����Ȃ��Ȃ�m�[�h�𔭌������̂�
                            //B�����菜��
                            B_set.remove(add_candidate_node_name);
                        }
                    }else{ //�������W��U�Ɍ���������Ă�����
                        is_have_adjacency_node_in_u = true;
                        add_candidate_adjacency_in_u++;
                    }
                }
            }
            
            //�W��C�ɒǉ������m�[�h��U���ɗאڃm�[�h�������Ă���ꍇB�ɂ��ǉ�
            if(is_have_adjacency_node_in_u){
                B_set.add(node_num_achive_max_dr);
                adjacency_in_u_count_of_c_arr[repeat_num+1] = add_candidate_adjacency_in_u;
            }
        }
        System.out.println("at last,local modurality is " + R);
        return C_set;
    }
    
    //��r���v�Z
    private double calculateDeltaR(String node_vj, ArrayList B_set, ArrayList C_set, ArrayList U_set,int T,double R) {
        boolean isHasNeighborOutOfC = isHasNeighborOutOfC(node_vj,B_set);
        double x = cal_x(node_vj,B_set,C_set,U_set);
        cal_y_and_z(node_vj,B_set,C_set,U_set,isHasNeighborOutOfC);
        
//        System.out.println((x - R*y_return - z_return*(1 - R)) + "/" + (T - z_return + y_return));
//        dt_return = T - z_return + y_return;
        dt_return = (-1.0*z_return) + y_return;
//        double dr = ((double)(x - R*y_return - z_return*(1 - R))) / ((double)(dt_return));
        double dr = ((double)(x - R*y_return - z_return*(1 - R))) / ((double)(T + dt_return));
//        System.out.println("dr=" + dr);
        return dr;
    }
    
    
    //�W��C�ȊO�ŗאڃm�[�h�������Ă��邩�`�F�b�N
    private boolean isHasNeighborOutOfC(String node_vj, ArrayList B_set) {
        int user_len = matrix.getXlength();
        
        int index_of_vj = matrix.getXIndex(node_vj);
        for(int i=0;i<user_len;i++){
            Object obj = matrix.getValueByIndex(index_of_vj,i);
            if(obj!=null){
                if(!B_set.contains(matrix.getXName(i))){
                    return true;
                }
            }
        }
        return false;
    }

    private int cal_x(String node_vj, ArrayList B_set, ArrayList C_set, ArrayList U_set) {
        int sum = 0;
        int b_len = B_set.size();
        for(int i=0;i<b_len;i++){
            String node_from_b_name = (String)B_set.get(i);
            Object obj = matrix.getValue(node_vj,node_from_b_name);
            if(obj!=null){
                int weight = ((Double)obj).intValue();
                sum+= weight;
            }
        }
//        System.out.println("x=" + sum);
        
        return sum;
    }
    
    private double cal_x_with_normalization(String node_vj, ArrayList B_set, ArrayList C_set, ArrayList U_set) {
        int user_len = matrix.getXlength();
        int all_bookmark_count = 0;
        int vj_index = matrix.getXIndex(node_vj);
        for(int i=0;i<user_len;i++){
            Object obj = matrix.getValueByIndex(vj_index,i);
            if(obj!=null){
                all_bookmark_count+= ((Double)obj).intValue();
            }
        }
        
        int sum = 0;
        int b_len = B_set.size();
        int adjacency_index = -1;
        for(int i=0;i<b_len;i++){
            String node_from_b_name = (String)B_set.get(i);
            Object obj = matrix.getValue(node_vj,node_from_b_name);
            if(obj!=null){
                if(adjacency_index == -1){
                    adjacency_index = C_set.indexOf(node_from_b_name);
                }
                int weight = ((Double)obj).intValue();
                sum+= weight;
            }
        }
        
        adjacency_index_return = adjacency_index;
        return sum/(double)all_bookmark_count;
    }
    
    //�Ԃ�l��y_return,z_return���g���ēn��
    private void cal_y_and_z(String node_vj, ArrayList B_set, ArrayList C_set, ArrayList U_set,boolean isHasNeighborOutOfC) {
        //�Ԃ�l�p�̕ϐ���������
        y_return = 0;
        z_return = 0;
        
        int user_len = matrix.getXlength();
        int vj_index = matrix.getXIndex(node_vj);
        for(int i=0;i<user_len;i++){
            Object obj = matrix.getValueByIndex(vj_index,i);
            if(obj!=null){
                //������������W��B�ɑ����Ă��Ȃ����
                if(!C_set.contains(matrix.getXName(i))){
                    int weight = ((Double)obj).intValue();
                    y_return += weight;
                }else if(isHasNeighborOutOfC){
                    int weight = ((Double)obj).intValue();
                    z_return += weight;
                }
            }
        }
//        System.out.println("y=" + y_return + " z=" + z_return);
    }
}