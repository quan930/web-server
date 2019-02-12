package app.mrquan.nginx.pojo;

import java.util.Objects;

//请求行
public class RequestLine {
    private String method;//请求方法
    private String requestURL;//请求url
    private String version;//请求版本

    public RequestLine(String line) {
        init(line);
    }

    public String getVersion() {
        return version;
    }
    public String getMethod() {
        return method;
    }
    public String getRequestURL() {
        return requestURL;
    }

    public String toLine(){
        return method+" "+requestURL+" "+version;
    }
    @Override
    public String toString() {
        return "Request Line\t" + "method=" + method +",url=" + requestURL + ",version=" + version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RequestLine)) return false;
        RequestLine that = (RequestLine) o;
        return Objects.equals(method, that.method) &&
                Objects.equals(requestURL, that.requestURL) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, requestURL, version);
    }

    private void init(String line){
        String requestLine[] = line.split(" ");
        method = requestLine[0];
        requestURL = requestLine[1];
//            int mm = requestLine[1].indexOf("?");
//            if(mm!=-1) {//切割
//                requestURL = requestLine[1].substring(0,mm);
//            }else{
//                requestURL = requestLine[1];
//            }
        version = requestLine[2];
    }
}
