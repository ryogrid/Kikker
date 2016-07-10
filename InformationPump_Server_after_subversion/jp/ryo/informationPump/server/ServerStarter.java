package jp.ryo.informationPump.server;

import java.io.*;

public class ServerStarter {

    public static void main(String[] args) {
        while(true){
            ProcessBuilder pb = new ProcessBuilder(new String[]{"java","-server","-Xmx600m","-Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.NoOpLog","-classpath",".:./new_rpc_lib/xmlrpc-client-3.1.jar:./new_rpc_lib/xmlrpc-common-3.1.jar:./new_rpc_lib/xmlrpc-server-3.1.jar:./apach_xml_rpc.jar:./new_rpc_lib/ws-commons-util-1.0.2.jar:./commons-beanutils.jar:./commons-codec-1.3.jar:./commons-collections.jar:./commons-httpclient-3.0.jar:commons-lang.jar:./commons-logging.jar:./dom4j.jar:./ehcache.jar:./filterbuilder.jar:./hibernate2.jar:./hsqldb.jar:./htmllexer.jar:./htmlparser.jar:./informa.jar:./jaxb-api.jar:./jaxb-impl.jar:./jaxb-libs.jar:./jaxb-xjc.jar:./jdom.jar:./jta.jar:./junit.jar:./lucene.jar:./odmg.jar:./sax2.jar:./sen.jar:./thumbelina.jar:../DBMappingForKikker/src/:../DBMappingForKikker/bin/:../DBMappingForKikker/src_gen/:../DBMappingForKikker/commons-dbcp-1.2.1.jar:../DBMappingForKikker/commons-pool-1.3.jar:../DBMappingForKikker/mysql-connector-java-5.0.0-beta-bin.jar","jp.ryo.informationPump.server.ViewServer"});
            try {
                Process process = pb.start();
                InputStream istream = process.getInputStream();
                int c;
                while((c = istream.read()) != -1){
                    System.out.write(c);
                }
                System.out.flush();
                istream.close();

                try {
                    process.waitFor();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}