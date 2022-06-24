package io.gateway.exception;

import io.netty.handler.codec.http.HttpResponseStatus;

public class GatewayServerException extends Exception {
    private String message;
    private int status;

    public GatewayServerException(String type, String reason) {
        this(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), type, reason);
    }

    public GatewayServerException(String message) {
        this("unknown error", message);
    }

    public GatewayServerException(int status, String type, String reason) {
        this.message = reason;
        this.status = status;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public int getStatus() {
        return status;
    }
}
