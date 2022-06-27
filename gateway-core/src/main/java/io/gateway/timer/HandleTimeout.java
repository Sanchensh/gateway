package io.gateway.timer;

import io.gateway.exception.HandleException;
import io.gateway.exception.TimeoutException;
import io.gateway.common.SessionContext;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.concurrent.TimeUnit;

/**
 * @author jlxu@telenav.cn
 * @date 2021/6/22 17:44
 */
public class HandleTimeout {
    public static void startTimer(SessionContext sessionContext) {
        TimerHolder.schedule(sessionContext.getKey(), () -> HandleException.errorProcess(sessionContext, new TimeoutException(HttpResponseStatus.REQUEST_TIMEOUT, "Request is timeout, please retry!")),
                sessionContext.getTimeout(), TimeUnit.MILLISECONDS);
    }

    public static void stopTimer(SessionContext sessionContext) {
        TimerHolder.stop(sessionContext.getKey());
    }
}
