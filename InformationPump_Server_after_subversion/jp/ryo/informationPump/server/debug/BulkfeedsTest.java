package jp.ryo.informationPump.server.debug;

import jp.ryo.informationPump.server.helper.BulkfeedsHelper;

public class BulkfeedsTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        String results[] = BulkfeedsHelper.getImportantWords("�����͐_�ьN�̓P�[�L�ƃ`���R���[�g��H�ׂ܂���");
        for(int i=0;i< results.length;i++){
            System.out.println(results[i]);    
        }
    }

}
