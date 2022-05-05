package top.ivan.simple.gateway.core.selector.supporter;

import top.ivan.simple.gateway.core.GTConstant;
import top.ivan.simple.gateway.core.RequestSelector;
import top.ivan.simple.gateway.core.selector.impl.SimpleFiltersSelector;
import org.springframework.stereotype.Component;

/**
 * @author Ivan
 * @since 2021/09/01 15:27
 */
@Component
public class SimpleFiltersSelectorSupporter extends AbsFiltersSelectorSupporter {

    @Override
    public RequestSelector resolve(String name) {
        if (name.startsWith("simple-")) {
            return new SimpleFiltersSelector(getNameMapping());
        }
        return null;
    }

    @Override
    public int order() {
        return GTConstant.SIMPLE_FILTERS_SELECTOR_ORDER;
    }
}
