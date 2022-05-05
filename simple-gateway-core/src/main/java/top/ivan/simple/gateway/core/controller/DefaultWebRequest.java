package top.ivan.simple.gateway.core.controller;

import lombok.AccessLevel;
import lombok.Setter;
import top.ivan.simple.gateway.core.WebRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import top.ivan.simple.gateway.core.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@Getter
@Builder(builderClassName = "WebRequestBuilder")
public class DefaultWebRequest implements WebRequest {
    @JsonIgnore
    private final Map<Object, Object> attributes = new HashMap<>();

    @JsonIgnore
    @Setter(AccessLevel.PACKAGE)
    private RequestContext context;

    private final String uri;
    private final String method;
    private final HttpHeaders headers;
    private final byte[] postBody;

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(Object key) {
        return (T) attributes.get(key);
    }

    @Override
    public boolean hasAttribute(Object key) {
        return attributes.containsKey(key);
    }

    @Override
    public void putAttribute(Object key, Object val) {
        attributes.put(key, val);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getByType(Class<T> type) {
        return (T) attributes.get(type);
    }

    @Override
    public <T> void putByType(Class<T> type, T val) {
        attributes.put(type, val);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T removeAttribute(Object key) {
        return (T) attributes.remove(key);
    }

    @Override
    public boolean removeAttribute(String key, Object val) {
        return attributes.remove(key, val);
    }

}
