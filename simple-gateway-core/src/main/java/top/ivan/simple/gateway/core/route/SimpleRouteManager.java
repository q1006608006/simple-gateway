package top.ivan.simple.gateway.core.route;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import top.ivan.simple.gateway.core.BadRequestException;
import top.ivan.simple.gateway.core.RouteManager;
import top.ivan.simple.gateway.core.RouteParser;
import top.ivan.simple.gateway.core.WebRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Ivan
 * @description
 * @date 2020/12/29
 */
@Slf4j
public class SimpleRouteManager implements RouteManager {

    private Map<String, RouteParser> functionMap;
    private Map<String, UrlSchema> infoMap;
    private String defaultParserId;

    @Override
    public void init(List<UrlSchema> hostList, Map<String, RouteParser> parserMap) {
        this.functionMap = new HashMap<>();
        this.infoMap = new HashMap<>();

        hostList.forEach(host -> {
            String routeId = host.getId();
            infoMap.put(routeId, host);

            if (parserMap.containsKey(host.getId())) {
                functionMap.put(routeId, parserMap.get(routeId));
            } else {
                functionMap.put(routeId, new SimpleRouteParser(host));
            }
        });
        if (!hostList.isEmpty()) {
            defaultParserId = hostList.get(0).getId();
            log.debug("set default RouteId: {}", defaultParserId);
        } else {
            defaultParserId = UUID.randomUUID().toString().replace("-", "");
            functionMap.put(defaultParserId, (s, p) -> null);
        }
    }

    @Override
    public String resolveUrl(String routeId, WebRequest request, Map<String, Object> properties) throws BadRequestException {
        if (!StringUtils.hasLength(routeId)) {
            routeId = defaultParserId;
        }
        RouteParser parser = functionMap.get(routeId);
        if (null == parser) {
            throw new RuntimeException("Route无法解析");
        }
        return parser.parse(request, properties);
    }

    @Override
    public void registerRoute(String routeId, UrlSchema urlSchema, RouteParser parser) {
        if (null != parser) {
            functionMap.put(routeId, parser);
        }
        if (null != urlSchema) {
            infoMap.put(routeId, urlSchema);
            if (parser == null) {
                functionMap.put(routeId, new SimpleRouteParser(urlSchema));
            }
        }
    }

    @Override
    public UrlSchema getHostInfo(String routeId) {
        return infoMap.get(routeId);
    }

}
