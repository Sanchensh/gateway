package io.gateway.server;

import io.gateway.common.SessionContext;
import io.gateway.exception.GatewayServerException;
import io.gateway.exception.HandleException;
import io.gateway.route.LoadBalance;
import io.gateway.route.RoundRobinBalance;
import io.gateway.server.client.GatewayChannelPool;
import io.gateway.util.ChannelUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.graalvm.collections.Pair;

import java.util.Objects;

public class Invoke {

    public static void call(SessionContext sessionContext, int retry, int maxRetry) {
        //将host设置到context中，可以直接使用，避免字符串的拼接与拆解
        sessionContext.setTargetURL("localhost:8888");
        LoadBalance loadBalance = new RoundRobinBalance();
        loadBalance.acquire(sessionContext);
        Pair<Channel, Bootstrap> pair = GatewayChannelPool.instance.poll("localhost", 8888, "localhost:8888");
        if (Objects.nonNull(pair.getRight())) { //如果是新建立的连接
            Bootstrap bootstrap = pair.getRight();
            bootstrap.connect().addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    Channel channel = future.channel();
                    ChannelUtil.attributeSessionContext(channel, sessionContext);
                    channel.writeAndFlush(sessionContext.getRequest()).addListener((ChannelFutureListener) future1 -> handleIfError(future1, sessionContext, retry, maxRetry));
                } else {
                    handleIfError(future, sessionContext, retry, maxRetry);
                }
            });
        } else {//如果连接池有连接，并且返回，则直接用已有的连接调用
            Channel channel = pair.getLeft();
            ChannelUtil.attributeSessionContext(channel, sessionContext);
            channel.writeAndFlush(sessionContext.getRequest()).addListener((ChannelFutureListener) future -> handleIfError(future, sessionContext, retry, maxRetry));
        }
    }


    private static void handleIfError(ChannelFuture future, SessionContext sessionContext, int retry, int maxRetry) {
        if (future.isSuccess()){
            return;
        }
        if (retry < maxRetry) {
            call(sessionContext, retry + 1, maxRetry);
        }
        if (!future.isSuccess()) {
            HandleException.errorProcess(sessionContext, new GatewayServerException("Connect to client failed")); //如果retry次数达到还无法正确响应，则给客户端返回错误信息
        }
    }

}
