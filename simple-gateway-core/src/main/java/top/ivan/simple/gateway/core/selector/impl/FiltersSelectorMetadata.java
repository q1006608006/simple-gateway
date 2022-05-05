package top.ivan.simple.gateway.core.selector.impl;

import top.ivan.simple.gateway.core.selector.SelectorMetadata;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class FiltersSelectorMetadata extends SelectorMetadata {
    private List<Map<String, String>> filters;
}
