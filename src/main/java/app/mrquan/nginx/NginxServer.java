package app.mrquan.nginx;

import app.mrquan.nginx.pojo.RequestDetails;
import app.mrquan.nginx.pojo.RequestLine;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NginxServer {
    private int port;//端口号
    private int serverPort;//服务器的端口号
    private String serverAddress;//服务器ip地址
    private String context;//上下文环境
    private ExecutorService executorService;
    private ServerSocket serverSocket;
    private ConcurrentHashMap<RequestLine, RequestDetails> map;
    private Callback callback;

    public static NginxServer create(int port, String context,String serverAddress,int serverPort,Callback callback){
        return new NginxServer(port, context, serverAddress, serverPort, callback);
    }

    private NginxServer(int port, String context,String serverAddress,int serverPort,Callback callback) {
        this.port = port;
        this.context = context;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.callback = callback;
    }
    public void start(){
//        if (port==serverPort){
//            try {
//                throw new Exception("端口重复");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        new Thread(()->{
            try {
                map = new ConcurrentHashMap<>();
                executorService = Executors.newFixedThreadPool(30);
                serverSocket = new ServerSocket(port);
                while (true){
                    executorService.execute(new NginxExchange(serverSocket.accept(),map,serverPort,serverAddress,callback));//线程池 执行线程
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
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
