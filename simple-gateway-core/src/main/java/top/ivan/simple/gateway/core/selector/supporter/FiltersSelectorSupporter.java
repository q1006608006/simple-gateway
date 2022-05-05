package top.ivan.simple.gateway.core.selector.supporter;

import top.ivan.simple.gateway.core.GTConstant;
import top.ivan.simple.gateway.core.RequestSelector;
import top.ivan.simple.gateway.core.selector.impl.FiltersRequestSelector;
import org.springframework.stereotype.Component;

/**
 * @author Ivan
 * @since 2021/09/01 15:24
 */
@Component
public class FiltersSelectorSupporter extends AbsFiltersSelectorSupporter {

    @Override
    public RequestSelector resolve(String name) {
        if (name.startsWith("filters-")) {
            return new FiltersRequestSelector(getNameMapping());
        }
        return null;
    }

    @Override
    public int order() {
        return GTConstant.FILTERS_SELECTOR_ORDER;
    }
}
