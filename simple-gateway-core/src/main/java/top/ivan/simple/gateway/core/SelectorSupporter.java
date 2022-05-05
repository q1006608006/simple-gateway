package top.ivan.simple.gateway.core;

/**
 * @author Ivan
 * @since 2021/09/01 14:03
 */
public interface SelectorSupporter {

    RequestSelector resolve(String name);

    default int order() {
        return 0;
    }
}
