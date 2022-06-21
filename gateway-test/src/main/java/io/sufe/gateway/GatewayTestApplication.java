package io.sufe.gateway;

import io.sufe.gateway.annotation.EnableGateway;
import io.sufe.gateway.config.GatewayProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableGateway
@EnableAspectJAutoProxy
public class GatewayTestApplication {



    public static void main(String[] args) {

        SpringApplication.run(GatewayTestApplication.class, args);

    }

    @Component
    class Runner implements ApplicationRunner{
        @Autowired
        private GatewayProperties p;
        @Override
        public void run(ApplicationArguments args) throws Exception {
            System.out.println(p.getProperties().getName());
        }
    }

}
