package top.ivan.simple.gateway.core;

import top.ivan.simple.gateway.core.filter.FilterMetadata;
import top.ivan.simple.gateway.core.http.HttpResponse;

/**
 * @author Ivan on 2019/11/18.
 * @version 1.0
 * <p>
 * 请求过滤器，由调用链调用
 */
public interface RequestFilter {

    /**
     * 执行请求
     *
     * @param request  请求体
     * @param chain    调用链（使用chain.doChain(body)显示的调用下一级调用栈）
     * @param metadata 元数据，指定在配置文件中的参数
     * @return 最终交给上一级调用栈处理的结果
     * @throws BadRequestException 执行过程中出现的异常
     */
    HttpResponse invoke(WebRequest request, RequestInvokeChain chain, FilterMetadata metadata) throws BadRequestException;

}
