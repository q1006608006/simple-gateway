package top.ivan.simple.gateway.core;

import top.ivan.simple.gateway.core.http.HttpResponse;

/**
 * @author Ivan on 2019/11/20.
 * @version 1.0
 *
 * 异常统一处理入口
 */
public interface ChainExceptionHandler {

    /**
     * 异常统一处理入口
     * @param request 请求
     * @param cause 链处理抛出的异常
     * @return 请求方收到的结果
     */
    HttpResponse handle(WebRequest request, Throwable cause);
}
