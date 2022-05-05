package top.ivan.simple.gateway.core.selector.impl;

import top.ivan.simple.gateway.core.RequestFilter;
import top.ivan.simple.gateway.core.RequestInvokeChain;
import top.ivan.simple.gateway.core.RequestSelector;
import top.ivan.simple.gateway.core.WebRequest;
import top.ivan.simple.gateway.core.filter.FilterMetadata;
import top.ivan.simple.gateway.core.filter.FilterNameMapping;
import top.ivan.simple.gateway.core.selector.SimpleRequestInvokeChain;
import top.ivan.simple.gateway.core.util.Pair;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Ivan
 * @description
 * @date 2020/12/25
 */
public class FiltersRequestSelector implements RequestSelector, RequestSelector.ConfigAware<FiltersSelectorMetadata> {

    protected final FilterNameMapping filterNameMapping;

    protected SimpleRequestInvokeChain invokeChain;

    public FiltersRequestSelector(FilterNameMapping filterNameMapping) {
        this.filterNameMapping = filterNameMapping;
    }

    @Override
    public RequestInvokeChain selectChain(WebRequest request) {
        return invokeChain;
    }

    @Override
    public void setConfig(FiltersSelectorMetadata config) {
        init(config.getFilters());
    }

    protected void init(List<Map<String, String>> filterConfigs) {
        if (CollectionUtils.isEmpty(filterConfigs)) {
            throw new IllegalArgumentException("filters不允许为空");
        }
        List<Pair<RequestFilter, FilterMetadata>> filterList = filterConfigs.stream().map(map -> {
            String id = Optional.ofNullable(map.get("id")).map(Object::toString).orElseThrow(() -> new IllegalArgumentException("filter id must be set"));
            RequestFilter filter = filterNameMapping.getFilter(id);
            if (null == filter) {
                throw new RuntimeException("Filter not found: " + map.get("id"));
            }
            FilterMetadata metadata = new FilterMetadata(map);
            return new Pair<>(filter, metadata);
        }).collect(Collectors.toList());
        invokeChain = SimpleRequestInvokeChain.fromFilterMetadata(filterList);
    }

    @Override
    public Class<FiltersSelectorMetadata> getConfigType() {
        return FiltersSelectorMetadata.class;
    }

}
