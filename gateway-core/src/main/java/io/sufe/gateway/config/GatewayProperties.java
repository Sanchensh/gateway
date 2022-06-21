package io.sufe.gateway.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "io.gateway")
@Data
public class GatewayProperties {
    private Properties properties = new Properties();
    @Data
    @ToString
    public static class Properties{
       private String name;
    }
}
