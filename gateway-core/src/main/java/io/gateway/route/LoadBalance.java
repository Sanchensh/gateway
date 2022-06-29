package io.gateway.route;

import io.gateway.common.SessionContext;

public interface LoadBalance {
    Route acquire(SessionContext sessionContext);
}
