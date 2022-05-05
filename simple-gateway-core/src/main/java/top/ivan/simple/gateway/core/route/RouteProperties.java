package top.ivan.simple.gateway.core.route;

import lombok.Data;

import java.util.Map;

/**
 * @author Ivan
 * @since 2022/04/27 20:41
 */
@Data
public class RouteProperties {
    private String id;
    private String schema;
    private Map<String,String> properties;
}
