package io.gateway.client;

import io.gateway.common.SessionContext;
import io.gateway.exception.GatewayServerException;
import io.gateway.timer.HandleTimeout;
import io.gateway.util.ChannelUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import lombok.extern.slf4j.Slf4j;

import static io.gateway.thread.GatewayThreadPool.submit;
import static io.netty.channel.ChannelFutureListener.CLOSE;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@Slf4j
@ChannelHandler.Sharable
public class HttpClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SessionContext sessionContext = ChannelUtil.getSessionContext(ctx.channel());
        //写回数据到客户端，并且需要关闭超时，同时需要清理SessionContext并将Channel放回到池中
        HandleTimeout.stopTimer(sessionContext);
        sessionContext.getServerChannel().writeAndFlush(msg).addListener((ChannelFutureListener) future -> {
            ChannelUtil.clearSessionContext(ctx.channel());
            if (future.isSuccess()) {
//                异步放入池中，避免阻塞导致的性能缺失
                submit(() -> GatewayClientChannelPool.instance.offer(ctx.channel(), sessionContext.getTargetURL()));
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        SessionContext sessionContext = ChannelUtil.getSessionContext(ctx.channel());
        //先关闭超时，再写回数据
        HandleTimeout.stopTimer(sessionContext);
        GatewayServerException customException = new GatewayServerException(INTERNAL_SERVER_ERROR, cause.getMessage());
        DefaultFullHttpResponse errorResponse = new DefaultFullHttpResponse(HTTP_1_1, INTERNAL_SERVER_ERROR, Unpooled.directBuffer().writeBytes(customException.getMessage().getBytes()));
        //出现异常，关闭客户端Channel
        sessionContext.getServerChannel().writeAndFlush(errorResponse)
                .addListener(CLOSE);

        ctx.channel().closeFuture();
        ChannelUtil.clearSessionContext(ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx,evt);
    }
}
