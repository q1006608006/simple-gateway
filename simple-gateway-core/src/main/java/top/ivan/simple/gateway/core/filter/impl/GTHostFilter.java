package top.ivan.simple.gateway.core.filter.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import top.ivan.simple.gateway.core.*;
import top.ivan.simple.gateway.core.annotation.FilterId;
import top.ivan.simple.gateway.core.filter.FilterMetadata;
import top.ivan.simple.gateway.core.filter.PreFilter;
import top.ivan.simple.gateway.core.route.UrlSchema;
import top.ivan.simple.gateway.core.util.ChainLogger;

import java.net.URISyntaxException;
import java.util.Locale;

/**
 * @author Ivan
 * @since 2022/04/27 17:03
 */
@Slf4j
@FilterId(GTConstant.HOST_FILTER_NAME)
@Component
public class GTHostFilter implements PreFilter {

    @Autowired
    private RouteManager routeManager;

    /**
     * Host（HTTP头部）来源，默认值"route"
     */
    private static final String PARAM_CHECK_HOST = "check";

    /**
     * 是否覆盖原有host，默认值"true"
     */
    private static final String PARAM_OVERRIDE = "override";

    @Override
    public void prepare(WebRequest request, FilterMetadata metadata) throws BadRequestException {
        boolean override = metadata.readWithType(PARAM_OVERRIDE, Boolean.class, v -> !"false".equalsIgnoreCase(v));
        if (!override && request.getRedirectHeaders().containsKey(HttpHeaders.HOST)) {
            return;
        }

        String host = metadata.getOrDefault(PARAM_CHECK_HOST, "route").toLowerCase(Locale.ENGLISH);
        switch (host) {
            case "route":
                UrlSchema info = routeManager.getHostInfo(request.getContext().getRouteId());
                if (null == info) {
                    throw new BadGatewayException(GTConstant.UNKNOWN_HOST);
                }
                host = info.getHost();
                break;
            case "url":
                String url = request.getRedirectUrl();
                try {
                    host = UrlSchema.parse(url).getHost();
                    break;
                } catch (URISyntaxException e) {
                    throw new BadGatewayException(GTConstant.UNKNOWN_HOST + ": " + url);
                }
        }

        ChainLogger.logDebug("[gt-host] set host: '{}'", host);
        request.setRedirectHeader(HttpHeaders.HOST, host);
    }
}
