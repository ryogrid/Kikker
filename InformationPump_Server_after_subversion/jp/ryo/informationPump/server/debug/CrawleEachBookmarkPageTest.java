package jp.ryo.informationPump.server.debug;

import java.io.IOException;
import java.net.MalformedURLException;

import org.htmlparser.util.ParserException;

import jp.ryo.informationPump.server.helper.HatebuHelper;

public class CrawleEachBookmarkPageTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            HatebuHelper.crawl(2);
        } catch (ParserException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
