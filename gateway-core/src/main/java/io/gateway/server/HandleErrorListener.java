package io.gateway.server;

import io.gateway.common.SessionContext;
import io.gateway.exception.HandleException;
import io.gateway.util.ChannelUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class HandleErrorListener implements ChannelFutureListener {

    private SessionContext sessionContext;

    public HandleErrorListener(SessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        if (future.isSuccess()) {
            return;
        }
//        HandleException.errorProcess(sessionContext, future.cause());
    }
}