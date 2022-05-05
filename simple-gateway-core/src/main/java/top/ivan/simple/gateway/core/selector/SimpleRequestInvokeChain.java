package top.ivan.simple.gateway.core.selector;

import top.ivan.simple.gateway.core.BadRequestException;
import top.ivan.simple.gateway.core.RequestFilter;
import top.ivan.simple.gateway.core.RequestInvokeChain;
import top.ivan.simple.gateway.core.WebRequest;
import top.ivan.simple.gateway.core.filter.FilterMetadata;
import top.ivan.simple.gateway.core.http.HttpResponse;
import top.ivan.simple.gateway.core.util.ChainLogger;
import top.ivan.simple.gateway.core.util.Pair;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Ivan on 2019/11/18.
 * @version 1.0
 * 简易执行链，根据RequestFilter调用链按顺序执行
 */
public class SimpleRequestInvokeChain implements RequestInvokeChain {

    @Getter
    private final List<Pair<RequestFilter, FilterMetadata>> filterList;

    private SimpleRequestInvokeChain(List<Pair<RequestFilter, FilterMetadata>> filters) {
        this.filterList = filters;
    }

    /**
     * 代理方式，实际上使用内部的invokeChain，避免并发问题
     *
     * @param request
     * @return
     * @throws BadRequestException
     */
    @Override
    public HttpResponse doChain(WebRequest request) throws BadRequestException {
        RealInvokeChain chain = new RealInvokeChain(0);
        return chain.doChain(request);
    }

    private class RealInvokeChain implements RequestInvokeChain {
        private final int currentPos;

        public RealInvokeChain(int currentPos) {
            this.currentPos = currentPos;
        }

        @Override
        public HttpResponse doChain(WebRequest request) throws BadRequestException {
            //根据position判断当前执行的是哪个filter
            HttpResponse response = null;
            if (currentPos < filterList.size()) {
                Pair<RequestFilter, FilterMetadata> pair = filterList.get(currentPos);
                RequestFilter filter = pair.getKey();
                FilterMetadata metadata = pair.getValue();
                long st = System.currentTimeMillis();
                response = filter.invoke(request, new RealInvokeChain(currentPos + 1), metadata);
                if (ChainLogger.isDebugEnable()) {
                    long ed = System.currentTimeMillis();
                    ChainLogger.logDebug("[{}] cost: {}ms", metadata.get("id"), ed - st);
                }
            }
            //无可用调用链返回空
            return response;
        }
    }

    public static SimpleRequestInvokeChain fromFilterMetadata(List<Pair<RequestFilter, FilterMetadata>> filters) {
        return new SimpleRequestInvokeChain(filters);
    }

    public static SimpleRequestInvokeChain fromFilters(List<RequestFilter> filters) {
        return new SimpleRequestInvokeChain(filters.stream().map(f -> new Pair<>(f, new FilterMetadata(Collections.emptyMap()))).collect(Collectors.toList()));
    }

}
