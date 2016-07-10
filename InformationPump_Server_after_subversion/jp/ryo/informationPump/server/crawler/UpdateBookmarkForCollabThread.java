package jp.ryo.informationPump.server.crawler;

import java.io.IOException;
import java.net.MalformedURLException;

import org.htmlparser.util.ParserException;

import jp.ryo.informationPump.server.collab.CollaborativeInformatoinManager;

public class UpdateBookmarkForCollabThread extends Thread {
    
    public UpdateBookmarkForCollabThread(String name) {
        super(name);
        
    }

    public void run(){
        //�͂ău�����������̍s��̃f�[�^���X�V����(�V�����u�N�}�����l�𔽉f���邽��)
        CollaborativeInformatoinManager collab_manager = CollaborativeInformatoinManager.getInstance();
        try {
            collab_manager.updateBookmarkCounts();
        } catch (ParserException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("�R���{���B�e�B�u�t�B���^�f�[�^�ۑ� at UpdateBookmarkForCollabThread#run");
//      �R���{���B�e�B�u�t�B���^�p�̏���ۑ�
        CollaborativeInformatoinManager.escapeObjectToFile();
    }
}