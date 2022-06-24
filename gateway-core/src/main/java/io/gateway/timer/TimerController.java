package io.gateway.timer;

import io.gateway.client.ChannelUtil;
import io.gateway.common.SessionContext;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.gateway.exception.GatewayServerException;
import io.gateway.server.ProxyRunner;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author jlxu@telenav.cn
 * @date 2021/6/22 17:44
 */
public class TimerController {
    public static void startTimer(SessionContext sessionContext) {
        TimerHolder.schedule(sessionContext.getKey(), () -> {
            if (Objects.nonNull(sessionContext.getClientChannel())) {//如果clientChannel不为空,则丢掉这个Channel，因为超时可能是该Channel导致的，如果放回池中可能还会出现类似问题
                ChannelUtil.clearSessionContext(sessionContext.getClientChannel());
                sessionContext.getClientChannel().close();
            }
            ProxyRunner.errorProcess(sessionContext, new GatewayServerException(HttpResponseStatus.REQUEST_TIMEOUT.code(), "Request Timeout", "This request is timeout,please retry"));
        }, sessionContext.getTimeout(), TimeUnit.MILLISECONDS);
    }

    public static void stopTimer(SessionContext sessionContext) {
        TimerHolder.stop(sessionContext.getKey());
    }
}
