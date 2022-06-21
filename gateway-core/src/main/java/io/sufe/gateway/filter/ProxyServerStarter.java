package io.sufe.gateway.filter;

import io.sufe.gateway.server.ProxyNettyServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class ProxyServerStarter implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        ProxyNettyServer server = new ProxyNettyServer();
        log.info("........初始化filter........");
        initJavaFilters();
        log.info("........netty 启动中........");
        server.startServer(8080);
    }

    private static void initJavaFilters() {
        DefaultFilterPipeLine.INSTANCE.addLastSegment();
    }
}
