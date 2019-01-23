package test;

import app.mrquan.ApacheServer;
import app.mrquan.HttpExchange;
import app.mrquan.HttpHandler;

import java.io.File;
import java.io.IOException;

public class Test1 {
    public static void main(String[] args) throws IOException {
//        File file = new File("www/html");
//        System.out.println(file.getAbsolutePath());



        ApacheServer apacheServer = new ApacheServer(8888, "/","www/html",new HttpHandler() {
            public void handle(HttpExchange httpExchange) throws IOException {
                System.out.println(httpExchange.getRequestLine());
//                System.out.println(new String(httpExchange.getRequestEntityBody()));
            }
        });

        apacheServer.start();
    }
}