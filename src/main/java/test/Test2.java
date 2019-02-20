package test;

import app.mrquan.apache.ApacheServer;
import app.mrquan.nginx.NginxServer;
import app.mrquan.tomcat.util.XMLParsing;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Test2 {
    public static void main(String[] args) throws Exception {
        //路径 -> .class
//        Map map = XMLParsing.parsingXML("web/WEB-INF/web.xml");
//        System.out.println(map);

//        String m="/quan/client";
//        String context = "/quan";
//        String mapping = null;
//        int mm = m.indexOf(context);
//        System.out.println(mm);
//        if(mm!=-1) {//切割
//            mapping = m.substring(mm+context.length());
//        }else{
//            mapping = m;
//        }
//        System.out.println("相对url:"+mapping);

//        byte b[] = new byte[0];
//        ByteArrayInputStream inputStream = new ByteArrayInputStream(b);
//        inputStream.close();
//        inputStream.close();


        ApacheServer apacheServer = ApacheServer.create(9999, "/","web",(httpExchange)-> {
            System.out.println("apache"+httpExchange.getRequestLine());
            System.out.println(httpExchange.getRequestHeaders());
            if (httpExchange.getRequestEntityBody()!=null){
                System.out.println(new String(httpExchange.getRequestEntityBody()));
            }
//            System.out.println(new String(httpExchange.getRequestEntityBody()));
        });
        apacheServer.start();
        NginxServer nginxServer = NginxServer.create(8888, "/", "127.0.0.1", 9999,(requestDetails)->{
            System.out.println("=================================nginx:"+(Boolean.TRUE.equals(requestDetails.getCache())?"已缓存":"未缓存")+"\t"+requestDetails);
        });
        nginxServer.start();
        System.out.println("begin~~~~");
        
    }
}