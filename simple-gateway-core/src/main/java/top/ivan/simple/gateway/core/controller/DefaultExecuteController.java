package top.ivan.simple.gateway.core.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import top.ivan.simple.gateway.core.*;
import top.ivan.simple.gateway.core.converter.ChunkedMessageConverter;
import top.ivan.simple.gateway.core.http.HttpResponse;
import top.ivan.simple.gateway.core.selector.AutoSelectorConfig;
import top.ivan.simple.gateway.core.selector.SelectorContext;
import top.ivan.simple.gateway.core.util.ChainLogger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Ivan on 2019/11/15.
 * @version 1.0
 * <p>
 * 所有请求的入口控制器
 */
@Slf4j
public class DefaultExecuteController implements GlobalExecuteController {
    private static final Logger ACCESS_LOG = LoggerFactory.getLogger("access");

    private static final Set<String> ignoreHeaders;

    static {
        ignoreHeaders = new TreeSet<>();
        ignoreHeaders.add("date");
        ignoreHeaders.add("connection");
        ignoreHeaders.add("transfer-encoding");
        ignoreHeaders.add("content-length");
        ignoreHeaders.add("keep-alive");
    }

    //selector注入器，用于遍历selector
    private AutoSelectorConfig autoSelectorConfig;

    //未找到调用链的默认执行链
    private RequestInvokeChain noneSelectChain;

    //选择链、格式化异常处理handler
    private ChainExceptionHandler prepareIllegalHandler;

    //请求执行过程中的异常处理handler
    private ChainExceptionHandler exceptionHandler;

    //chunked形式返回需要用到converter
    private ChunkedMessageConverter chunkedMessageConverter;

    @Autowired
    public void autoWireBean(AutoSelectorConfig config, GlobalExecuteAdvice advice, ChunkedMessageConverter chunkedMessageConverter) {
        this.autoSelectorConfig = config;
        this.noneSelectChain = advice.getNoneSelectChain();
        this.prepareIllegalHandler = advice.getPrepareIllegalHandler();
        this.exceptionHandler = advice.getGlobalExceptionHandler();
        this.chunkedMessageConverter = chunkedMessageConverter;
    }

    /**
     * 所有请求的入口
     *
     * @param body 请求体
     * @param req  ''
     * @param rsp  ''
     * @return 返回结果, 同RestController的接口
     * @throws Exception 未能捕获的异常,建议能处理的尽量处理,因为更外层的异常处理是spring-web的dispatcher
     */
    @Override
    public Object execute(@RequestBody(required = false) byte[] body, @RequestHeader HttpHeaders headers, HttpServletRequest req, HttpServletResponse rsp) throws Exception {
        ChainLogger.logStart();
        //获取selector列表（有序的）
        List<SelectorContext> selectorContexts = autoSelectorConfig.getSelectorOrderList();
        RequestInvokeChain chain = null;

        String uri = req.getRequestURI();
        ACCESS_LOG.debug("receive [{}] request: '{}'", req.getMethod(), uri);

        DefaultWebRequest request = DefaultWebRequest.builder()
                .postBody(body)
                .headers(headers)
                .uri(uri)
                .method(req.getMethod())
                .build();

        try {
            //遍历selector列表,以找到合适的调用链
            for (SelectorContext context : selectorContexts) {
                if (context.getMatcher().match(request)) {
                    chain = context.getSelector().selectChain(request);
                    if (chain != null) {
                        RequestContext reqCtx = RequestContext.builder()
                                .routeId(context.getRouteId())
                                .selectorId(context.getSelectorId())
                                .selectorMetadata(context.getMetadata())
                                .httpRequest(req)
                                .httpResponse(rsp)
                                .build();
                        request.setContext(reqCtx);
                        break;
                    }
                }
            }
        } catch (Throwable cause) {
            //异常处理
            return toEntity(prepareIllegalHandler.handle(request, cause));
        }

        //未找到处理链
        if (chain == null) {
            ChainLogger.logError("调用链匹配失败(" + uri + ")");
            return toEntity(noneSelectChain.doChain(request));
        }

        try {
            //执行调用链并返回
            HttpResponse result = chain.doChain(request);

            //返回结果
            return toEntity(result);
        } catch (Throwable e) {
            //异常处理
            return toEntity(exceptionHandler.handle(request, e));
        } finally {
            //finally
            ChainLogger.logEnd();
        }
    }

    public ResponseEntity<Object> toEntity(HttpResponse response) {
        if (response.getBody() instanceof ResponseEntity) {
            return (ResponseEntity<Object>) response.getBody();
        }
        HttpHeaders responseHeaders = response.getHeaders();
        formatHeaders(responseHeaders);
        return ResponseEntity.status(response.getCode()).headers(responseHeaders).body(response.getBody());
    }

    public void formatHeaders(HttpHeaders headers) {
        if (headers.isEmpty()) {
            return;
        }

        ignoreHeaders.forEach(headers::remove);

        if (headers.containsKey("transfer-encoding")) {
            chunkedMessageConverter.setReturnLength(false);
        }

    }
}
