package top.ivan.simple.gateway.core.selector.impl;

import top.ivan.simple.gateway.core.BadRequestException;
import top.ivan.simple.gateway.core.RequestInvokeChain;
import top.ivan.simple.gateway.core.RequestSelector;
import top.ivan.simple.gateway.core.WebRequest;

/**
 * @author Ivan
 * @description
 * @date 2020/12/25
 */
public class UnavailableRequestSelector implements RequestSelector {

    @Override
    public RequestInvokeChain selectChain(WebRequest request) {
        return req -> {
            throw new BadRequestException();
        };
    }
}
