package top.ivan.simple.gateway.core;

import top.ivan.simple.gateway.core.route.UrlSchema;

import java.util.Map;

/**
 * @author Ivan
 * @description
 * @date 2021/1/4
 */
public interface RouteParser {

    String parse(WebRequest request, Map<String, Object> properties) throws BadRequestException;

    interface ConfigAware {
        void analyse(UrlSchema urlSchema);
    }
}
