package io.gateway.filter;

import io.gateway.common.SessionContext;
import io.gateway.exception.GatewayServerException;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public abstract class AbstractFilterContext {

    public AbstractFilterContext next;

    public void fireNext(SessionContext sessionContext) throws GatewayServerException {
        if (Objects.isNull(next)) {
            return;
        }
        fire0(next, sessionContext);
    }

    public void fireSelf(SessionContext sessionContext) {
        fire0(this, sessionContext);
    }

    public void fireFilter(String filterName, SessionContext sessionContext) throws GatewayServerException {
        AbstractFilterContext filterContext = GatewayFilterPipeLine.instance.get(filterName);
        if (Objects.isNull(filterContext)) {
            return;
        }
        fire0(filterContext, sessionContext);
    }

    private void fire0(AbstractFilterContext filterContext, SessionContext sessionContext) {
        try {
            filterContext.getFilter().run(filterContext, sessionContext);
        } catch (GatewayServerException e) {
            log.error("系统内部错误，错误信息：", e);
        }
    }

    public abstract Filter getFilter();
}
