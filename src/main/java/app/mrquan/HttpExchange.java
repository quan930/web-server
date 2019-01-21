package app.mrquan;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

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
    private HttpHandler handler;
    public HttpExchange(Socket socket, HttpHandler handler) {
        this.socket = socket;
        this.handler = handler;
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
    public void setResponseLine(ResponseLine responseLine) {
        this.responseLine = responseLine;
    }
    public void setResponse(ResponseLine responseLine,Headers responseHeaders,byte[] responseEntityBody){
        this.responseLine = responseLine;
        this.responseHeaders = responseHeaders;
        this.responseEntityBody = responseEntityBody;
    }
    private void init1() throws IOException {
        in = socket.getInputStream();
        out = socket.getOutputStream();
        requestHeaders = new Headers();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        requestLine = new RequestLine(reader.readLine());//请求行
        String m;
        while ((m = reader.readLine()).length()!=0){
            requestHeaders.set(m.substring(0,m.indexOf(": ")),m.substring(m.indexOf(": ")+2));
            //请求头
        }
        if (requestHeaders.get("Content-Length")!=null){
            System.out.println(requestHeaders.get("Content-Length").get(0));
            byte requestBody[] = new byte[Integer.parseInt(requestHeaders.get("Content-Length").get(0))];
            System.out.println(in.read(requestBody,0,requestBody.length));
            String s = new String(requestBody);
            System.out.println("aaaaaa"+s);
        }
    }
    /**
     * 读取请求 可封装流
     * @throws IOException
     */
    private void init() throws IOException{
        /**
         * 请求行
         */
        in = socket.getInputStream();
        out = socket.getOutputStream();
        requestHeaders = new Headers();
        StringBuilder requestLineBuffer = new StringBuilder();
        int i;
        while ((i = in.read())!=13){//读取请求行
            requestLineBuffer.append((char)i);
        }
        in.read();
        requestLine = new RequestLine(requestLineBuffer.toString());
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
                    continue;
                }
            }else{
                requestHeader.append((char)m);
            }
        }
        for (String s : requestHeader.toString().split("\r\n")) {
            requestHeaders.set(s.substring(0,s.indexOf(": ")),s.substring(s.indexOf(": ")+2));
        }
        /**
         * 请求体
         */
        if (requestHeaders.get("Content-Length")!=null){
            byte requestBody[] = new byte[Integer.parseInt(requestHeaders.get("Content-Length").get(0))];
            in.read(requestBody,0,requestBody.length);
            requestEntityBody = requestBody;
        }
    }
    public void run() {
        try {
            init();//请求
            handler.handle(this);//响应回调
            /**
             * 发送报文
             */
            out.write(responseLine.toLine().getBytes());
            out.write("\r\n".getBytes());
            out.write(responseHeaders.format().getBytes());
            out.write("\r\n\r\n".getBytes());
            out.write(responseEntityBody);
            in.close();
            out.close();
            socket.close();
//            socket.getInputStream().close();
//            socket.getOutputStream().close();
//            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            return "请求行\t" + "method=" + method +",url=" + requestURL + ",version=" + version;
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
            int mm = requestLine[1].indexOf("?");
            if(mm!=-1) {//切割
                requestURL = requestLine[1].substring(0,mm);
            }else{
                requestURL = requestLine[1];
            }
            version = requestLine[2];
        }
    }
    public static class ResponseLine{
        private String version;//版本
        private String statusCode;//状态码
        private String reasonPhrase;//原因短语

        public ResponseLine(String version, String statusCode, String reasonPhrase) {
            this.version = version;
            this.statusCode = statusCode;
            this.reasonPhrase = reasonPhrase;
        }
        private String toLine(){
            return version+" "+statusCode+" "+reasonPhrase;
        }
    }
}
