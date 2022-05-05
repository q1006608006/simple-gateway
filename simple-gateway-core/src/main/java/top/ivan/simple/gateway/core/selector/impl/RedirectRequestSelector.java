package top.ivan.simple.gateway.core.selector.impl;

import top.ivan.simple.gateway.core.GTConstant;
import top.ivan.simple.gateway.core.RequestInvokeChain;
import top.ivan.simple.gateway.core.RequestSelector;
import top.ivan.simple.gateway.core.WebRequest;
import top.ivan.simple.gateway.core.filter.FilterNameMapping;
import top.ivan.simple.gateway.core.filter.impl.GTRedirectFilter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author Ivan
 * @description
 * @date 2020/12/24
 */
public class RedirectRequestSelector implements RequestSelector, RequestSelector.ConfigAware<RedirectRequestSelector.RedirectMetadata> {

    private final FiltersRequestSelector selector;
    private final FilterNameMapping filterNameMapping;

    public RedirectRequestSelector(FilterNameMapping filterNameMapping) {
        this.filterNameMapping = filterNameMapping;
        this.selector = new FiltersRequestSelector(filterNameMapping);
    }

    @Override
    public void setConfig(RedirectMetadata config) {
        if (config.isDisableRedirect()) {
            selector.init(config.getFilters());
            return;
        }

        List<Map<String, String>> filtersList = config.getFilters();
        boolean hasRedirectFilter = false;

        if (!CollectionUtils.isEmpty(filtersList)) {
            for (Map<String, String> metadata : filtersList) {
                String filterId = Optional.ofNullable(metadata.get("id")).map(Object::toString).orElseThrow(() -> new IllegalArgumentException("filter-id must be set"));
                if (filterNameMapping.getFilter(filterId) instanceof GTRedirectFilter) {
                    hasRedirectFilter = true;
                    break;
                }
            }
        } else {
            filtersList = new ArrayList<>();
        }

        if (!hasRedirectFilter) {
            filtersList = new ArrayList<>(filtersList);
            filtersList.add(Collections.singletonMap("id", GTConstant.REDIRECT_FILTER_NAME));
        }

        selector.init(filtersList);
    }

    @Override
    public Class<RedirectMetadata> getConfigType() {
        return RedirectMetadata.class;
    }

    @Override
    public RequestInvokeChain selectChain(WebRequest request) {
        return selector.selectChain(request);
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class RedirectMetadata extends FiltersSelectorMetadata {
        private boolean disableRedirect = false;
    }
}
