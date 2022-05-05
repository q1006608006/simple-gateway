package top.ivan.simple.gateway.core.selector;

import lombok.Data;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import top.ivan.simple.gateway.core.match.MatchCondition;

import java.util.List;

/**
 * @author Ivan
 * @description
 * @date 2021/1/8
 */
@Data
public class SelectorMetadata {
    private String routeId;
    private String description;
    private boolean patch;
    private List<MatchCondition> matches;

    public void extend(SelectorMetadata metadata) {
        if (CollectionUtils.isEmpty(matches)) {
            setMatches(metadata.getMatches());
        }
        if (!StringUtils.hasLength(routeId)) {
            setRouteId(metadata.getRouteId());
        }
        setPatch(patch);
        if (!StringUtils.hasLength(description)) {
            setDescription(metadata.description);
        }
    }
}
