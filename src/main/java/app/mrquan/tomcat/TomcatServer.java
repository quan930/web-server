package app.mrquan.tomcat;

import app.mrquan.tomcat.util.XMLParsing;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TomcatServer {
    private int port;//端口号
    private String context;//上下文环境
    private ExecutorService executorService;
    private ServerSocket serverSocket;
    private Map<String,String> servletMappingMap;//映射关系
    private ConcurrentHashMap<String,Servlet> servletMap;//servlet容器


    /**
     * 创建服务器
     * @param port 端口号
     * @param context 环境上下文
     * @return TomcatServer对象
     */
    public static TomcatServer create(int port,String context){
        return new TomcatServer(port,context);
    }

    private TomcatServer(int port, String context) {
        this.port = port;
        this.context = context;
    }
    /**
     * 启动apache服务器
     */
    public void start(){
        new Thread(()->{
            try {
                servletMappingMap = XMLParsing.parsingXML("web/WEB-INF/web.xml");//解析web.xml
                servletMap = new ConcurrentHashMap<>();
                executorService = Executors.newFixedThreadPool(30);//线程池
                serverSocket = new ServerSocket(port);
                while (true){
                    executorService.execute(new Service(serverSocket.accept(),context,servletMappingMap,servletMap));//线程池 执行线程
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void shutdown(){
        /**
         * 执行Servlet.destroy()方法
         */
        executorService.shutdown();
        while (!executorService.isTerminated()){ }
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
