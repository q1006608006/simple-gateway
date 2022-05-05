package top.ivan.simple.gateway.core;

import top.ivan.simple.gateway.core.tools.RedirectUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author Ivan
 * @description
 * @date 2020/5/21
 */
public interface WebRequest {

    String getUri();

    String getMethod();

    HttpHeaders getHeaders();

    byte[] getPostBody();

    <T> T getAttribute(Object key);

    boolean hasAttribute(Object key);

    void putAttribute(Object key, Object val);

    <T> T removeAttribute(Object key);

    boolean removeAttribute(String key, Object val);

    <T> T getByType(Class<T> type);

    <T> void putByType(Class<T> type, T val);

    RequestContext getContext();

    default String getRedirectUrl() throws BadRequestException {
        String url = getAttribute(GTConstant.REDIRECT_URL);
        if (null == url) {
            return resolveRedirectUrl();
        }
        return url;
    }

    default void setRedirectUrl(String url) {
        putAttribute(GTConstant.REDIRECT_URL, url);
    }

    default String resolveRedirectUrl() throws BadRequestException {
        return resolveRedirectUrl(getAttribute(GTConstant.REDIRECT_URL_PROPERTIES));
    }

    default String resolveRedirectUrl(Map<String, Object> config) throws BadRequestException {
        String url = RedirectUtils.resolveRedirectUrl(this, config);
        setRedirectUrl(url);
        return url;
    }

    default void setRedirectBody(byte[] body) {
        putAttribute(GTConstant.REDIRECT_BODY, body);
    }

    default void setRedirectBody(byte[] body, MediaType type) {
        putAttribute(GTConstant.REDIRECT_BODY, body);
        getRedirectHeaders().setContentType(type);
    }

    default byte[] getRedirectBody() {
        byte[] body = getAttribute(GTConstant.REDIRECT_BODY);
        if (null == body) {
            body = getPostBody();
            setRedirectBody(body);
        }
        return body;
    }

    default HttpHeaders getRedirectHeaders() {
        HttpHeaders redirectHeaders = getAttribute(GTConstant.REDIRECT_HEADERS);
        if (null == redirectHeaders) {
            HttpHeaders headers = new HttpHeaders();
            getHeaders().forEach((k, v) -> headers.put(k, new ArrayList<>(v)));
            setRedirectHeader(headers);
            return headers;
        }
        return redirectHeaders;
    }

    default void setRedirectHeader(HttpHeaders headers) {
        putAttribute(GTConstant.REDIRECT_HEADERS, headers);
    }

    default void addRedirectHeaderValue(String header, String value) {
        if (StringUtils.hasLength(value)) {
            getRedirectHeaders().add(header, value);
        }
    }

    default void addRedirectHeaderValues(String header, List<String> values) {
        getRedirectHeaders().addAll(header, values);
    }

    default void setRedirectHeader(String header, String value) {
        if (Objects.isNull(value)) {
            getRedirectHeaders().remove(header);
        } else {
            getRedirectHeaders().set(header, value);
        }
    }

    default void setRedirectHeaderValues(String header, List<String> values) {
        getRedirectHeaders().put(header, values);
    }

    default String getRedirectMethod() {
        String method = getAttribute(GTConstant.REDIRECT_METHOD);
        if (!StringUtils.hasLength(method)) {
            method = getMethod();
            setRedirectMethod(method);
        }
        return method;
    }

    default void setRedirectMethod(String method) {
        putAttribute(GTConstant.REDIRECT_METHOD, method);
    }

    default Object getRedirectRequestConfig() {
        return getAttribute(GTConstant.REDIRECT_REQUEST_CONFIG);
    }

    default void setRedirectRequestConfig(Object config) {
        putAttribute(GTConstant.REDIRECT_REQUEST_CONFIG, config);
    }

    default <T> T checkAttribute(Object key, Supplier<T> supplier) {
        if (!hasAttribute(key)) {
            putAttribute(key, supplier.get());
        }
        return getAttribute(key);
    }

}