/*
 * �쐬��: 2006/02/22
 *
 * ���̐������ꂽ�R�����g�̑}�������e���v���[�g��ύX���邽��
 * �E�B���h�E > �ݒ� > Java > �R�[�h���� > �R�[�h�ƃR�����g
 */
package jp.ryo.informationPump.server.debug;

import jp.ryo.informationPump.server.util.Util;

public class EncodeDecodeTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        String encoded = Util.getQueryString("ab�ق��_��LLY)(=");
        System.out.println(Util.decodeQueryStringUTF8(encoded));
    }

}
