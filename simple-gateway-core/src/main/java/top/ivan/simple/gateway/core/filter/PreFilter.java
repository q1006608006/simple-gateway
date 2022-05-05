package top.ivan.simple.gateway.core.filter;

import top.ivan.simple.gateway.core.BadRequestException;
import top.ivan.simple.gateway.core.RequestFilter;
import top.ivan.simple.gateway.core.RequestInvokeChain;
import top.ivan.simple.gateway.core.WebRequest;
import top.ivan.simple.gateway.core.http.HttpResponse;

/**
 * @author Ivan
 * @since 2021/09/15 17:01
 */
public interface PreFilter extends RequestFilter {

    default HttpResponse invoke(WebRequest request, RequestInvokeChain chain, FilterMetadata metadata) throws BadRequestException {
        prepare(request, metadata);
        return chain.doChain(request);
    }

    void prepare(WebRequest request, FilterMetadata metadata) throws BadRequestException;
}
