package app.mrquan.tomcat.test;

import app.mrquan.tomcat.http.HttpServlet;
import app.mrquan.tomcat.http.HttpServletRequest;
import app.mrquan.tomcat.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HelloServlet extends HttpServlet {
    private int n;
    @Override
    public void init() {
        super.init();
        System.out.println(getClass().getName()+"初始化了～～～～～");
        n = 0;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html; charset=UTF-8");
        try {
            String m = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"></head><body><h1>"+
                    request.getParameter("name")+"第"+String.valueOf(++n
            )+"次"+"</h1></body></html>";
            response.setContentLength(m.getBytes(StandardCharsets.UTF_8).length);
            response.getOutputStream().write(m.getBytes(StandardCharsets.UTF_8));
            response.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
