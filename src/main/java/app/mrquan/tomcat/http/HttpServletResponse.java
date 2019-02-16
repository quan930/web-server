package app.mrquan.tomcat.http;

import java.io.OutputStream;

public interface HttpServletResponse {
    /**
     * 设置响应正文的 MIME 类型
     * @param type 类型描述
     */
    void setContentType(String type);

    /**
     * 返回报文输出流
     * @return out
     */
    OutputStream getOutputStream();

    /**
     * 向响应头添加内容
     * @param name 属性名
     * @param value value描述
     */
    void addHeader(String name,String value);

    /**
     * 设置HTTP 响应头内容 覆盖原有内容
     * @param name 属性名
     * @param value value描述
     */
    void setHeader(String name,String value);

    /**
     * 设置响应
     * @param sc 设置响应状态码
     * @param reasonPhrase 设置原因短语
     */
    void setStatus(int sc,String reasonPhrase);

    /**
     * 设置响应正文长度
     * @param length 长度
     */
    void setContentLength(int length);
}
