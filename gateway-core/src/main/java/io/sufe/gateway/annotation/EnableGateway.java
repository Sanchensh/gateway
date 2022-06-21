package io.sufe.gateway.annotation;


import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableGateway {
}
