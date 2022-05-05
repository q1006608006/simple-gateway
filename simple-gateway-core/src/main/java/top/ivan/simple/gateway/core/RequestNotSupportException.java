package top.ivan.simple.gateway.core;

import top.ivan.simple.gateway.core.http.HttpCode;
import org.apache.http.HttpStatus;

/**
 * @author Ivan on 2019/11/20.
 * @version 1.0
 */
public class RequestNotSupportException extends BadRequestException implements HttpCode {

    private final int httpCode = HttpStatus.SC_BAD_REQUEST;

    public RequestNotSupportException(String msg) {
        super(msg);
    }

    @Override
    public int getHttpCode() {
        return this.httpCode;
    }
}
