package top.ivan.simple.gateway.core.selector;

import top.ivan.simple.gateway.core.GatewayConfigurer;
import top.ivan.simple.gateway.core.RequestSelector;
import top.ivan.simple.gateway.core.SelectorSupporter;
import top.ivan.simple.gateway.core.filter.FilterNameMapping;
import top.ivan.simple.gateway.core.selector.impl.RedirectRequestSelector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Ivan
 * @since 2021/09/01 15:07
 */
@Component
public class SelectorFactory {
    private final List<SelectorSupporter> supporters;
    private final FilterNameMapping filterNameMapping;

    public SelectorFactory(@Autowired(required = false) List<SelectorSupporter> supporters, GatewayConfigurer configurer, FilterNameMapping filterNameMapping) {
        if (CollectionUtils.isEmpty(supporters)) {
            supporters = Collections.emptyList();
        } else {
            supporters.sort(Comparator.comparingInt(SelectorSupporter::order));
        }
        configurer.configureSelectorSupporters(supporters);
        this.supporters = supporters;
        this.filterNameMapping = filterNameMapping;
    }

    @NonNull
    public RequestSelector resolve(String selectorId) {
        RequestSelector selector;
        for (SelectorSupporter supporter : supporters) {
            if ((selector = supporter.resolve(selectorId)) != null) {
                return selector;
            }
        }

        return new RedirectRequestSelector(filterNameMapping);
    }
}
