package top.ivan.simple.gateway.core.selector.supporter;

import top.ivan.simple.gateway.core.SelectorSupporter;
import top.ivan.simple.gateway.core.filter.FilterNameMapping;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Ivan
 * @since 2021/09/01 15:33
 */
public abstract class AbsFiltersSelectorSupporter implements SelectorSupporter {
    @Autowired
    protected FilterNameMapping nameMapping;

    public FilterNameMapping getNameMapping() {
        return nameMapping;
    }
}
