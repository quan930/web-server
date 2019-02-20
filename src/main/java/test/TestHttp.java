package test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class TestHttp {
    public static void main(String[] args) throws Exception {
        URL url = new URL("http://localhost:7777/quan/hello");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        String m;
        while ((m=reader.readLine())!=null){
            System.out.println(m);
        }
    }
}
