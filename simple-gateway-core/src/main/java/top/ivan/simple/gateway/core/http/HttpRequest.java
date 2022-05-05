package top.ivan.simple.gateway.core.http;

import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.Locale;

/**
 * @author Ivan
 * @description
 * @date 2020/12/23
 */
@Data
public class HttpRequest {

    private String url;

    private HttpMethod method;

    private HttpHeaders headers;

    private byte[] body;

    public HttpRequest(String url, HttpMethod method) {
        this.url = url;
        this.method = method;
    }

    public MediaType getContentType() {
        return getHeaders().getContentType();
    }

    public HttpHeaders getHeaders() {
        if (null == headers) {
            headers = new HttpHeaders();
        }
        return headers;
    }

    public HttpRequest(String url, String method) {
        HttpMethod m;
        switch (method.toUpperCase(Locale.ENGLISH)) {
            case "POST":
                m = HttpMethod.POST;
                break;
            case "DELETE":
                m = HttpMethod.DELETE;
                break;
            case "PUT":
                m = HttpMethod.PUT;
                break;
            case "HEAD":
                m = HttpMethod.HEAD;
                break;
            case "OPTIONS":
                m = HttpMethod.OPTIONS;
                break;
            case "PATCH":
                m = HttpMethod.PATCH;
                break;
            case "TRACE":
                m = HttpMethod.TRACE;
                break;
            default:
                m = HttpMethod.GET;
                break;
        }
        this.url = url;
        this.method = m;
    }
}
