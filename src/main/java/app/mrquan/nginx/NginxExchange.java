package app.mrquan.nginx;

import app.mrquan.nginx.pojo.RequestDetails;
import app.mrquan.nginx.pojo.RequestLine;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class NginxExchange implements Runnable {
    private Socket socket;
    private ConcurrentHashMap<RequestLine, RequestDetails> map;
    private int serverPort;//服务器的端口号
    private String serverAddress;//服务器ip地址
    private Callback callback;
    private InputStream in;
    private OutputStream out;

    public NginxExchange(Socket socket, ConcurrentHashMap<RequestLine, RequestDetails> map, int serverPort, String serverAddress,Callback callback) {
        this.socket = socket;
        this.map = map;
        this.serverPort = serverPort;
        this.serverAddress = serverAddress;
        this.callback = callback;

    }

    public void run() {
        /**
         * 反向代理策略
         */
        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(out);

            RequestLine requestLine = requestLine();//请求行
            Headers headers;//请求首部
            byte requestBody[];
            if (map.containsKey(requestLine)){//请求已经存在
                map.get(requestLine).setNumber(map.get(requestLine).getNumber()+1);
                if (map.get(requestLine).getCache()){//已经缓存
                    headers = requestHeaders();
                    requestBody =  requestEntityBody(headers);
                    bufferedOutputStream.write(map.get(requestLine).getResponseMessage());
                    bufferedOutputStream.flush();
//                    System.out.println("已经缓存.............."+requestLine+"====="+map.get(requestLine));
                }else {//没有缓存
                    headers = requestHeaders();
                    requestBody =  requestEntityBody(headers);
                    if (requestLine.getMethod().toUpperCase().equals("GET")&&!requestLine.getRequestURL().contains("?")&&map.get(requestLine).getNumber()>1){
                        map.get(requestLine).setCache(true);//缓存
                        map.get(requestLine).setResponseMessage(getResponse(requestLine,headers,requestBody));
                        bufferedOutputStream.write(map.get(requestLine).getResponseMessage());
//                        System.out.println("缓存!.............."+requestLine+"====="+map.get(requestLine));
                    }else {
                        bufferedOutputStream.write(getResponse(requestLine,headers,requestBody));
                    }
                    bufferedOutputStream.flush();
                }
            }else {
                map.put(requestLine,new RequestDetails(1,requestLine.getRequestURL(),requestLine.getMethod(),false));
                headers = requestHeaders();
                requestBody =  requestEntityBody(headers);
                bufferedOutputStream.write(getResponse(requestLine,headers,requestBody));
                bufferedOutputStream.flush();
            }
            callback.callback(map.get(requestLine));
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
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
        return new RequestLine(requestLineBuffer.toString());
    }

    /**
     * 读取报文首部
     * @return 报文首部
     * @throws IOException 异常
     */
    private Headers requestHeaders() throws IOException {
        StringBuilder requestHeader = new StringBuilder();
        Headers headers = new Headers();
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
            headers.set(s.substring(0,s.indexOf(": ")),s.substring(s.indexOf(": ")+2));
        }
        return headers;
    }

    /**
     * 读取报文主体
     * @param requestHeaders 请求头
     * @return 报文主体
     * @throws IOException 异常
     */
    private byte[] requestEntityBody(Headers requestHeaders) throws IOException {
        if (requestHeaders.get("Content-Length")!=null){
            byte requestBody[] = new byte[Integer.parseInt(requestHeaders.get("Content-Length").get(0))];
            in.read(requestBody,0,requestBody.length);
            return requestBody;
        }
        return null;
    }

    /**
     * 获取响应报文
     * @return
     */
    private byte[] getResponse(RequestLine requestLine,Headers headers,byte requestBody[]) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Socket socket = new Socket(serverAddress,serverPort);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
        InputStream inputStream = socket.getInputStream();
        bufferedOutputStream.write(requestLine.toLine().getBytes());
        bufferedOutputStream.write("\r\n".getBytes());
        bufferedOutputStream.write(headers.format().getBytes());
        bufferedOutputStream.write("\r\n\r\n".getBytes());
        if (requestBody!=null){
            bufferedOutputStream.write(requestBody);
            System.out.println(new String(requestBody));
        }
        bufferedOutputStream.flush();
        byte[] b = new byte[32];
//        int count = 0;
//        do {
//            count = inputStream.read(b);
//            byteArrayOutputStream.write(b, 0, count);
//        } while (inputStream.available() != 0);

        int n;
        while ((n = inputStream.read(b)) != -1) {
            byteArrayOutputStream.write(b, 0, n);
//            if (inputStream.available() == 0){
//                break;
//            }
        }
        byteArrayOutputStream.close();
        inputStream.close();
        bufferedOutputStream.close();
        socket.close();
        return byteArrayOutputStream.toByteArray();

    }

}
