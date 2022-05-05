package top.ivan.simple.gateway.core.route;

import org.springframework.util.StringUtils;
import top.ivan.simple.gateway.core.RouteParser;
import top.ivan.simple.gateway.core.WebRequest;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author Ivan
 * @description
 * @date 2021/1/6
 */
public class SimpleRouteParser implements RouteParser {

    private final String prefix;
    private final String parameters;
    private final String rootPath;

    public SimpleRouteParser(UrlSchema host) {
        String proto = host.getProtocol();
        if (!"http".equals(proto) && !"https".equals(proto)) {
            proto = "http";
        }
        this.prefix = proto + "://" + host.getHost();
        String path = StringUtils.hasLength(host.getPath()) ? host.getPath() : "";
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 2);
        }
        this.rootPath = path;
        this.parameters = StringUtils.hasLength(host.getQueryString()) ? host.getQueryString() : "";
    }

    @Override
    public String parse(WebRequest request, Map<String, Object> properties) {
        if (Objects.isNull(properties)) {
            properties = Collections.emptyMap();
        }
        StringBuilder builder = new StringBuilder(prefix).append(rootPath);
        if (!getBoolean("alias", false, properties)) {
            builder.append(request.getUri());
        }

        boolean apSign = false;
        if (getBoolean("queryString", true, properties)) {
            String qs = request.getContext().getHttpRequest().getQueryString();
            if (StringUtils.hasLength(qs)) {
                builder.append("?").append(qs);
                apSign = true;
            }
        }
        if (getBoolean("queryStatic", true, properties) && StringUtils.hasLength(parameters)) {
            builder.append(apSign ? "&" : "?").append(parameters);
        }

        return builder.toString();
    }

    public boolean getBoolean(String key, boolean defVal, Map<String, Object> props) {
        return (boolean) props.getOrDefault(key, defVal);
    }

}
