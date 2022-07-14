package io.gateway.server;

import io.gateway.common.SessionContext;
import io.gateway.config.GatewayServerProperties;
import io.gateway.exception.GatewayServerException;
import io.gateway.server.client.GatewayChannelPool;
import io.gateway.timer.HandleTimeout;
import io.gateway.util.ChannelUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.graalvm.collections.Pair;

import java.util.Objects;

import static io.netty.channel.ChannelFutureListener.CLOSE;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpUtil.is100ContinueExpected;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@Slf4j
@ChannelHandler.Sharable
public class GatewayServerHandler extends ChannelInboundHandlerAdapter {
    private GatewayServerProperties properties;

    public GatewayServerHandler(GatewayServerProperties properties) {
        this.properties = properties;
        GatewayChannelPool.instance.init(properties);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
            if (is100ContinueExpected(fullHttpRequest)) { //HTTP 100 Continue 信息型状态响应码表示目前为止一切正常, 客户端应该继续请求, 如果已完成请求则忽略.
                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
            }
            SessionContext sessionContext = new SessionContext(ctx.channel(), fullHttpRequest);
            HandleTimeout.startTimer(sessionContext);//该请求超时设置
            call(sessionContext);
        } catch (Exception e) {
            log.error("服务出错{}", e);
            ReferenceCountUtil.release(msg);//释放请求数据，避免堆外内存泄露
        }
    }

    public static void call(SessionContext sessionContext) {
        //将host设置到context中，可以直接使用，避免字符串的拼接与拆解
        sessionContext.setTargetURL("localhost:8888");
//        LoadBalance loadBalance = new RoundRobinBalance();
//        loadBalance.acquire(sessionContext);
        Pair<Channel, Bootstrap> pair = GatewayChannelPool.instance.poll("localhost", 8888, "localhost:8888");
        if (Objects.nonNull(pair.getRight())) { //如果是新建立的连接
            Bootstrap bootstrap = pair.getRight();
            bootstrap.connect().addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    Channel channel = future.channel();
                    ChannelUtil.attributeSessionContext(channel, sessionContext);
                    channel.writeAndFlush(sessionContext.getRequest())
                            .addListener(new HandleErrorListener(sessionContext));
                }
            }).addListener(new HandleErrorListener(sessionContext));
        } else {//如果连接池有连接，并且返回，则直接用已有的连接调用
            Channel channel = pair.getLeft();
            ChannelUtil.attributeSessionContext(channel, sessionContext);
            channel.writeAndFlush(sessionContext.getRequest())
                    .addListener(new HandleErrorListener(sessionContext));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Internal server error ：{}", cause);
        DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(HTTP_1_1, INTERNAL_SERVER_ERROR, Unpooled.directBuffer().writeBytes(cause.getMessage().getBytes()));
        ctx.writeAndFlush(defaultFullHttpResponse).addListener(CLOSE);
    }

    @Override
    public void userEventTriggered(final ChannelHandlerContext ctx, final Object evt) throws Exception {
        // 如果是超时,则对连接进行关闭
        if (evt instanceof IdleStateEvent) {
            ctx.channel().closeFuture();
        }
        super.userEventTriggered(ctx, evt);
    }

}
