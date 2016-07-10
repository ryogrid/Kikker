package jp.ryo.informationPump.server.debug;

import java.io.IOException;

import net.java.sen.StringTagger;
import jp.ryo.informationPump.server.crawler.analyze.InDependentDocumentAnalyzer;

public class IndependentAnalyzeTest {

    public static void main(String[] args) {
        System.setProperty("sen.home","./sen-1.2.1");
        InDependentDocumentAnalyzer analyzer = new InDependentDocumentAnalyzer();
        analyzer.analyzeByURL("http://d.hatena.ne.jp/kanbayashi/");
    }
}
