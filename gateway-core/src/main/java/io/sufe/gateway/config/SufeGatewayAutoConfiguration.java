package io.sufe.gateway.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = SufeGatewayProperties.class)
public class SufeGatewayAutoConfiguration {
}
