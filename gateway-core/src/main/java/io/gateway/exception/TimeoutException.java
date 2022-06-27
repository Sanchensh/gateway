package io.gateway.exception;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeoutException extends RuntimeException{
    private HttpResponseStatus status;
    private String message;
}

