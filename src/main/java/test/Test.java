package test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Test {
    public static void main(String[] args) throws IOException {
        URL url = new URL("localhost:8888/dddd");
        URLConnection connection = url.openConnection();
        System.out.println(connection);
    }
}
