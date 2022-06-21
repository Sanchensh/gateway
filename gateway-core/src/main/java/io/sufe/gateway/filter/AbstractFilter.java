package io.sufe.gateway.filter;


import io.sufe.gateway.common.SessionContext;
import io.sufe.gateway.exception.GatewayServerException;

public abstract class AbstractFilter implements Filter {
    public final static String PRE_FILTER_NAME = "PROXY_FILTER_";

    @Override
    public void run(AbstractFilterContext filterContext, SessionContext sessionContext) throws GatewayServerException {
        filterContext.fireNext(sessionContext);
    }
}
