package app.mrquan.tomcat.http.pojo;

/**
 * 请求行
 */
public class RequestLine {
    private String method;//请求方法
    private String requestURL;//请求url
    private String className;
    private String simpleRUL;//去除？后内容
    private String version;//请求版本
    private String context;
    private String queryString;

    public RequestLine(String line, String context) {
        this.context = context;
        init(line);
    }

    public String getQueryString() {
        return queryString;
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

    public String getSimpleRUL() {
        return simpleRUL;
    }

    public String getClassName() {
        return className;
    }

    @Override
    public String toString() {
        return "RequestLine{" +
                "method='" + method + '\'' +
                ", requestURL='" + requestURL + '\'' +
                ", className='" + className + '\'' +
                ", simpleRUL='" + simpleRUL + '\'' +
                ", version='" + version + '\'' +
                '}';
    }

    private void init(String line){
        String requestLine[] = line.split(" ");
        method = requestLine[0];
        requestURL = requestLine[1];
        int mm = requestLine[1].indexOf("?");
        if(mm!=-1) {//切割
            simpleRUL = requestLine[1].substring(0,mm);
            queryString = requestLine[1].substring(mm+1);
        }else{
            simpleRUL = requestLine[1];
            queryString = "";
        }
        int m = simpleRUL.indexOf(context);//相对路径
        if(m==0) {//切割
            className = simpleRUL.substring(m+context.length());
        }else {
            className = simpleRUL;
        }
        version = requestLine[2];
    }
}
