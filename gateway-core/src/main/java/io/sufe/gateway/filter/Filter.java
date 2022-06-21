package io.sufe.gateway.filter;


import io.sufe.gateway.common.SessionContext;
import io.sufe.gateway.exception.GatewayServerException;

public interface Filter {
    String name();
    void run(AbstractFilterContext filterContext, SessionContext sessionContext) throws GatewayServerException;
}
