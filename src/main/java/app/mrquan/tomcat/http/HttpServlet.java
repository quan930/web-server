package app.mrquan.tomcat.http;

import app.mrquan.tomcat.Servlet;
import app.mrquan.tomcat.config.DisplayPage;
import app.mrquan.tomcat.util.StringHandling;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

public class HttpServlet implements Servlet {
    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    @Override
    final public void service(HttpServletRequest request,HttpServletResponse response) {
        try {
            //利用反射执行响应方法
            getClass().getMethod(StringHandling.methodFormat(request.getMethod()), HttpServletRequest.class, HttpServletResponse.class).invoke(this,request,response);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void doGet(HttpServletRequest request,HttpServletResponse response){
        try {
            response.setContentType("text/html; charset=UTF-8");
            response.getOutputStream().write(DisplayPage.Code405.getBytes(StandardCharsets.UTF_8));
            response.setContentLength(DisplayPage.Code405.getBytes(StandardCharsets.UTF_8).length);
            response.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response){
        try {
            send405(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void send405(HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=UTF-8");
        response.getOutputStream().write(DisplayPage.Code405.getBytes(StandardCharsets.UTF_8));
        response.setContentLength(DisplayPage.Code405.getBytes(StandardCharsets.UTF_8).length);
        response.getOutputStream().flush();
    }
}
