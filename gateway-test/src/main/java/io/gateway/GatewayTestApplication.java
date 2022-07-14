package io.gateway;

import io.gateway.config.GatewayServerProperties;
import io.gateway.server.GatewayServer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
//@EnableGateway
public class GatewayTestApplication {
    public static void main(String[] args) {
        DirectMemoryReporter.getInstance().startReport();
        SpringApplication.run(GatewayTestApplication.class, args);
    }

    @Component
    public class RunServer implements ApplicationRunner{

        @Override
        public void run(ApplicationArguments args) throws Exception {
            new GatewayServer(new GatewayServerProperties()).startServer();
        }
    }
}
