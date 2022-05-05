package top.ivan.simple.gateway.core;

import top.ivan.simple.gateway.core.http.HttpResponse;

/**
 * @author Ivan on 2019/11/18.
 * @version 1.0
 * <p>
 * 请求调用链
 */
public interface RequestInvokeChain {

    /**
     * 处理请求
     *
     * @param request 本次请求
     * @return 请求端收到的内容（自动转为json对象）
     * @throws BadRequestException 处理调用链时出现的异常
     */
    HttpResponse doChain(WebRequest request) throws BadRequestException;
}
