package com.dms.apps.vertx.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.vertx.core.http.HttpMethod;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RestMapping {
    String value();
    HttpMethod[] methods() default {
        HttpMethod.OPTIONS,
        HttpMethod.GET,
        HttpMethod.POST,
        HttpMethod.PUT,
        HttpMethod.DELETE,
        HttpMethod.HEAD,
        // HttpMethod.TRACE,
        // HttpMethod.CONNECT,
        // HttpMethod.PATCH,
        // HttpMethod.OTHER
    };
}
