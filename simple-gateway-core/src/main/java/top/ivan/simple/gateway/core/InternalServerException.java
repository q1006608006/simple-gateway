package top.ivan.simple.gateway.core;

import org.apache.http.HttpStatus;

/**
 * @author Ivan
 * @description
 * @date 2020/12/24
 */
public class InternalServerException extends BadGatewayException {

    public InternalServerException() {
        this("Internal Server Error");
    }

    public InternalServerException(String msg) {
        this(msg, null);
    }

    public InternalServerException(String msg, Throwable cause) {
        super(msg, HttpStatus.SC_INTERNAL_SERVER_ERROR, cause);
    }

}
