package top.ivan.simple.gateway.core.route;

import top.ivan.simple.gateway.core.annotation.ParserId;
import top.ivan.simple.gateway.core.tools.SpringAnnotationExUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import top.ivan.simple.gateway.core.*;

import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Ivan
 * @description
 * @date 2020/12/29
 */
@Component
public class AutoRouteManagerConfig {

    @Autowired
    private GatewayProperties gtProp;

    @Autowired
    private void init(Environment env, ApplicationContext context, @Autowired(required = false) Map<String, RouteParser> parserMap, GatewayConfigurer configurer) {
        parserMap = Objects.isNull(parserMap) ? Collections.emptyMap() : parserMap;

        String selectedRouteManager = gtProp.getRouteManager();
        selectedRouteManager = StringUtils.hasLength(selectedRouteManager) ? selectedRouteManager : GTConstant.DEFAULT_ROUTE_MANAGER_ID;

        if (!context.containsBean(selectedRouteManager)) {
            throw new RuntimeException("there is no routeManager named '" + selectedRouteManager + "'");
        }
        RouteManager routeManager = (RouteManager) context.getBean(selectedRouteManager);
        List<RouteProperties> baseList = gtProp.getRoutes();
        List<UrlSchema> hostList = baseList.stream().map(bc -> {
            if (!StringUtils.hasLength(bc.getId()) || !StringUtils.hasLength(bc.getSchema())) {
                throw new IllegalArgumentException("config not found 'anicert.gateway.routes.[*].[id|schema]'");
            }
            try {
                UrlSchema info = UrlSchema.parse(bc.getSchema());
                info.setId(bc.getId());
                return info;
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(e);
            }
        }).collect(Collectors.toList());

        Map<String, UrlSchema> hostInfoMap = hostList.stream().collect(Collectors.toMap(UrlSchema::getId, h -> h));
        Map<String, RouteParser> fullMap = new HashMap<>(parserMap);
        parserMap.forEach((bn, parser) -> {
            String alias = SpringAnnotationExUtils.findAnnotationValue(ParserId.class, "value", parser, context, bn, bn);
            if (StringUtils.hasLength(alias)) {
                fullMap.put(alias, parser);
            }
            if (parser instanceof RouteParser.ConfigAware) {
                UrlSchema info = getHostInfo(alias, bn, hostInfoMap);
                ((RouteParser.ConfigAware) parser).analyse(info);
            }
        });
        routeManager.init(hostList, fullMap);
        configurer.configureRouteManager(routeManager);
    }

    private static UrlSchema getHostInfo(String id, String beanName, Map<String, UrlSchema> infoMap) {
        UrlSchema info = infoMap.get(id);
        if (Objects.isNull(info)) {
            info = infoMap.get(beanName);
        }
        return info;
    }

}
