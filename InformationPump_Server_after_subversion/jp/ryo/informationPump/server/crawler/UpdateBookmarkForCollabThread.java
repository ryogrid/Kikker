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
        //はてブおせっかいの行列のデータを更新する(新しくブクマした人を反映するため)
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
        System.out.println("コラボレィティブフィルタデータ保存 at UpdateBookmarkForCollabThread#run");
//      コラボレィティブフィルタ用の情報を保存
        CollaborativeInformatoinManager.escapeObjectToFile();
    }
}
