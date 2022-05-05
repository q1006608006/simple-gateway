package top.ivan.simple.gateway.core.filter.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import top.ivan.simple.gateway.core.*;
import top.ivan.simple.gateway.core.annotation.FilterId;
import top.ivan.simple.gateway.core.filter.FilterMetadata;
import top.ivan.simple.gateway.core.http.HttpInvoker;
import top.ivan.simple.gateway.core.http.HttpRequest;
import top.ivan.simple.gateway.core.http.HttpResponse;
import top.ivan.simple.gateway.core.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * @author Ivan
 * @description
 * @date 2020/12/28
 */
@Slf4j
@FilterId(GTConstant.REDIRECT_FILTER_NAME)
@Component
public class GTRedirectFilter implements RequestFilter {

    /**
     * 指定转发HTTP方法
     */
    private static final String PARAM_METHOD = "method";
    /**
     * 指定转发头
     */
    private static final String PARAM_SET_HEADER = "set-header";
    /**
     * 添加转发头
     */
    private static final String PARAM_ADD_HEADER = "add-header";
    /**
     * 指定转发地址
     */
    private static final String PARAM_URL = "url";

    @Autowired
    private HttpInvoker httpInvoker;

    @Override
    public HttpResponse invoke(WebRequest request, RequestInvokeChain chain, FilterMetadata metadata) throws BadRequestException {
        if (log.isDebugEnabled()) {
            log.debug("into redirect");
        }

        configureRequest(request, metadata);

        byte[] redirectBody = request.getRedirectBody();
        String redirectMethod = request.getRedirectMethod();
        HttpHeaders redirectHeaders = request.getRedirectHeaders();
        Object requestConfig = request.getRedirectRequestConfig();

        String redirectUrl = request.getRedirectUrl();
        if (null == redirectUrl) {
            log.error("转发地址不能为空！");
            throw new BadGatewayException(GTConstant.UNKNOWN_HOST);
        }

        //获取请求包
        try {
            //转发并返回
            HttpRequest req = new HttpRequest(redirectUrl, redirectMethod);
            req.setHeaders(redirectHeaders);
            req.setBody(redirectBody);

            HttpResponse rsp = httpInvoker.invoke(req, requestConfig);
            request.putByType(HttpRequest.class, req);
            request.putByType(HttpResponse.class, rsp);

            HttpResponse next = chain.doChain(request);
            return Optional.ofNullable(next).orElse(rsp);
        } catch (Exception e) {
            log.error("redirect to: " + redirectUrl + " failed,cause:", e.getMessage());
            throw new InternalServerException("后台服务异常", e);
        }
    }

    public void configureRequest(WebRequest request, FilterMetadata metadata) {
        String method = metadata.get(PARAM_METHOD);
        if (StringUtils.hasLength(method)) {
            request.setRedirectMethod(method);
        }
        String url = metadata.get(PARAM_URL);
        if (StringUtils.hasLength(url)) {
            request.setRedirectUrl(url);
        }
        PairList headers = metadata.readFromList(PARAM_SET_HEADER, PairList.class, PairList::new);
        for (Pair<String, String> header : headers) {
            request.setRedirectHeader(header.getKey(), header.getValue());
        }
        headers = metadata.readFromList(PARAM_ADD_HEADER, PairList.class, PairList::new);
        for (Pair<String, String> pair : headers) {
            request.addRedirectHeaderValue(pair.getKey(), pair.getValue());
        }
    }

    private static class PairList extends ArrayList<Pair<String, String>> {
        public PairList(Collection<String> collection) {
            for (String item : collection) {
                int pos = item.indexOf(':');
                if (pos > -1 && (pos = item.indexOf('=')) > -1) {
                    String key = item.substring(0, pos).trim();
                    String value = item.substring(pos + 1).trim();
                    if ("null".equals(value)) {
                        value = null;
                    }
                    add(new Pair<>(key, value));
                } else {
                    add(new Pair<>(item, ""));
                }
            }
        }
    }
}
