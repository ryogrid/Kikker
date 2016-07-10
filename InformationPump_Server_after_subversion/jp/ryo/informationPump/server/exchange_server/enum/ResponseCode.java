/*
 * �쐬��: 2006/02/07
 *
 * ���̐������ꂽ�R�����g�̑}�������e���v���[�g��ύX���邽��
 * �E�B���h�E > �ݒ� > Java > �R�[�h���� > �R�[�h�ƃR�����g
 */
package jp.ryo.informationPump.server.exchange_server.enum;

public class ResponseCode {
    public static final int SUCCESS = 1;
    public static final int ERROR = 0;
    
    public static String getResponceName(int code){
        if(code == SUCCESS){
            return "SUCCESS";
        }else if(code == ERROR){
            return "ERROR";
        }else{
            return "Ellegal Response code!!";
        }
    }
}
