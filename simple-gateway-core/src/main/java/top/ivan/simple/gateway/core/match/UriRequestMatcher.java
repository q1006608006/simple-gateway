package top.ivan.simple.gateway.core.match;

import top.ivan.simple.gateway.core.RequestMatcher;
import top.ivan.simple.gateway.core.UriMatcher;
import top.ivan.simple.gateway.core.WebRequest;

/**
 * @author Ivan
 * @since 2021/09/01 10:02
 */
public class UriRequestMatcher implements RequestMatcher {
    private final UriMatcher matcher;

    public UriRequestMatcher(UriMatcher uriMatcher) {
        this.matcher = uriMatcher;
    }

    @Override
    public boolean match(WebRequest request) {
        return matcher.match(request.getUri());
    }
}
