package jp.ryo.informationPump.server.crawler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import jp.ryo.informationPump.server.data.DocumentSearchResult;
import jp.ryo.informationPump.server.data.StoreBoxForDocumentEntry;

public interface CrawledDataManager extends Serializable{
    
    public abstract void margeClawledData(HashMap data,int doc_type,boolean isAsAnalyzede);

    //  ����̃x�N�^�[��n���Ď��Ă���h�L�������g�̔z��Ɨގ��x��������SearchResult�𓾂�
    public abstract DocumentSearchResult search(HashMap taste_vector,int doc_type);
    
    //����̃x�N�^�[��n���Ď��Ă���h�L�������g�̔z��Ɨގ��x��������SearchResult�𓾂�
    //DB�ɃA�N�Z�X����ʂ����炷���߂�,���O�ɓ��Ă������G���g����n���B�J�e�S���w��\�B
    public abstract DocumentSearchResult searchNews(HashMap taste_vector,String category,StoreBoxForDocumentEntry entries[]);
    
    //�^�����L�[���[�h�ɑ���,hour���ԑO���猻�݂̊Ԃɒǉ����ꂽ���̂�Ԃ�
    public abstract StoreBoxForDocumentEntry[] getTagetKeywordBelongs(
            String keyword, int hour,int doc_type);

    //�^�����L�[���[�h�ɑ�����h�L�������g�𓾂�
    public abstract StoreBoxForDocumentEntry[] getTagetKeywordBelongs(
            String keyword,int doc_type);

    public abstract void setAnalyzedResults(String target_url,
            HashMap newVecMap, ArrayList sortedKeywords,int doc_type);

    //call�������_����莞�Ԉȏ�O�̃G���g�����폜����
    public abstract void removeOldEntries(int doc_type);
    
    //�S�G���g���̃u�b�N�}�[�N�����X�V����
    public abstract void updateBookmarkCounts(int doc_type);
    
    //�Ώۂ̃A�h���X�̃G���g���𓾂�
    public abstract StoreBoxForDocumentEntry getEntryWithAddress(String address,int doc_type);
    
    //�S�ẴG���g���𓾂�
    public abstract StoreBoxForDocumentEntry[] getAllEntry(int doc_type);
}