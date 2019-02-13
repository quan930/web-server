package app.mrquan.nginx.pojo;

import java.util.Arrays;
import java.util.Objects;

/**
 * 请求详情 记录请求次数 是否缓存 响应报文 (适合静态资源)
 */
public class RequestDetails {
    private Integer number;//请求次数
    private String url;//请求url
    private String method;//请求方法
    private Boolean cache;//是否缓存
    private byte[] responseMessage;//响应报文

    public RequestDetails(Integer number, String url, String method, Boolean cache) {
        this.number = number;
        this.url = url;
        this.method = method;
        this.cache = cache;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    synchronized public String getUrl() {
        return url;
    }

    synchronized public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    synchronized public Boolean getCache() {
        return cache;
    }

    synchronized public void setCache(Boolean cache) {
        this.cache = cache;
    }

    synchronized public byte[] getResponseMessage() {
        return responseMessage;
    }

    synchronized public void setResponseMessage(byte[] responseMessage) {
        this.responseMessage = responseMessage;
    }

    @Override
    public String toString() {
        return "RequestDetails"+"......url=" + url + "\tmethod=" + method + "\tnumber=" + number+"\tcache=" + cache;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RequestDetails)) return false;
        RequestDetails that = (RequestDetails) o;
        return Objects.equals(number, that.number) &&
                Objects.equals(url, that.url) &&
                Objects.equals(method, that.method) &&
                Objects.equals(cache, that.cache) &&
                Arrays.equals(responseMessage, that.responseMessage);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(number, url, method, cache);
        result = 31 * result + Arrays.hashCode(responseMessage);
        return result;
    }
}
