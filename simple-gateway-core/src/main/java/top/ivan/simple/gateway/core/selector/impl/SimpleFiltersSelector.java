package top.ivan.simple.gateway.core.selector.impl;

import top.ivan.simple.gateway.core.RequestFilter;
import top.ivan.simple.gateway.core.RequestInvokeChain;
import top.ivan.simple.gateway.core.RequestSelector;
import top.ivan.simple.gateway.core.WebRequest;
import top.ivan.simple.gateway.core.filter.FilterNameMapping;
import top.ivan.simple.gateway.core.selector.SelectorMetadata;
import top.ivan.simple.gateway.core.selector.SimpleRequestInvokeChain;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author Ivan
 * @since 2021/09/01 00:34
 */
public class SimpleFiltersSelector implements RequestSelector, RequestSelector.ConfigAware<SimpleFiltersSelector.FilterList> {

    @Autowired
    private FilterNameMapping filterNameMapping;

    public SimpleFiltersSelector() {
    }

    public SimpleFiltersSelector(FilterNameMapping filterNameMapping) {
        this.filterNameMapping = filterNameMapping;
    }

    public SimpleFiltersSelector(List<RequestFilter> filters) {
        support(SimpleRequestInvokeChain.fromFilters(filters));
    }

    private Supplier<RequestInvokeChain> chainSupplier = () -> {
        throw new IllegalStateException("Selector is not initialization");
    };

    @Override
    public RequestInvokeChain selectChain(WebRequest request) {
        return chainSupplier.get();
    }

    @Override
    public void setConfig(FilterList config) {
        if (CollectionUtils.isEmpty(config.filters)) {
            throw new IllegalArgumentException("field 'filters' must be not empty");
        }
        List<RequestFilter> filterList = new ArrayList<>();
        for (String filterId : config.getFilters()) {
            RequestFilter filter = filterNameMapping.getFilter(filterId);
            if (null == filter) {
                throw new IllegalArgumentException("no found RequestFilter with id: " + filterId);
            }
            filterList.add(filter);
        }
        support(SimpleRequestInvokeChain.fromFilters(filterList));
    }

    @Override
    public Class<FilterList> getConfigType() {
        return FilterList.class;
    }

    @Data
    public static class FilterList extends SelectorMetadata {
        List<String> filters;
    }

    private void support(RequestInvokeChain chain) {
        this.chainSupplier = () -> chain;
    }

}
