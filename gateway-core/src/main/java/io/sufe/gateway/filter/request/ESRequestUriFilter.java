package io.sufe.gateway.filter.request;

import io.sufe.gateway.common.SessionContext;
import io.sufe.gateway.exception.GatewayServerException;
import io.sufe.gateway.filter.AbstractFilter;
import io.sufe.gateway.filter.AbstractFilterContext;

public class ESRequestUriFilter extends AbstractFilter {
    public static String DEFAULT_NAME = PRE_FILTER_NAME + ESRequestUriFilter.class.getSimpleName().toUpperCase();

    @Override
    public String name() {
        return DEFAULT_NAME;
    }

    @Override
    public void run(AbstractFilterContext filterContext, SessionContext sessionContext) throws GatewayServerException {

    }
}
