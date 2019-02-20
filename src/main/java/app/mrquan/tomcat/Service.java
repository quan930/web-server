package app.mrquan.tomcat;


import app.mrquan.tomcat.config.DisplayPage;
import app.mrquan.tomcat.http.*;
import app.mrquan.tomcat.http.pojo.Headers;
import app.mrquan.tomcat.http.pojo.RequestLine;
import app.mrquan.tomcat.http.pojo.ResponseLine;
import app.mrquan.tomcat.util.FileIO;
import app.mrquan.tomcat.util.NewInstanceRequestResponse;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Service implements Runnable {
    private Socket socket;
    private String context;//上下文环境
    private Map<String,String> servletMappingMap;//映射关系
    private ConcurrentHashMap<String,Servlet> servletMap;//servlet容器
    private String staticResourcePath;//静态资源
    private InputStream in;
    private OutputStream out;

    public Service(Socket socket, String context, Map<String, String> servletMappingMap, ConcurrentHashMap<String, Servlet> servletMap,String staticResourcePath) {
        this.socket = socket;
        this.context = context;
        this.servletMappingMap = servletMappingMap;
        this.servletMap = servletMap;
        this.staticResourcePath = staticResourcePath;
    }

    @Override
    public void run() {
        try {
            in = socket.getInputStream();
            out = new BufferedOutputStream(socket.getOutputStream());
            RequestLine requestLine = requestLine();//读取请求行
//            System.out.println("..................."+requestLine);
            String className = servletMappingMap.get(requestLine.getClassName());
            if (className==null||requestLine.getRequestURL().indexOf(context)!=0){//不含有servlet，或者不是以context开头
                /**
                 * 404
                 */
                ResponseLine responseLine = null;
                Headers responseHeaders = null;
                byte[] responseEntityBody = null;
                File file = new File(staticResourcePath+requestLine.getClassName());
                if (file.isDirectory()){
                    file = new File(staticResourcePath+requestLine.getClassName()+"/index.html");
                }
                if (file.exists()){//判断请求是否存在
                    responseLine = new ResponseLine("HTTP/1.0","200","OK");
                    responseHeaders = new Headers();
                    responseHeaders.add("Content-Length",String.valueOf(file.length()));
                    responseEntityBody = FileIO.getBytes(file);
                }else {//404
//                    System.out.println("文件不存在"+file);
//                    System.out.println(requestLine);
                    responseLine = new ResponseLine("HTTP/1.0","404","Not Found");
                    responseHeaders = new Headers();
                    responseHeaders.add("Content-Length",String.valueOf(DisplayPage.Code404.getBytes().length));
                    responseEntityBody = DisplayPage.Code404.getBytes();
                }
                out.write(responseLine.toLine().getBytes());
                out.write("\r\n".getBytes());
                out.write(responseHeaders.format().getBytes());
                out.write("\r\n".getBytes());
                out.write(responseEntityBody);
                out.flush();
            }else {//servlet逻辑
//                System.out.println(requestLine);
                if (servletMap.get(className)==null){//容器中没有指定servlet
                    Servlet servlet = Class.forName(className).asSubclass(Servlet.class).newInstance();
                    servlet.init();
                    servletMap.put(className,servlet);
                }
                //请求首部
                Headers requestHeaders = new Headers();
                //请求报文
                byte[] requestEntityBody;
                //读取报文首部和报文实体
                requestEntityBody = request(requestHeaders);
                //创建 请求对象
                HttpServletRequest httpServletRequest = NewInstanceRequestResponse.createHttpServletRequest(context,requestLine,requestHeaders,requestEntityBody);
                //响应行
                ResponseLine responseLine = new ResponseLine();
                //响应首部
                Headers responseHeaders = new Headers();
                //内存流
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                //创建 响应对象
                HttpServletResponse httpServletResponse = NewInstanceRequestResponse.createHttpServletResponse(responseLine,responseHeaders,outputStream);
                //执行service()方法
                servletMap.get(className).service(httpServletRequest,httpServletResponse);
                //设置响应
                response(responseLine,responseHeaders,httpServletRequest.getInputStream(),outputStream);
            }
            //释放资源
            socket.shutdownOutput();
            socket.shutdownInput();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    /**
     * 读取请求行
     * @return 请求行
     * @throws IOException 异常
     */
    private RequestLine requestLine() throws IOException {
        StringBuilder requestLineBuffer = new StringBuilder();
        int i;
        while ((i = in.read())!=13){//读取请求行
            requestLineBuffer.append((char)i);
        }
        in.read();
        return new RequestLine(requestLineBuffer.toString(),context);
    }

    /**
     * 读取请求头和请求报文
     * @throws IOException io异常
     */
    private byte[] request(Headers requestHeaders) throws IOException{
        byte[] requestEntityBody;
        /**
         * 请求头
         */
        StringBuilder requestHeader = new StringBuilder();
        int m;
        byte [] bytes = new byte[3];
        while ((m = in.read())!=-1){//请求头
            if(m==13){
                requestHeader.append((char)m);
                in.read(bytes);
                if(new String(bytes).equals("\n\r\n")){
                    requestHeader.append('\n');
                    break;
                }else{
                    requestHeader.append(new String(bytes));
                }
            }else{
                requestHeader.append((char)m);
            }
        }
        for (String s : requestHeader.toString().split("\r\n")) {
            requestHeaders.set(s.substring(0,s.indexOf(": ")),s.substring(s.indexOf(": ")+2));
        }
        /*
         * 请求体
         */
        if (requestHeaders.get("Content-Length")!=null){
            byte requestBody[] = new byte[Integer.parseInt(requestHeaders.get("Content-Length").get(0))];
            in.read(requestBody,0,requestBody.length);
            requestEntityBody = requestBody;
        }else {
            requestEntityBody = new byte[0];
        }
        return requestEntityBody;
    }

    /**
     * 关闭内存流 发送响应报文
     * @param responseLine 响应行
     * @param responseHeaders 响应头
     * @param inputStream 请求内存流
     * @param outputStream 响应内存流
     * @throws IOException io异常
     */
    private void response(ResponseLine responseLine,Headers responseHeaders,InputStream inputStream,ByteArrayOutputStream outputStream) throws IOException{
        if (responseLine.getReasonPhrase()==null||responseLine.getStatusCode()==null){
            responseLine.setReasonPhrase("OK");
            responseLine.setStatusCode("200");
        }
        responseLine.setVersion("HTTP/1.0");
        inputStream.close();//关闭内存流
        outputStream.flush();
        outputStream.close();//关闭内存流
        out.write(responseLine.toLine().getBytes());
        out.write("\r\n".getBytes());
        out.write(responseHeaders.format().getBytes());
        out.write("\r\n".getBytes());
        out.write(outputStream.toByteArray());
        out.flush();
    }
}