package top.ivan.simple.gateway.core;

import top.ivan.simple.gateway.core.route.RouteProperties;
import top.ivan.simple.gateway.core.selector.SelectorMetadata;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Ivan
 * @since 2021/09/01 17:28
 */
@Configuration
@ConfigurationProperties(prefix = GTConstant.GATEWAY_PROPERTIES_PREFIX)
@Data
public class GatewayProperties {

    private boolean enable = true;

    private List<String> matches = Collections.singletonList("/*");

    private String configLocation;

    private String routeManager = GTConstant.DEFAULT_ROUTE_MANAGER_ID;

    private List<RouteProperties> routes;

    private Map<String, SelectorMetadata> selector;

}
