package io.gateway.server.client;

import io.gateway.config.GatewayServerProperties;
import io.gateway.thread.GatewayThreadFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

import static io.gateway.common.Constants.GATEWAY_CLIENT_BOSS_NAME;

public class HttpClient {
    private NioEventLoopGroup bossGroup;
    private int connectTimeout;
    private GatewayServerProperties properties;

    public HttpClient(GatewayServerProperties properties) {
        this.connectTimeout = properties.getConnectTimeout() * 1000;
        this.properties = properties;
        this.bossGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() << properties.getClient(), GatewayThreadFactory.create(GATEWAY_CLIENT_BOSS_NAME, true));
    }

    /**
     * 配置channel
     *
     * @param ip   目标ip
     * @param port 端口
     * @return
     */
    public Bootstrap newChannel(String ip, int port) {
        Bootstrap bootstrap = newBootstrap(ip, port);
        bootstrap.handler(new ClientInitializer(properties));
        return bootstrap;//直接返回bootstrap是为了防止future已经isDone,再加上listener无效
    }

    /**
     * new一个bootstrap
     *
     * @param ip   目标ip
     * @param port 端口
     * @return boostrap配置
     */
    private Bootstrap newBootstrap(String ip, int port) {
        Bootstrap bootstrap = new Bootstrap()
                .remoteAddress(new InetSocketAddress(ip, port))
                .group(bossGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
                .option(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                .option(ChannelOption.AUTO_CLOSE, Boolean.FALSE);
        return bootstrap;
    }

}
