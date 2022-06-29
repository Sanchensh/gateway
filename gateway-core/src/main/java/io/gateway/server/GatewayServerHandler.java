package io.gateway.server;

import io.gateway.server.client.GatewayClientChannelPool;
import io.gateway.common.Constants;
import io.gateway.common.SessionContext;
import io.gateway.config.GatewayServerProperties;
import io.gateway.exception.GatewayServerException;
import io.gateway.exception.HandleException;
import io.gateway.route.LoadBalance;
import io.gateway.route.RoundRobinBalance;
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
import org.springframework.util.StringUtils;

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
        check(properties);
        this.properties = properties;
        GatewayClientChannelPool.instance.init(properties);
    }

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
            HandleTimeout.startTimer(sessionContext);//该请求超时设置
            sessionContext.setRequest(fullHttpRequest);
            call(sessionContext, 0);
        } catch (Exception e) {
            log.error("Handle request occurred some errors, message ", e);
            ReferenceCountUtil.release(msg);//释放请求数据，避免堆外内存泄露
        }
    }

    private void call(SessionContext sessionContext, int retry) {
        //将host设置到context中，可以直接使用，避免字符串的拼接与拆解
        sessionContext.setTargetURL("localhost:8888");
        LoadBalance loadBalance = new RoundRobinBalance();
        loadBalance.acquire(sessionContext);
        Pair<Channel, Bootstrap> pair = GatewayClientChannelPool.instance.poll("localhost", 8888, "localhost:8888");
        if (Objects.nonNull(pair.getRight())) { //如果是新建立的连接
            Bootstrap bootstrap = pair.getRight();
            bootstrap.connect().addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    Channel channel = future.channel();
                    ChannelUtil.attributeSessionContext(channel, sessionContext);
                    channel.writeAndFlush(get(sessionContext)).addListener((ChannelFutureListener) future1 -> handleIfError(future1, sessionContext, retry));
                } else {
                    handleIfError(future, sessionContext, retry);
                }
            });
        } else {//如果连接池有连接，并且返回，则直接用已有的连接调用
            Channel channel = pair.getLeft();
            ChannelUtil.attributeSessionContext(channel, sessionContext);
            channel.writeAndFlush(get(sessionContext)).addListener((ChannelFutureListener) future -> handleIfError(future, sessionContext, retry));
        }
    }

    FullHttpRequest get(SessionContext sessionContext){
        return sessionContext.getRequest();
//        return  new DefaultFullHttpRequest(HTTP_1_1,HttpMethod.GET,"/hello/sss/1");
    }

    private void handleIfError(ChannelFuture future, SessionContext sessionContext, int retry) {
        if (retry < properties.getRetry()) {
            call(sessionContext, retry + 1);
        }
        if (!future.isSuccess()) {
            HandleException.errorProcess(sessionContext, new GatewayServerException("Connect to client failed")); //如果retry次数达到还无法正确响应，则给客户端返回错误信息
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Internal server error ：{}", cause);
        GatewayServerException gatewayServerException = new GatewayServerException(INTERNAL_SERVER_ERROR, cause.getMessage());
        DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(HTTP_1_1,
                INTERNAL_SERVER_ERROR,
                Unpooled.directBuffer().writeBytes(gatewayServerException.getMessage().getBytes()));
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

    private void check(GatewayServerProperties properties) {
        if (properties.getRetry() < 0) {
            throw new IllegalArgumentException("The parameter of retry must be >= 0");
        }
    }

}
