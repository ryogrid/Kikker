/*
 * �쐬��: 2006/02/07
 *
 * ���̐������ꂽ�R�����g�̑}�������e���v���[�g��ύX���邽��
 * �E�B���h�E > �ݒ� > Java > �R�[�h���� > �R�[�h�ƃR�����g
 */
package jp.ryo.informationPump.server.exchange_server.enum;

public class OperationCode {
    //  �I�y���[�V�����R�[�h
    public static final int CREATE_NEW_USER=1;
    public static final int ADD_A_PAGE=2;
    public static final int ADD_SOME_PAGE = 3;
    public static final int UPDATE_USER_TASTE = 4;
    
    public static String getOperationName(int code){
        if(code == CREATE_NEW_USER){
            return "CREATE_NEW_USER";
        }else if(code == CREATE_NEW_USER){
            return "CREATE_NEW_USER";
        }else if(code == ADD_SOME_PAGE){
            return "ADD_SOME_PAGE";
        }else if(code == UPDATE_USER_TASTE){
            return "UPDATE_USER_TASTE";
        }else{
            return "Ellegal Operation code!!";
        }
    }
}