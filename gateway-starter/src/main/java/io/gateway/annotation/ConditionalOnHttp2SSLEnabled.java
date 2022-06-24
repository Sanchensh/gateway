package io.gateway.annotation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ConditionalOnProperty(value = "io.gateway.server.http2.ssl.enabled", matchIfMissing = true)
public @interface ConditionalOnHttp2SSLEnabled {
}
