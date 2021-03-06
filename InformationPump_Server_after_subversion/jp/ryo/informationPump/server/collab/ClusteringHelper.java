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
    
    //グラフでのクラスタリングの場合用
      //返り値を渡すための変数
      double y_return = 0;
      double z_return = 0;
      
      double dt_return = 0;
      
      //オリジナル版でvjの隣接ノードのインデックスを返すための変数
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
        
//      どの番号のユーザがどのクラスタに所属するかの番号を保持
        int cluster_map[] = new int[user_len];
//      key=クラスタ番号 Integer,Value=所属するユーザの番号(Integer)を保持するArrayList
        HashMap clusters = new HashMap(); 
        
        //一個目のクラスタを設定
        cluster_heads.add(new Integer(0));
        cluster_map[0] = cluster_count;
        ArrayList belongs = new ArrayList();
        belongs.add(new Integer(0));
        clusters.put(new Integer(cluster_count++),belongs);

        int x_length = matrix.getXlength();
        for(int i=1;i<user_len;i++){
            System.out.println("now moving user number:" + i + "/" + user_len + ",cluster count=" + cluster_count);
            int cluster_len = cluster_heads.size();
            
            //値の参照を早くするために、基準となるベクトルの値を配列に値を取り出しておく
            double base_vector[] = new double[x_length];
            for(int j=0;j<x_length;j++){
                Object obj1 = matrix.getValueByIndex(j,i);
                if(obj1!=null){
                    base_vector[j] = ((Double)obj1).doubleValue();    
                }else{
                    base_vector[j] = 0;
                }
            }
            
            int max_head_index = -1; //現状で一番近いクラスタの代表点のIndex
            double max_distance = 0;  //現状で一番近いクラスタの代表点との距離
            for(int j=0;j<cluster_len;j++){
                int cluster_head_index = ((Integer)cluster_heads.get(j)).intValue();
                double distance = calculateEseDistance(i,cluster_head_index,base_vector);
                
                //新たな直近のクラスタを発見したら
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
                //一個も閾値より近いクラスタが見つからなかった場合
                cluster_heads.add(new Integer(i));
                cluster_map[i] = cluster_count;
                ArrayList new_belongs = new ArrayList();
                new_belongs.add(new Integer(i));
                clusters.put(new Integer(cluster_count++),new_belongs);
            }else{
                //クラスタが見つかった場合
                int cluster_index = cluster_map[max_head_index];
                cluster_map[i] = cluster_index;
                ArrayList past_belongs = (ArrayList)clusters.get(new Integer(cluster_index));
                past_belongs.add(new Integer(i));
                clusters.put(new Integer(cluster_index),past_belongs);
            }
        }
        
        return new ClusteringResult(cluster_map,clusters);
    }
    
//  i行とj行の距離を求める
    public double calculateDistance(int arg_i,int arg_j,double base_user_bookmark[]){
        if(distance_cache.containsKey(new Integer(arg_i*LARGE_NUMBER_FOR_CACHE_INDEX+arg_j))){
            return ((Double)distance_cache.get(new Integer(arg_i*LARGE_NUMBER_FOR_CACHE_INDEX+arg_j))).doubleValue();
        }else if(distance_cache.containsKey(new Integer(arg_j*LARGE_NUMBER_FOR_CACHE_INDEX+arg_i))){
            return ((Double)distance_cache.get(new Integer(arg_j*LARGE_NUMBER_FOR_CACHE_INDEX+arg_i))).doubleValue();
        }else{
            //一致している個数を求める
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
    
//  i行とj行の距離を求める
    public double calculateEseDistance(int arg_i,int arg_j,double base_user_bookmark[]){
//        if(distance_cache.containsKey(new Integer(arg_i*LARGE_NUMBER_FOR_CACHE_INDEX+arg_j))){
//            return ((Double)distance_cache.get(new Integer(arg_i*LARGE_NUMBER_FOR_CACHE_INDEX+arg_j))).doubleValue();
//        }else if(distance_cache.containsKey(new Integer(arg_j*LARGE_NUMBER_FOR_CACHE_INDEX+arg_i))){
//            return ((Double)distance_cache.get(new Integer(arg_j*LARGE_NUMBER_FOR_CACHE_INDEX+arg_i))).doubleValue();
//        }else{
            //一致している個数を求める
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
    
    //指定したユーザの周りのk人のコミュニティ集合を表現するArrayListを返す
    //返り値のArrayListが含むのはノード名(String)
    public ArrayList findCommunityAroundAUser(String user_name,int k){
        int T = 0;
        
        
        //集合C内のノードがU内にいくつのリンクを持っているかの配列
        int adjacency_in_u_count_of_c_arr[] = new int[k+1];
        
        //集合はノードの名前を保持
        ArrayList B_set = new ArrayList();
        ArrayList C_set = new ArrayList();
        ArrayList U_set = new ArrayList();
        
        //指定ユーザを初期ノードとして追加
        C_set.add(user_name);
        B_set.add(user_name);
        
        //初期ノードの隣接ノードを集合Uに追加
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
        
        //指定された回数,Rを更新しつつ、集合Cを決定していく
        for(int repeat_num = 0;repeat_num < k;repeat_num++){
            System.out.println(repeat_num + "/" + k);
            System.out.println("  C:" + C_set);
            System.out.println("  B:" + B_set);
            System.out.println("  U:" + U_set);
            System.out.println("   R is " + R);
            System.out.println("   T is " + T);
            
            int u_len = U_set.size();
            
            //最大値を求めるための初期値として0番目のノードの値を設定
            String node_num_achive_max_dr = (String)U_set.get(0);
            double max_dr = calculateDeltaR(node_num_achive_max_dr,B_set,C_set,U_set,T,R);
            double max_dt = dt_return;
            
            //最大値を求める
            for(int i=1;i<u_len;i++){
                String node_vj = (String)U_set.get(i);
                double dr = calculateDeltaR(node_vj,B_set,C_set,U_set,T,R);
                if(dr > max_dr){
                    max_dr = dr;
                    node_num_achive_max_dr = node_vj;
                    max_dt = dt_return;
                }
            }
            //発見したノードvjを集合Cへ追加
            C_set.add(node_num_achive_max_dr);
            U_set.remove(node_num_achive_max_dr);
            
            //追加したノードの隣接ノードを集合Uへ追加
            int addd_node_index = matrix.getXIndex(node_num_achive_max_dr);
            boolean is_have_adjacency_node_in_u = false;
            int add_candidate_adjacency_in_u = 0; //集合U内に持つことになるノード数
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
                        //隣接ノードが集合Cつまり集合Bに入っているので、Bに移動する時に
                        //adjacency_in_u_count_of_c_arrを更新しなければならない
                        adjacency_in_u_count_of_c_arr[c_set_contain_index]--;
                        if(adjacency_in_u_count_of_c_arr[c_set_contain_index] == 0){
                            //ノードの移動によって集合B内にいられなくなるノードを発見したので
                            //Bから取り除く
                            B_set.remove(add_candidate_node_name);
                        }
                    }else{ //他方が集合Uに元から入っていた時
                        is_have_adjacency_node_in_u = true;
                        add_candidate_adjacency_in_u++;
                    }
                }
            }
            
            //集合Cに追加したノードがU内に隣接ノードを持っている場合Bにも追加
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
    
//  指定したユーザの周りのk人のコミュニティ集合を表現するArrayListを返す
    //返り値のArrayListが含むのはノード名(String)
    public ArrayList findCommunityAroundAUserOriginal(String user_name,int k){
        int T = 0;
        
        
        //集合C内のノードがU内にいくつのリンクを持っているかの配列
        int adjacency_in_u_count_of_c_arr[] = new int[k+1];
        
        //集合はノードの名前を保持
        ArrayList B_set = new ArrayList();
        ArrayList C_set = new ArrayList();
        ArrayList U_set = new ArrayList();
        
        double C_set_distances_from_start[] = new double[k+1];
        
        //指定ユーザを初期ノードとして追加
        C_set.add(user_name);
        C_set_distances_from_start[0] = 2.0;
        B_set.add(user_name);
        
        
        //初期ノードの隣接ノードを集合Uに追加
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
        
        //指定された回数,Rを更新しつつ、集合Cを決定していく
        for(int repeat_num = 0;repeat_num < k;repeat_num++){
            System.out.println(repeat_num + "/" + k);
            System.out.println("  C:" + C_set);
            System.out.println("  B:" + B_set);
            System.out.println("  U:" + U_set);
            
            int u_len = U_set.size();
            
            //最大値を求めるための初期値として0番目のノードの値を設定
            String node_num_achive_max_dr = (String)U_set.get(0);
            double max_dx = cal_x_with_normalization(node_num_achive_max_dr,B_set,C_set,U_set);
            int adjacency_index = adjacency_index_return;
            double max_eval_point =  max_dx/(Math.log(C_set_distances_from_start[adjacency_index]));
//            double max_eval_point =  max_dx;
            
            //最大値を求める
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
            //発見したノードvjを集合Cへ追加
            C_set.add(node_num_achive_max_dr);
            C_set_distances_from_start[repeat_num+1] = C_set_distances_from_start[adjacency_index] + 1;
            U_set.remove(node_num_achive_max_dr);
            
            //追加したノードの隣接ノードを集合Uへ追加
            int addd_node_index = matrix.getXIndex(node_num_achive_max_dr);
            boolean is_have_adjacency_node_in_u = false;
            int add_candidate_adjacency_in_u = 0; //集合U内に持つことになるノード数
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
                        //隣接ノードが集合Cつまり集合Bに入っているので、Bに移動する時に
                        //adjacency_in_u_count_of_c_arrを更新しなければならない
                        adjacency_in_u_count_of_c_arr[c_set_contain_index]--;
                        if(adjacency_in_u_count_of_c_arr[c_set_contain_index] == 0){
                            //ノードの移動によって集合B内にいられなくなるノードを発見したので
                            //Bから取り除く
                            B_set.remove(add_candidate_node_name);
                        }
                    }else{ //他方が集合Uに元から入っていた時
                        is_have_adjacency_node_in_u = true;
                        add_candidate_adjacency_in_u++;
                    }
                }
            }
            
            //集合Cに追加したノードがU内に隣接ノードを持っている場合Bにも追加
            if(is_have_adjacency_node_in_u){
                B_set.add(node_num_achive_max_dr);
                adjacency_in_u_count_of_c_arr[repeat_num+1] = add_candidate_adjacency_in_u;
            }
        }
        System.out.println("at last,local modurality is " + R);
        return C_set;
    }
    
    //�决を計算
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
    
    
    //集合C以外で隣接ノードを持っているかチェック
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
    
    //返り値はy_return,z_returnを使って渡す
    private void cal_y_and_z(String node_vj, ArrayList B_set, ArrayList C_set, ArrayList U_set,boolean isHasNeighborOutOfC) {
        //返り値用の変数を初期化
        y_return = 0;
        z_return = 0;
        
        int user_len = matrix.getXlength();
        int vj_index = matrix.getXIndex(node_vj);
        for(int i=0;i<user_len;i++){
            Object obj = matrix.getValueByIndex(vj_index,i);
            if(obj!=null){
                //他方が元から集合Bに属していなければ
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
