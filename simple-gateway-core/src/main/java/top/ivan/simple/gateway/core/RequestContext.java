package top.ivan.simple.gateway.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Builder;
import top.ivan.simple.gateway.core.WebRequest;
import lombok.Getter;
import lombok.Setter;
import top.ivan.simple.gateway.core.selector.SelectorContext;
import top.ivan.simple.gateway.core.selector.SelectorMetadata;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Ivan
 * @description
 * @date 2021/1/4
 */
@Getter
@Builder
public class RequestContext {
    @Setter
    private String routeId;

    private final String selectorId;
    private final SelectorMetadata selectorMetadata;

    @JsonIgnore
    private final HttpServletRequest httpRequest;
    @JsonIgnore
    private final HttpServletResponse httpResponse;
}
