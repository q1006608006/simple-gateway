package top.ivan.simple.gateway.core.selector.supporter;

import top.ivan.simple.gateway.core.GTConstant;
import top.ivan.simple.gateway.core.RequestSelector;
import top.ivan.simple.gateway.core.selector.impl.RedirectRequestSelector;
import org.springframework.stereotype.Component;

/**
 * @author Ivan
 * @since 2021/09/01 15:31
 */
@Component
public class RedirectSelectorSupporter extends AbsFiltersSelectorSupporter {

    @Override
    public RequestSelector resolve(String name) {
        return new RedirectRequestSelector(getNameMapping());
    }

    @Override
    public int order() {
        return GTConstant.REDIRECT_SELECTOR_ORDER;
    }
}
