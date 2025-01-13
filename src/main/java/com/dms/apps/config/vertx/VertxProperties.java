package com.dms.apps.config.vertx;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "vertx")
public class VertxProperties {

    private Http http = new Http();
    private int workerPoolSize;
    private boolean worker;
    private int instances;

    @Data
    public static class Http {
        private String host;
        private int port;
    }
}

