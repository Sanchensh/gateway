//package io.gateway.config;
//
//import io.netty.bootstrap.ServerBootstrap;
//import io.netty.buffer.PooledByteBufAllocator;
//import io.netty.channel.ChannelOption;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.nio.NioChannelOption;
//import io.netty.channel.socket.nio.NioServerSocketChannel;
//import io.netty.handler.codec.http2.Http2SecurityUtil;
//import io.netty.handler.ssl.*;
//import io.netty.handler.ssl.util.SelfSignedCertificate;
//import io.sufe.gateway.annotation.ConditionalOnHttp2Enabled;
//import io.sufe.gateway.annotation.ConditionalOnHttp2SSLEnabled;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.DependsOn;
//import org.springframework.stereotype.Component;
//
//import javax.net.ssl.SSLException;
//import java.security.cert.CertificateException;
//
////
////@Configuration
//@Slf4j
//@EnableConfigurationProperties(value = GatewayServerProperties.class)
//public class GatewayServerAutoConfiguration {
//    private final GatewayServerInitializer initializer;
//
//    public GatewayServerAutoConfiguration(@Autowired GatewayServerInitializer initializer) {
//        this.initializer = initializer;
//    }
//
//    @Configuration(proxyBeanMethods = false)
//    @ConditionalOnHttp2Enabled
//    protected static class Http2 {
//        @Bean
//        @ConditionalOnHttp2SSLEnabled
//        public SslContext sslContext() throws CertificateException, SSLException {
//            SslProvider provider = OpenSsl.isAlpnSupported() ? SslProvider.OPENSSL : SslProvider.JDK;
//            SelfSignedCertificate ssc = new SelfSignedCertificate();
//            return SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey())
//                    .sslProvider(provider)
//                    /* NOTE: the cipher filter may not include all ciphers required by the HTTP/2 specification.
//                     * Please refer to the HTTP/2 specification for cipher requirements. */
//                    .ciphers(Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
//                    .applicationProtocolConfig(new ApplicationProtocolConfig(
//                            ApplicationProtocolConfig.Protocol.ALPN,
//                            // NO_ADVERTISE is currently the only mode supported by both OpenSsl and JDK providers.
//                            ApplicationProtocolConfig.SelectorFailureBehavior.NO_ADVERTISE,
//                            // ACCEPT is currently the only mode supported by both OpenSsl and JDK providers.
//                            ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT,
//                            ApplicationProtocolNames.HTTP_2,
//                            ApplicationProtocolNames.HTTP_1_1))
//                    .build();
//        }
//    }
//
//    @Component
//    @DependsOn("serverBootstrap")
//    public class GatewayStarter implements ApplicationRunner{
//        final GatewayServerProperties properties;
//        final ServerBootstrap serverBootstrap;
//
//        public GatewayStarter(GatewayServerProperties properties, ServerBootstrap serverBootstrap) {
//            this.properties = properties;
//            this.serverBootstrap = serverBootstrap;
//        }
//
//        @Override
//        public void run(ApplicationArguments args) throws Exception {
//            log.info("Gateway server start on port {}", properties.getPort());
//            serverBootstrap.bind(properties.getPort()).sync();
//        }
//    }
//
//    @Bean
//    public ServerBootstrap serverBootstrap(GatewayServerProperties properties) {
//        ServerBootstrap serverBootstrap = new ServerBootstrap();
//        if (properties.getMaster() <= 0) {
//            throw new IllegalArgumentException("the size of eventloop must be greater than 0");
//        }
//        //boss线程
//        NioEventLoopGroup bossGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() << properties.getMaster());
//        //worker线程
//        NioEventLoopGroup workGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() << properties.getWorker());
//        serverBootstrap.group(bossGroup, workGroup)
//                .channel(NioServerSocketChannel.class)
//                .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
//                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
//                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Constants.CONNECT_TIMEOUT_MILLIS)
//                .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
//                .childOption(NioChannelOption.SO_KEEPALIVE, Boolean.TRUE)
//                .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
//                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
//                .childHandler(initializer);
//        return serverBootstrap;
//    }
//
//
//}
