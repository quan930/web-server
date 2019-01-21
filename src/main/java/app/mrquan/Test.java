package app.mrquan;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;

public class Test {
    public static void main(String[] args) throws IOException {
//        File file = new File("www/html");
//        System.out.println(file.getAbsolutePath());
////        HttpServer.create()
//        ServerSocket serverSocket = new ServerSocket(9999);
//        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1",9999);
//        serverSocket.bind(inetSocketAddress);
//        Socket socket = serverSocket.accept();
//        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        while (true){
//            String s = reader.readLine();
//            if (s==null){
//                break;
//            }
//            System.out.println(s);
//        }
//        reader.close();

        String m=  "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36";
        String n = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8";
        System.out.println(m.split(",[^ ]")[0]);
//        HttpServer.create()
//        com.sun.net.httpserver.HttpServer
//        new HttpExchange().getRequestHeaders()
//        HttpHandler
    }
}
