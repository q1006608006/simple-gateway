package top.ivan.simple.gateway.core;

/**
 * @author Ivan
 * @description
 * @date 2021/1/8
 */
public interface RequestMatcher {

    boolean match(WebRequest request);

    static RequestMatcher allPermit() {
        return r -> true;
    }

    static RequestMatcher allConflict() {
        return r -> false;
    }
}
