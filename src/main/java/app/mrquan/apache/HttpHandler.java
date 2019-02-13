package app.mrquan.apache;

import java.io.IOException;

/**
 * 异步回调
 */
public interface HttpHandler {
    /**
     *  线程执行逻辑
     * @param httpExchange http交互类
     */
    void handle(final HttpExchange httpExchange)throws IOException;
}
