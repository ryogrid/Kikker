/*
 * �쐬��: 2006/02/05
 *
 * ���̐������ꂽ�R�����g�̑}�������e���v���[�g��ύX���邽��
 * �E�B���h�E > �ݒ� > Java > �R�[�h���� > �R�[�h�ƃR�����g
 */
package jp.ryo.informationPump.server.util;

import java.util.Comparator;
import java.util.Map;

import jp.ryo.informationPump.server.data.*;

//�傫�����Ɏw�肷�� Comparator
public class BiasEntryComp implements Comparator {
    
  // �R���X�g���N�^
  public BiasEntryComp() {
    super();
  }
  
  public boolean equals(Object obj) {
    return (super.equals(obj));
  }
  
  // �����ő������Ɏw�肷��
  public int compare(Object obj1, Object obj2) {
      
    double v1;
    double v2;
    if(obj1 instanceof SortBoxForDocumentEntry){
        SortBoxForDocumentEntry entry1 = (SortBoxForDocumentEntry)obj1;
        SortBoxForDocumentEntry entry2 = (SortBoxForDocumentEntry)obj2;
        if(entry1.entry.getDoc_type()==DBUtil.HATEBU_TYPE){
            v1 = getEvalPoint(entry1.simirality.doubleValue(),entry1.entry.getView_users());
            v2 = getEvalPoint(entry2.simirality.doubleValue(),entry2.entry.getView_users());    
        }else if(entry1.entry.getDoc_type()==DBUtil.CEEK_NEWS_TYPE){
            v1 = entry1.simirality.doubleValue();
            v2 = entry2.simirality.doubleValue();
        }else if(entry1.entry.getDoc_type()==DBUtil.YOUTUBE_TYPE){
            v1 = getEvalPoint(entry1.simirality.doubleValue(),entry1.entry.getView_users());
            v2 = getEvalPoint(entry2.simirality.doubleValue(),entry2.entry.getView_users());    
        }else{
            throw new RuntimeException();
        }
    }else{
        throw new RuntimeException();
    }
    
    if(v1 == v2){
        return 0;
    }else if(v1 > v2){
        return -1;
    }else{
        return 1;
    }
  }
  
  private double getEvalPoint(double simirality,int view_count){
      return simirality*Math.log(view_count+1);
  }
}
