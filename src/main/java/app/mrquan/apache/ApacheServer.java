package app.mrquan.apache;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApacheServer {
    private int port;//端口号
    private String context;//上下文环境
    private HttpHandler handler;
    private String htmlPath;//html 文件路径
    private ExecutorService executorService;
    private ServerSocket serverSocket;

    /**
     * 创建服务器
     * @param port 端口号
     * @param context 环境上下文
     * @param htmlPath 资源文件路径(html等)
     * @param handler 回调函数
     * @return ApacheServer对象
     */
    public static ApacheServer create(int port,String context,String htmlPath,HttpHandler handler){
        return new ApacheServer(port,context,htmlPath,handler);
    }

    private ApacheServer(int port,String context,String htmlPath,HttpHandler handler) {
        this.port = port;
        this.context = context;
        this.handler = handler;
        this.htmlPath = htmlPath;
    }

    /**
     * 启动apache服务器
     */
    public void start(){
        new Thread(()->{
            try {
                executorService = Executors.newFixedThreadPool(30);
                serverSocket = new ServerSocket(port);
                while (true){
                    executorService.execute(new HttpExchange(serverSocket.accept(),handler,htmlPath));//线程池 执行线程
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
