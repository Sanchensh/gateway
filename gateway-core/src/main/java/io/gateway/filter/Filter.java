package io.gateway.filter;


import io.gateway.common.SessionContext;
import io.gateway.exception.GatewayServerException;

public interface Filter {
    String name();
    void run(AbstractFilterContext filterContext, SessionContext sessionContext) throws GatewayServerException;
}
