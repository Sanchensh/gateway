package io.gateway.exception;

import io.gateway.common.SessionContext;
import io.gateway.timer.HandleTimeout;
import io.gateway.util.ChannelUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import static io.netty.channel.ChannelFutureListener.CLOSE;

@Slf4j
public class HandleException {
    public static void errorProcess(SessionContext context, Throwable throwable) {
        HandleTimeout.stopTimer(context);

        context.getServerChannel().writeAndFlush(throwable).addListener(CLOSE);

        if (Objects.nonNull(context.getClientChannel())){
            context.getClientChannel().closeFuture();
            ChannelUtil.clearSessionContext(context.getClientChannel());
        }
    }
}
