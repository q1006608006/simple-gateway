package top.ivan.simple.gateway.core;

import top.ivan.simple.gateway.core.route.UrlSchema;

import java.util.List;
import java.util.Map;

/**
 * @author Ivan
 * @description
 * @date 2020/12/29
 */
public interface RouteManager {

    void init(List<UrlSchema> hostList, Map<String, RouteParser> parserMap);

    String resolveUrl(String routeId, WebRequest request, Map<String, Object> properties) throws BadRequestException;

    void registerRoute(String routeId, UrlSchema urlSchema, RouteParser parser);

    UrlSchema getHostInfo(String routeId);
}
