/*
 * �쐬��: 2006/02/10
 *
 * ���̐������ꂽ�R�����g�̑}�������e���v���[�g��ύX���邽��
 * �E�B���h�E > �ݒ� > Java > �R�[�h���� > �R�[�h�ƃR�����g
 */
package jp.ryo.informationPump.server.debug;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import de.nava.informa.core.*;
import de.nava.informa.exporters.RSS_1_0_Exporter;
import de.nava.informa.impl.basic.ChannelBuilder;

public class CreateRssTest {

    public static void main(String[] args) {
        ChannelBuilder builder = new ChannelBuilder();

        ChannelIF newChannel = builder.createChannel("My Homepage");
        System.out.println(newChannel.getTitle());
        newChannel.setFormat(ChannelFormat.RSS_1_0);
        newChannel.setLanguage("ja");
        try {
            //
            newChannel.setSite(new URL("http://www.xucker.jpn.org"));
            //
            newChannel.setLocation(
                new URL("http://www.xucker.jpn.org/xucker-utf-8.rdf"));
            ItemIF item =
                builder.createItem(
                    newChannel,
                    "title",
                    "description",
                    new URL("http://localhost"));
            item.setDate(new Date());
            newChannel.addItem(item);
            RSS_1_0_Exporter writer =
                new RSS_1_0_Exporter(new File("./rss.xml"));
            writer.write(newChannel);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}