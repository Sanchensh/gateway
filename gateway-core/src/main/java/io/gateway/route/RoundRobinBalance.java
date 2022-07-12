package io.gateway.route;

import io.gateway.common.SessionContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;

public class RoundRobinBalance implements LoadBalance{
    @Override
    public Route acquire(SessionContext sessionContext) {
        FullHttpRequest request = sessionContext.getRequest();
        HttpMethod method = request.method();
        String uri = request.uri();
        HttpHeaders headers = request.headers();

        System.out.println(uri);
        return null;
    }

}
