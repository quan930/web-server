package app.mrquan.nginx;

import app.mrquan.nginx.pojo.RequestDetails;
import app.mrquan.nginx.pojo.RequestLine;

public interface Callback {
    /**
     * 请求详情
     * @param requestDetails
     */
    void callback(RequestDetails requestDetails);
}
