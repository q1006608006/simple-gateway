package top.ivan.simple.gateway.core.http;

import lombok.Data;
import org.springframework.http.HttpHeaders;

import java.io.Serializable;

/**
 * @author Ivan
 * @description
 * @date 2020/12/23
 */
@Data
public class HttpResponse implements Serializable {
    private static final long serialVersionUID = 1905122041950251207L;

    private int code = 200;

    private HttpHeaders headers;

    private Object body;

    private HttpResponse() {
    }

    public static HttpResponse ok(Object body) {
        return build(200, null, body);
    }

    public static HttpResponse ok(HttpHeaders headers, Object body) {
        return build(200, headers, body);
    }

    public static HttpResponse build(int code, Object body) {
        return build(code, null, body);
    }

    public static HttpResponse build(int code, HttpHeaders headers, Object body) {
        HttpResponse resp = new HttpResponse();
        resp.setCode(code);
        if (null == headers) {
            resp.setHeaders(new HttpHeaders());
        } else {
            resp.setHeaders(headers);
        }
        resp.setBody(body);
        return resp;
    }
}
