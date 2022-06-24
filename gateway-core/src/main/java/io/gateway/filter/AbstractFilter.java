package io.gateway.filter;


import io.gateway.common.SessionContext;
import io.gateway.exception.GatewayServerException;

import static io.gateway.common.Constants.PRE_FILTER_NAME;

public abstract class AbstractFilter implements Filter {



    @Override
    public void run(AbstractFilterContext filterContext, SessionContext sessionContext) throws GatewayServerException {
        filterContext.fireNext(sessionContext);
    }

    @Override
    public String name(){
        return PRE_FILTER_NAME + this.getClass().getSimpleName();
    }

}
