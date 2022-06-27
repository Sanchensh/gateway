package io.gateway.server;

import io.gateway.config.GatewayServerProperties;
import io.gateway.thread.GatewayThreadFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerKeepAliveHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

import static io.gateway.common.Constants.*;


@Slf4j
public class GatewayServer {

    private GatewayServerProperties properties;
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workGroup;

    public GatewayServer(GatewayServerProperties properties) {
        check(properties);
        this.properties = properties;
        this.bossGroup = getEventLoopGroup(true);
        this.workGroup = getEventLoopGroup(false);
    }

    private NioEventLoopGroup getEventLoopGroup(boolean boss) {
        return new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() << (boss ? properties.getBoss() : properties.getWork()),
                GatewayThreadFactory.create(boss ? GATEWAY_SERVER_BOSS_NAME : GATEWAY_SERVER_WORK_NAME, true));
    }

    private ServerBootstrap config(NioEventLoopGroup bossGroup, NioEventLoopGroup workGroup) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, properties.getConnectTimeout() * 1000)
                .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                .childOption(NioChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new Initializer());
        return serverBootstrap;
    }

    public void startServer() throws InterruptedException {
        log.info("Gateway Server start on port " + properties.getPort());
        ChannelFuture channelFuture = config(bossGroup, workGroup).bind(properties.getPort()).sync();
        channelFuture.channel().closeFuture().sync();
    }

    public void shutdown() {
        bossGroup.shutdownGracefully().syncUninterruptibly();
        workGroup.shutdownGracefully().syncUninterruptibly();
        log.info("Gateway server stopped");
    }

    private class Initializer extends ChannelInitializer<Channel> {
        @Override
        public void initChannel(Channel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new IdleStateHandler(properties.getIdle(), ZERO, ZERO, TimeUnit.SECONDS));
            pipeline.addLast(new HttpResponseEncoder());
            pipeline.addLast(new HttpRequestDecoder(properties.getMaxInitialSize() * 1024, properties.getMaxHeaderSize() * 1024, properties.getMaxChunkSize() * 1024, properties.getValidHeader()));
            pipeline.addLast(new HttpServerKeepAliveHandler());
            pipeline.addLast(new HttpObjectAggregator(properties.getContentLength() * 1024 * 1024));
            pipeline.addLast(new GatewayServerHandler(properties));
        }
    }

    void check(GatewayServerProperties p) {
        if (p.getBoss() <= 0) {
            throw new IllegalArgumentException("The size of boss thread pool must be > 0");
        }
        if (p.getWork() <= 0) {
            throw new IllegalArgumentException("The size of work thread pool must be > 0");
        }
        if (p.getConnectTimeout() <= 0) {
            throw new IllegalArgumentException("The size of work thread pool must be > 0");
        }

        if (p.getContentLength() <= 0) {
            throw new IllegalArgumentException("The max content-length must be > 0");
        }
    }
}
