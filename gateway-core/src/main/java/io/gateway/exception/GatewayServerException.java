package io.gateway.exception;

import io.netty.handler.codec.http.HttpResponseStatus;

public class GatewayServerException extends Exception {
    private String message = "";
    private HttpResponseStatus status = HttpResponseStatus.OK;

    public GatewayServerException(String reason) {
        this(HttpResponseStatus.INTERNAL_SERVER_ERROR, reason);
    }

    public GatewayServerException(HttpResponseStatus status, String reason) {
        this.message = reason;
        this.status = status;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public HttpResponseStatus getStatus() {
        return status;
    }
}
