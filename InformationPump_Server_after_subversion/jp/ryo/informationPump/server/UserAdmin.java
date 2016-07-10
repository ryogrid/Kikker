package jp.ryo.informationPump.server;

import java.io.Serializable;
import java.util.HashMap;

import jp.ryo.informationPump.server.data.UserProfile;
import jp.ryo.informationPump.server.data.UserSearchResult;

public interface UserAdmin extends Serializable {
    
    public abstract UserProfile getUser(String id);

    public abstract UserProfile[] getAllUser();

    //�Ԃ�l��true���Ɛ����A��������Ȃ��ꍇ�͎��s
    public abstract boolean createNewUser(UserProfile profile);

    public abstract void addReadPage(String user_name, String page_name);

    public abstract void addReadPages(String user_name, String page_names[]);

    public abstract void updateTasete(String user_name, HashMap taste_vector);

    //  ����̃x�N�^�[��n���Ď��Ă���h�L�������g�̔z��Ɨގ��x��������SearchResult�𓾂�
    public abstract UserSearchResult search(HashMap taste_vector);

}