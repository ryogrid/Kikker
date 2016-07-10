package jp.ryo.informationPump.server.web_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer extends Thread {
    public static final String HOST_NAME = "localhost";
    public static final int PORT = 1235;
    
    public void run() {

        try {
            ServerSocket server_socket;
            server_socket = new ServerSocket(WebServer.PORT);
            System.out.println("Informatin Pump Web Server running on port:" + server_socket.getLocalPort());
            
            while(true){
                    Socket socket = server_socket.accept();
                    
//                    System.out.println("New HTTP connection accepted " + socket.getInetAddress() + ":" + socket.getPort());
                    socket.setSoTimeout(1200000);
                    
                    HttpRequestProcessor request = new HttpRequestProcessor(socket);
                    
                    
                    Thread thread = new Thread(request);
                    thread.setPriority(Thread.MAX_PRIORITY);
                    
                    thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
