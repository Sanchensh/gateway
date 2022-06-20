package io.sufe.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "io.gateway")
@Data
public class SufeGatewayProperties {
    private Properties properties = new Properties();
    public static class Properties{

    }
}
