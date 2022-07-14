package io.gateway.timer;

import io.gateway.exception.HandleException;
import io.gateway.exception.TimeoutException;
import io.gateway.common.SessionContext;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.concurrent.TimeUnit;

import static io.netty.channel.ChannelFutureListener.CLOSE;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.REQUEST_TIMEOUT;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author jlxu@telenav.cn
 * @date 2021/6/22 17:44
 */
public class HandleTimeout {
    private static final String msg = "Request is timeout, please retry!";
    public static void startTimer(SessionContext sessionContext) {
        TimerHolder.schedule(sessionContext.getKey(), new TimeoutRunner(sessionContext), sessionContext.getTimeout(), TimeUnit.MILLISECONDS);
    }

    public static void stopTimer(SessionContext sessionContext) {
        TimerHolder.stop(sessionContext.getKey());
    }

    private static class TimeoutRunner implements Runnable{
        private SessionContext sessionContext;
        public TimeoutRunner(SessionContext sessionContext){
            this.sessionContext = sessionContext;
        }
        @Override
        public void run() {
                HandleTimeout.stopTimer(sessionContext);
                DefaultFullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, REQUEST_TIMEOUT, Unpooled.directBuffer().writeBytes(msg.getBytes()));
                sessionContext.getServerChannel().writeAndFlush(response).addListener(CLOSE);
        }
    }
}
