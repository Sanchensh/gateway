//package io.gateway.config;
//
//import io.gateway.server.GatewayServerHandler;
//import io.netty.channel.Channel;
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.ChannelPipeline;
//import io.netty.handler.codec.http.HttpObjectAggregator;
//import io.netty.handler.codec.http.HttpRequestDecoder;
//import io.netty.handler.codec.http.HttpResponseEncoder;
//import io.netty.handler.codec.http.HttpServerKeepAliveHandler;
//import io.netty.handler.timeout.IdleStateHandler;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.concurrent.TimeUnit;
//
//@Configuration
//public class GatewayServerInitializer extends ChannelInitializer<Channel> {
//    private final GatewayServerProperties properties;
//
//    public GatewayServerInitializer(@Autowired GatewayServerProperties properties) {
//        this.properties = properties;
//    }
//
//    @Override
//    public void initChannel(Channel ch) throws Exception {
//        ChannelPipeline pipeline = ch.pipeline();
//        pipeline.addLast(new IdleStateHandler(30, Constants.ZERO, Constants.ZERO, TimeUnit.SECONDS));
//        pipeline.addLast(new HttpResponseEncoder());
//        pipeline.addLast(new HttpRequestDecoder(Constants.ALL_MAX_SIZE, Constants.ALL_MAX_SIZE, Constants.ALL_MAX_SIZE, Boolean.TRUE));
//        pipeline.addLast(new HttpServerKeepAliveHandler());
//        pipeline.addLast(new HttpObjectAggregator(properties.getContentLength()));
//        pipeline.addLast(new GatewayServerHandler());
//    }
//}