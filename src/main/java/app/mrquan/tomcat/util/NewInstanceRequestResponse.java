package app.mrquan.tomcat.util;

import app.mrquan.tomcat.http.*;
import app.mrquan.tomcat.http.pojo.Headers;
import app.mrquan.tomcat.http.pojo.RequestLine;
import app.mrquan.tomcat.http.pojo.ResponseLine;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * 实例化HttpServletRequest HttpServletResponse
 */
public class NewInstanceRequestResponse {
    /**
     * 实例化 HttpServletRequest
     * @param context 上下文
     * @param requestLine 请求行
     * @param requestHeaders 请求头
     * @param requestEntityBody 请求报文
     * @return HttpServletRequest
     */
    public static HttpServletRequest createHttpServletRequest(String context, RequestLine requestLine, Headers requestHeaders, byte[] requestEntityBody) throws UnsupportedEncodingException {
        Map<String,String> map = StringHandling.queryStringToMap(java.net.URLDecoder.decode(requestLine.getQueryString(), "UTF-8"));//解决乱码
//        System.out.println("查询字符串map:"+map);
        HttpServletRequest request = new HttpServletRequest() {
            @Override
            public long getContentLength() {//正文长度
                if (requestHeaders.get("Content-Length")!=null){
                    return Long.parseLong(requestHeaders.get("Content-Length").get(0));
                }
                return -1;
            }

            @Override
            public InputStream getInputStream() {
                return new ByteArrayInputStream(requestEntityBody);
            }

            @Override
            public String getProtocol() {
                return requestLine.getVersion();
            }

            @Override
            public String getContextPath() {
                return context;
            }

            @Override
            public Headers getHeaders() {
                return requestHeaders;
            }

            @Override
            public String getMethod() {
                return requestLine.getMethod();
            }

            @Override
            public String getRequestURL() {
                return requestLine.getSimpleRUL();
            }

            @Override
            public String getQueryString() {
                return requestLine.getQueryString();
            }

            @Override
            public String getParameter(String name) {
                return map.get(name);
            }
        };
        return request;
    }

    /**
     * 实例化 HttpServletResponse
     * @param responseLine 响应行
     * @param responseHeaders 响应头
     * @param outputStream 报文流
     * @return HttpServletResponse对象
     */
    public static HttpServletResponse createHttpServletResponse(ResponseLine responseLine, Headers responseHeaders, OutputStream outputStream){
        return new HttpServletResponse() {
            @Override
            public void setContentType(String type) {
                responseHeaders.add("Content-type",type);
            }

            @Override
            public OutputStream getOutputStream() {
                return outputStream;
            }

            @Override
            public void addHeader(String name, String value) {
                responseHeaders.add(name, value);
            }

            @Override
            public void setHeader(String name, String value) {
                responseHeaders.set(name, value);
            }

            @Override
            public void setStatus(int sc, String reasonPhrase) {
                responseLine.setStatusCode(sc+"");
                responseLine.setReasonPhrase(reasonPhrase);
            }

            @Override
            public void setContentLength(int length) {
                responseHeaders.set("Content-Length",Integer.toString(length+2));
            }
        };
    }
}
