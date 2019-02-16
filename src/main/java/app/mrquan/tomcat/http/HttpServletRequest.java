package app.mrquan.tomcat.http;

import app.mrquan.tomcat.http.pojo.Headers;

import java.io.InputStream;

public interface HttpServletRequest {
    /**
     * 返回请求报文长度，如长度未知返回-1
     * @return 报文长度
     */
    long getContentLength();

    /**
     * 返回用于读取请求正文的输入流
     * @return io流
     */
    InputStream getInputStream();

    /**
     * 通信协议描述
     * @return 协议名称，版本号
     */
    String getProtocol();

    /**
     * 返回URL人口
     * @return 上下文
     */
    String getContextPath();

    /**
     * 返回请求首部
     * @return map集合
     */
    Headers getHeaders();

    /**
     * 返回HTTP 请求方式
     * @return 请求方式
     */
    String getMethod();

    /**
     * 返回请求的url
     * @return url
     */
    String getRequestURL();

    /**
     * 返回查询字符串 ?后面内容
     * @return 查询结果
     */
    String getQueryString();

    /**
     * 根据请求参数名，返回请求参数值
     * @param name 请求参数名
     * @return 请求参数值
     */
    String getParameter(String name);
}
