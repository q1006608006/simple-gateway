package top.ivan.simple.gateway.core.http;

import org.springframework.lang.Nullable;

import java.io.IOException;

/**
 * @author Ivan
 * @since 2021/09/15 13:53
 */
public interface HttpInvoker {
    HttpResponse invoke(HttpRequest request) throws IOException;

    HttpResponse invoke(HttpRequest request, int timeout) throws IOException;

    HttpResponse invoke(HttpRequest request, @Nullable Object conf) throws IOException;
}
