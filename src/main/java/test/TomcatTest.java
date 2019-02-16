package test;

import app.mrquan.tomcat.TomcatServer;

public class TomcatTest {
    public static void main(String[] args) {
        TomcatServer tomcatServer = TomcatServer.create(7777,"/quan");
        tomcatServer.start();
    }
}
