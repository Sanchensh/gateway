package io.gateway.exception;

import io.gateway.common.SessionContext;
import io.gateway.timer.HandleTimeout;
import io.gateway.util.ChannelUtil;
import lombok.extern.slf4j.Slf4j;

import static io.netty.channel.ChannelFutureListener.CLOSE;

@Slf4j
public class HandleException {
    public static void errorProcess(SessionContext context, Throwable throwable) {
        HandleTimeout.stopTimer(context);
        ChannelUtil.clearSessionContext(context.getClientChannel());
        context.getServerChannel().writeAndFlush(throwable).addListener(CLOSE);
    }
}
