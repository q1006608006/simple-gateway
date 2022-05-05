package top.ivan.simple.gateway.core.tools;

import top.ivan.simple.gateway.core.BadRequestException;
import top.ivan.simple.gateway.core.GTConstant;
import top.ivan.simple.gateway.core.RouteManager;
import top.ivan.simple.gateway.core.WebRequest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @author Ivan
 * @description
 * @date 2021/1/5
 */
public class RedirectUtils {
    private static RouteManager routeManager;

    @Autowired
    public void setRouteManager(RouteManager routeManager) {
        RedirectUtils.routeManager = routeManager;
    }

    public static String resolveRedirectUrl(WebRequest request) throws BadRequestException {
        return routeManager.resolveUrl(request.getContext().getRouteId(), request, request.getAttribute(GTConstant.REDIRECT_URL_PROPERTIES));
    }

    public static String resolveRedirectUrl(WebRequest request, Map<String, Object> config) throws BadRequestException {
        return routeManager.resolveUrl(request.getContext().getRouteId(), request, config);
    }

}
