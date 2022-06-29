package io.gateway.server.client;

import io.gateway.config.GatewayServerProperties;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

import static io.gateway.common.Constants.ZERO;

public class ClientInitializer extends ChannelInitializer<Channel> {

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