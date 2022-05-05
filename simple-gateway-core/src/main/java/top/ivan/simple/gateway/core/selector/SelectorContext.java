package top.ivan.simple.gateway.core.selector;

import top.ivan.simple.gateway.core.RequestMatcher;
import top.ivan.simple.gateway.core.RequestSelector;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SelectorContext {
    private String beanName;
    private String selectorId;
    private RequestSelector selector;
    private RequestMatcher matcher;
    private String routeId;
    private SelectorMetadata metadata;
}