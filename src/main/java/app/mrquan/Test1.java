package app.mrquan;

import java.io.IOException;

public class Test1 {
    public static void main(String[] args) throws IOException {
        ApacheServer apacheServer = new ApacheServer(8888, "/", new HttpHandler() {
            public void handle(HttpExchange httpExchange) throws IOException {
                System.out.println(httpExchange.getRequestLine());
//                System.out.println(new String(httpExchange.getRequestEntityBody()));
                String m = "hello world";
                Headers responseHeaders = new Headers();
                responseHeaders.add("Content-Length",String.valueOf(m.getBytes().length));
                httpExchange.setResponse(new HttpExchange.ResponseLine("HTTP/1.0","200","OK"),responseHeaders,m.getBytes());
            }
        });

        apacheServer.start();
    }
}