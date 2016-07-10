/*
 * �쐬��: 2006/02/05
 *
 * ���̐������ꂽ�R�����g�̑}�������e���v���[�g��ύX���邽��
 * �E�B���h�E > �ݒ� > Java > �R�[�h���� > �R�[�h�ƃR�����g
 */
package jp.ryo.informationPump.server.debug.profile;

import java.util.Comparator;
import java.util.Map;

import jp.ryo.informationPump.server.data.*;

//�傫�����Ɏw�肷�� Comparator
public class ProfileDataComp implements Comparator {
  
  // �R���X�g���N�^
  public ProfileDataComp() {
    super();
  }
  
  public boolean equals(Object obj) {
    return (super.equals(obj));
  }
  
  // �����ő������Ɏw�肷��
  public int compare(Object obj1, Object obj2) {
      
    long v1;
    long v2;
    v1 = ((ProfileTabData)obj1).cpu_time.longValue();
    v2 = ((ProfileTabData)obj2).cpu_time.longValue();
    
    if(v1 == v2){
        return 0;
    }else if(v1 > v2){
        return -1;
    }else{
        return 1;
    }
  }
}
