package top.ivan.simple.gateway.core;


import top.ivan.simple.gateway.core.http.HttpCode;
import org.apache.http.HttpStatus;

/**
 * @author Ivan on 2019/11/18.
 * @version 1.0
 */
public class BadRequestException extends Exception implements HttpCode {

    private final int httpCode;

    public BadRequestException() {
        this("Bad Request");
    }

    public BadRequestException(Throwable cause) {
        this(cause.getMessage(), cause);
    }

    public BadRequestException(String msg) {
        this(msg, null);
    }

    public BadRequestException(String msg, int httpCode) {
        this(msg, httpCode, null);
    }

    public BadRequestException(String msg, Throwable cause) {
        this(msg, HttpStatus.SC_BAD_REQUEST, cause);
    }

    public BadRequestException(String msg, int httpCode, Throwable cause) {
        super(msg, cause);
        this.httpCode = httpCode;
    }

    @Override
    public int getHttpCode() {
        return httpCode;
    }

}
