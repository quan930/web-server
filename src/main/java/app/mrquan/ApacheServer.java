package app.mrquan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApacheServer {
    private int port;//端口号
    private String context;//上下文环境
    private HttpHandler handler;
    private String htmlPath;//html 文件路径
    private ExecutorService executorService;
    private ServerSocket serverSocket;

    public ApacheServer(int port,String context,String htmlPath,HttpHandler handler) {
        this.port = port;
        this.context = context;
        this.handler = handler;
        this.htmlPath = htmlPath;
    }

    public void start(){
        try {
            executorService = Executors.newFixedThreadPool(30);
            serverSocket = new ServerSocket(port);
            while (true){
                executorService.execute(new HttpExchange(serverSocket.accept(),handler,htmlPath));//线程池 执行线程
            }
        }catch (SocketException ignored){
//            System.out.println("关闭登陆服务器");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown(){
        executorService.shutdown();
        while (!executorService.isTerminated()){ }
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
