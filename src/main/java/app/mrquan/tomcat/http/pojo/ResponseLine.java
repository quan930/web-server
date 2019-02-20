package app.mrquan.tomcat.http.pojo;

public class ResponseLine {
    private String version;//版本
    private String statusCode;//状态码
    private String reasonPhrase;//原因短语

    public ResponseLine(String version, String statusCode, String reasonPhrase) {
        this.version = version;
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public ResponseLine() {
    }

    public String toLine(){
        return version+" "+statusCode+" "+reasonPhrase;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public void setReasonPhrase(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }
}
