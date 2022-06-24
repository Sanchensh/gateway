package io.gateway.server;

import io.gateway.common.Constants;
import io.gateway.common.SessionContext;
import io.gateway.exception.GatewayServerException;
import io.gateway.timer.TimerController;
import io.gateway.util.ByteBufManager;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import static io.netty.channel.ChannelFutureListener.CLOSE;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpUtil.is100ContinueExpected;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@Slf4j
@ChannelHandler.Sharable
public class GatewayServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
            if (is100ContinueExpected(fullHttpRequest)) { //HTTP 100 Continue 信息型状态响应码表示目前为止一切正常, 客户端应该继续请求, 如果已完成请求则忽略.
                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
            }
            String timeout = fullHttpRequest.headers().get(Constants.TIMEOUT);
            SessionContext sessionContext = StringUtils.hasLength(timeout)
                    ? new SessionContext(Long.parseLong(timeout), ctx.channel())
                    : new SessionContext(ctx.channel());
            TimerController.startTimer(sessionContext);//该请求超时设置
            sessionContext.setRequest(fullHttpRequest);
            ProxyRunner.run(sessionContext);
        } finally {
            ByteBufManager.deepSafeRelease(msg);//释放请求数据，避免堆外内存泄露
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("系统内部错误(proxy server error)，详细信息：{}", cause);
        GatewayServerException customException = new GatewayServerException(INTERNAL_SERVER_ERROR.code(), "internal server error(proxy server error)", cause.getMessage());
        DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(HTTP_1_1,
                INTERNAL_SERVER_ERROR,
                Unpooled.directBuffer().writeBytes(customException.getMessage().getBytes()));
        ctx.writeAndFlush(defaultFullHttpResponse).addListener(CLOSE);
    }

    @Override
    public void userEventTriggered(final ChannelHandlerContext ctx, final Object evt) throws Exception {
        // 如果是超时,则对连接进行关闭
        if (!(evt instanceof IdleStateEvent)) {
            super.userEventTriggered(ctx, evt);
            return;
        }
        ctx.channel().closeFuture();
    }
}
