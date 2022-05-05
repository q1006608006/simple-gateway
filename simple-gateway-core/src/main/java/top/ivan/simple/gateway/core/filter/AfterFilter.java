package top.ivan.simple.gateway.core.filter;

import top.ivan.simple.gateway.core.BadRequestException;
import top.ivan.simple.gateway.core.RequestFilter;
import top.ivan.simple.gateway.core.RequestInvokeChain;
import top.ivan.simple.gateway.core.WebRequest;
import top.ivan.simple.gateway.core.http.HttpResponse;

/**
 * @author Ivan
 * @since 2021/09/15 16:58
 */
public interface AfterFilter extends RequestFilter {

    default HttpResponse invoke(WebRequest request, RequestInvokeChain chain, FilterMetadata metadata) throws BadRequestException {
        HttpResponse response = chain.doChain(request);
        after(request, response, metadata);
        return response;
    }

    void after(WebRequest request, HttpResponse response, FilterMetadata metadata) throws BadRequestException;
}
