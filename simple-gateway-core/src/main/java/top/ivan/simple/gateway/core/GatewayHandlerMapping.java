package top.ivan.simple.gateway.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author Ivan on 2019/11/15.
 * @version 1.0
 * 实现请求映射到方法的RequestMappingHandlerMapping
 */
@Slf4j
public class GatewayHandlerMapping extends RequestMappingHandlerMapping {

    @Autowired
    private GlobalExecuteController gec;

    @Autowired
    @Qualifier(GatewayConfiguration.GLOBAL_MATCHER)
    private UriMatcher uriMatcher;

    private HandlerMethod handlerMethod;

    @PostConstruct
    public void init() throws NoSuchMethodException {
        log.info("register GlobalExecuteController to '{}'", gec.getClass());
        Method method = GlobalExecuteController.class.getMethod("execute", byte[].class, HttpHeaders.class, HttpServletRequest.class, HttpServletResponse.class);
        this.handlerMethod = new HandlerMethod(gec, method);
    }

    @Override
    public HandlerMethod getHandlerInternal(HttpServletRequest req) throws Exception {
        return uriMatcher.match(req.getRequestURI()) ? handlerMethod : super.getHandlerInternal(req);
    }

}
