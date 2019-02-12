package app.mrquan.apache;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class HttpExchange implements Runnable{
    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private RequestLine requestLine;//请求行
    private ResponseLine responseLine;//响应行
    private Headers requestHeaders;//请求头
    private Headers responseHeaders;//响应头
    private byte[] requestEntityBody;//请求实体
    private byte[] responseEntityBody;//响应实体
    private String filePath;//文件路径
    private HttpHandler handler;
    public HttpExchange(Socket socket, HttpHandler handler,String filePath) {
        this.socket = socket;
        this.handler = handler;
        this.filePath = filePath;
    }
    public void setResponse(ResponseLine responseLine,Headers responseHeaders,byte[] responseEntityBody){
        this.responseLine = responseLine;
        this.responseHeaders = responseHeaders;
        this.responseEntityBody = responseEntityBody;
    }
    public void run() {
        try {
            init();//请求
            handler.handle(this);//响应回调
            response();
            /*
             * 发送报文
             */
            socket.shutdownOutput();
            socket.shutdownInput();
//            out.close();
//            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 请求 可封装流
     * @throws IOException
     */
    private void init() throws IOException{
        /**
         * 请求行
         */

        in = socket.getInputStream();
        out = new BufferedOutputStream(socket.getOutputStream());
        requestHeaders = new Headers();
//        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//        BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
        StringBuilder requestLineBuffer = new StringBuilder();
        int i;
        while ((i = in.read())!=13){//读取请求行
            requestLineBuffer.append((char)i);
        }
        in.read();
        requestLine = new RequestLine(requestLineBuffer.toString());

//        requestLine = new RequestLine(reader.readLine());
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
//        String s;
//        while ((s = reader.readLine()).length()!=0){
//            System.out.println("aaa"+s);
//            requestHeaders.set(s.substring(0,s.indexOf(": ")),s.substring(s.indexOf(": ")+2));
//        }
//        System.out.println("++++++++++++++");
        /**
         * 请求体
         */
        if (requestHeaders.get("Content-Length")!=null){
            byte requestBody[] = new byte[Integer.parseInt(requestHeaders.get("Content-Length").get(0))];
            in.read(requestBody,0,requestBody.length);
            requestEntityBody = requestBody;
        }
//        System.out.println("啊啊啊啊啊啊啊啊啊啊啊");
    }

    /**
     * 响应
     * @throws IOException
     */
    private void response() throws IOException {
        File file = new File(filePath+requestLine.requestURL);
        if (file.isDirectory()){
            file = new File(filePath+requestLine.requestURL+"/index.html");
        }
        if (file.exists()){//判断请求是否存在
            responseLine = new ResponseLine("HTTP/1.0","200","OK");
            responseHeaders = new Headers();
            responseHeaders.add("Content-Length",String.valueOf(file.length()));
            responseEntityBody = getBytes(file);
        }else {//404
            File file1 = new File(filePath+"/404.html");
            if (file1.exists()){//判断请求是否存在
                responseLine = new ResponseLine("HTTP/1.0","404","Not Found");
                responseHeaders = new Headers();
                responseHeaders.add("Content-Length",String.valueOf(file1.length()));
                responseEntityBody = getBytes(file1);
            }
        }
//        long start = System.currentTimeMillis();
        out.write(responseLine.toLine().getBytes());
        out.write("\r\n".getBytes());
        out.write(responseHeaders.format().getBytes());
        out.write("\r\n\r\n".getBytes());
        out.write(responseEntityBody);
        out.flush();
//        System.out.println("写入时间:"+requestLine.requestURL+"\t"+(System.currentTimeMillis()-start));
    }

    /**
     * 文件转字节
     * @param file 文件
     * @return 字节数组
     */
    private byte[] getBytes(File file){
        byte[] buffer = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }
    public byte[] getRequestEntityBody() {
        return requestEntityBody;
    }
    public Headers getRequestHeaders() {
        return requestHeaders;
    }
    public RequestLine getRequestLine() {
        return requestLine;
    }
    class RequestLine{//请求行
        private String method;//请求方法
        private String requestURL;//请求url
        private String version;//请求版本

        private RequestLine(String line) {
            init(line);
        }
        @Override
        public String toString() {
            return "Request Line:" + "method=" + method +",url=" + requestURL + ",version=" + version;
        }
        public String getVersion() {
            return version;
        }
        public String getMethod() {
            return method;
        }
        public String getRequestURL() {
            return requestURL;
        }

        private void init(String line){
            String requestLine[] = line.split(" ");
            method = requestLine[0];
            requestURL = requestLine[1];
//            int mm = requestLine[1].indexOf("?");
//            if(mm!=-1) {//切割
//                requestURL = requestLine[1].substring(0,mm);
//            }else{
//                requestURL = requestLine[1];
//            }
            version = requestLine[2];
        }
    }
    class ResponseLine{
        private String version;//版本
        private String statusCode;//状态码
        private String reasonPhrase;//原因短语

        ResponseLine(String version, String statusCode, String reasonPhrase) {
            this.version = version;
            this.statusCode = statusCode;
            this.reasonPhrase = reasonPhrase;
        }
        private String toLine(){
            return version+" "+statusCode+" "+reasonPhrase;
        }
    }

}
