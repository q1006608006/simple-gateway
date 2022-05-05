package top.ivan.simple.gateway.core;

import top.ivan.simple.gateway.core.http.HttpCode;
import top.ivan.simple.gateway.core.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ivan on 2019/11/20.
 * @version 1.0
 * <p>
 * 全局的异常处理建议
 */
public interface GlobalExecuteAdvice {
    ChainExceptionHandler DEFAULT_HANDLER = new EasyChainExceptionHandler();

    /**
     * 全局异常处理类
     */
    @Slf4j
    class EasyChainExceptionHandler implements ChainExceptionHandler {

        @Override
        public HttpResponse handle(WebRequest request, Throwable cause) {
            if (cause instanceof BadRequestException) {
                log.error(String.format("请求异常(%s)：{}", request.getUri()), cause);
            } else if (cause instanceof BadGatewayException) {
                log.error("服务异常: " + cause.getMessage(), cause);
            } else {
                log.error("未知异常: " + cause.getMessage(), cause);
            }

            int code = 500;
            if (cause instanceof HttpCode) {
                code = ((HttpCode) cause).getHttpCode();
            }

            Map<String, Object> info = new HashMap<>();
            info.put("time", LocalDateTime.now());
            info.put("message", cause.getMessage());
            info.put("status", code);

            return HttpResponse.build(code, info);
        }
    }

    /**
     * 调用选择、解析对象异常处理
     *
     * @return handler
     */
    default ChainExceptionHandler getPrepareIllegalHandler() {
        return DEFAULT_HANDLER;
    }

    /**
     * 调用执行异常处理
     *
     * @return handler
     */
    default ChainExceptionHandler getGlobalExceptionHandler() {
        return DEFAULT_HANDLER;
    }

    /**
     * 默认的、未找到处理链的全局处理
     *
     * @return RequestInvokeChain
     */
    default RequestInvokeChain getNoneSelectChain() {
        return (request -> {
            HttpServletResponse rsp = request.getContext().getHttpResponse();
            HttpServletRequest req = request.getContext().getHttpRequest();
            rsp.setStatus(HttpStatus.NOT_FOUND.value());

            Map<String, Object> info = new HashMap<>();
            String uri = req.getRequestURI();

            info.put("time", LocalDateTime.now());
            info.put("path", uri);
            info.put("message", "not service for '" + uri + "'");
            info.put("code", HttpStatus.NOT_FOUND.value());
            return HttpResponse.build(HttpStatus.NOT_FOUND.value(), info);
        });
    }

}
