package top.ivan.simple.gateway.core;

/**
 * @author Ivan
 * @since 2021/09/15 09:32
 */
public interface ErrorResponse {
    boolean hasBody();

    Object getBody();
}
