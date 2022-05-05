package top.ivan.simple.gateway.core.filter;

import top.ivan.simple.gateway.core.RequestFilter;
import top.ivan.simple.gateway.core.annotation.FilterId;
import top.ivan.simple.gateway.core.tools.SpringAnnotationExUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ivan on 2019/11/20.
 * @version 1.0
 * <p>
 * filter的名字映射服务,默认为spring容器中的beanId，可以通过注解@cn.anicert.gateway.core.annotation.Filter指定
 */
@Component
public class FilterNameMapping {
    private final Map<String, RequestFilter> filterMap = new HashMap<>();

    /**
     * 根据指定name获取filter
     *
     * @param name ''
     * @return RequestFilter
     */
    public RequestFilter getFilter(String name) {
        return filterMap.get(name);
    }

    @Autowired(required = false)
    private void setFilterMap(ApplicationContext context, Map<String, RequestFilter> filterMap) {
        Map<String, RequestFilter> nameMap = new HashMap<>();
        filterMap.forEach((bn, filter) -> {
            String filterId = SpringAnnotationExUtils.findAnnotationValue(FilterId.class, "value", filter, context, bn, bn);
            if (StringUtils.hasLength(filterId)) {
                nameMap.put(filterId, filter);
            }
        });
        this.filterMap.putAll(filterMap);
        this.filterMap.putAll(nameMap);
    }
}
