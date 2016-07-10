package jp.ryo.informationPump.server;

import java.io.Serializable;
import java.util.HashMap;

import jp.ryo.informationPump.server.data.*;

public interface WebMetadataAdmin extends Serializable {

    public abstract void addNewPage(WebData data);

    //�^�����L�[���[�h�ɑ���,hour���ԑO���猻�݂̊Ԃɒǉ����ꂽ���̂�Ԃ�
    public abstract StoreBoxForWebData[] getTagetKeywordBelongs(String keyword,
            int hour);

    //�^�����L�[���[�h�ɑ�����h�L�������g�𓾂�
    public abstract StoreBoxForWebData[] getTagetKeywordBelongs(String keyword);

    public abstract StoreBoxForWebData getPage(String name);

    //  ����̃x�N�^�[��n���Ď��Ă���h�L�������g�̔z��Ɨގ��x��������SearchResult�𓾂�
    public abstract SearchResult search(HashMap taste_vector);

}