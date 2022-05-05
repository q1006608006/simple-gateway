package top.ivan.simple.gateway.core;

/**
 * @author Ivan
 * @description
 * @date 2020/12/28
 */
public class GTConstant {
    private GTConstant() {
    }

    public static final String GATEWAY_PROPERTIES_PREFIX = "gateway";
    public static final String GATEWAY_ENABLE = GATEWAY_PROPERTIES_PREFIX + ".enable";

    public static final String REDIRECT_FILTER_NAME = "gt-redirect";
    public static final String HOST_FILTER_NAME = "gt-host";

    public static final String REDIRECT_BODY = "GT_REDIRECT_BODY";
    public static final String REDIRECT_URL = "GT_REDIRECT_URL";
    public static final String REDIRECT_URL_PROPERTIES = "REDIRECT_URL_PROPERTIES";
    public static final String REDIRECT_HEADERS = "GT_REDIRECT_HEADERS";
    public static final String REDIRECT_METHOD = "GT_REDIRECT_METHOD";

    public static final String REDIRECT_REQUEST_CONFIG = "GT_REDIRECT_CONFIG";

    public static final String UNAVAILABLE_SELECTOR = "unavailable";
    public static final String UNKNOWN_HOST = "Unknown Host";

    public static final int DEFAULT_URL_CACHE_MAX = 4096;

    public static final int FILTERS_SELECTOR_ORDER = Integer.MAX_VALUE - 1;
    public static final int SIMPLE_FILTERS_SELECTOR_ORDER = FILTERS_SELECTOR_ORDER;
    public static final int REDIRECT_SELECTOR_ORDER = Integer.MAX_VALUE;

    public static final String DEFAULT_ROUTE_MANAGER_ID = "GT_ROUTE_MANAGER";
}
