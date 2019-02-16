package app.mrquan.tomcat;

import app.mrquan.tomcat.http.HttpServletRequest;
import app.mrquan.tomcat.http.HttpServletResponse;

public interface Servlet {
    /**
     * 初始化Servlet
     */
    void init();

    /**
     * 服务
     * @param request 响应对象
     * @param response 请求对象
     */
    void service(HttpServletRequest request, HttpServletResponse response);

    /**
     * 销毁方式
     */
    void destroy();
}
