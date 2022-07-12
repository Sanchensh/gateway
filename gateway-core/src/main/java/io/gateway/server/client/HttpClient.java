package io.gateway.server.client;

import io.gateway.config.GatewayServerProperties;
import io.gateway.thread.GatewayThreadFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import static io.gateway.common.Constants.GATEWAY_CLIENT_BOSS_NAME;
import static io.gateway.common.Constants.ZERO;

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

    private static class ClientInitializer extends ChannelInitializer<Channel> {

        private GatewayServerProperties properties;

        private int maxContentLength;

        private int maxInitialSize;

        private int maxHeaderSize;

        private int maxChunkSize;

        private boolean validHeader;

        public ClientInitializer(GatewayServerProperties properties) {
            this.properties = properties;
            this.maxContentLength = properties.getContentLength() * 1024 * 1024;
            this.maxInitialSize = properties.getMaxInitialSize() * 8;
            this.maxHeaderSize = properties.getMaxHeaderSize() * 8;
            this.maxChunkSize = properties.getMaxChunkSize() * 8;
            this.validHeader = properties.getValidHeader();
        }

        @Override
        protected void initChannel(Channel ch) {
            ch.pipeline().addLast(new HttpClientCodec(maxInitialSize, maxHeaderSize, maxChunkSize, validHeader));
            ch.pipeline().addLast(new IdleStateHandler(properties.getIdle(), ZERO, ZERO, TimeUnit.SECONDS));
            ch.pipeline().addLast(new HttpObjectAggregator(maxContentLength));
            ch.pipeline().addLast(new HttpClientHandler());
        }
    }

}
