package top.ivan.simple.gateway.core;

import top.ivan.simple.gateway.core.selector.SelectorMetadata;

/**
 * @author Ivan on 2019/11/18.
 * @version 1.0
 * <p>
 * 请求调用链选择器
 */
public interface RequestSelector {

    /**
     * 选择调用链
     *
     * @param request 请求
     * @return 对应的调用链
     * @throws RequestNotSupportException 符合选择条件但拒绝处理
     */
    RequestInvokeChain selectChain(WebRequest request) throws RequestNotSupportException;


    interface ConfigAware<T extends SelectorMetadata> {
        void setConfig(T config);

        Class<T> getConfigType();
    }

}
