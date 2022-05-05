package top.ivan.simple.gateway.core;

import top.ivan.simple.gateway.core.http.HttpCode;
import org.apache.http.HttpStatus;

/**
 * @author Ivan
 * @description
 * @date 2020/12/24
 */
public class BadGatewayException extends RuntimeException implements HttpCode {

    private final int httpCode;

    public BadGatewayException() {
        this("Bad Gateway");
    }

    public BadGatewayException(Throwable cause) {
        this(cause.getMessage(), cause);
    }

    public BadGatewayException(String msg) {
        this(msg, null);
    }

    public BadGatewayException(String msg, int httpCode) {
        this(msg, httpCode, null);
    }

    public BadGatewayException(String msg, Throwable cause) {
        this(msg, HttpStatus.SC_BAD_GATEWAY, cause);
    }

    public BadGatewayException(String msg, int httpCode, Throwable cause) {
        super(msg, cause);
        this.httpCode = httpCode;
    }

    @Override
    public int getHttpCode() {
        return httpCode;
    }

}
