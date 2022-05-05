package top.ivan.simple.gateway.core;

import top.ivan.simple.gateway.core.selector.SelectorContext;

import java.util.List;

/**
 * @author Ivan
 * @since 2021/09/01 14:27
 */
public interface GatewayConfigurer {
    default void configureSelectors(List<SelectorContext> contexts) {
    }

    default void configureSelectorSupporters(List<SelectorSupporter> supporters) {
    }

    default void configureRouteManager(RouteManager manager) {
    }
}
