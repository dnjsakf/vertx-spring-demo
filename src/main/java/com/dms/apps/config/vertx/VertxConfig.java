package com.dms.apps.config.vertx;

import java.util.HashSet;
import java.util.Set;

import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dms.apps.config.vertx.annotations.EventBusVerticle;
import com.dms.apps.config.vertx.annotations.RestVerticle;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableConfigurationProperties(VertxProperties.class)
public class VertxConfig {
    
    private static Set<Class<?>> annotatedRestVerticle;
    private static Set<Class<?>> annotatedEventBusVerticle;

    private Vertx vertx;

    @Autowired
    private VertxProperties vertxProperties;

    @Bean
    public Vertx vertx() {
        if( vertx == null ){
            vertx = Vertx.vertx();
        }
        return vertx;
    }

    public static Set<Class<?>> getAnnotatedRestVerticle(){
        return annotatedRestVerticle;
    }

    public static Set<Class<?>> getAnnotatedEventBus(){
        return annotatedEventBusVerticle;
    }
    
    @Bean
    public DeploymentOptions deploymentOptions() {
        Reflections reflections = new Reflections("com.dms.apps");
        annotatedRestVerticle = reflections.getTypesAnnotatedWith(RestVerticle.class);
        annotatedEventBusVerticle = reflections.getTypesAnnotatedWith(EventBusVerticle.class); // EventBus 처리용

        DeploymentOptions options = new DeploymentOptions();
        options.setWorkerPoolSize(vertxProperties.getWorkerPoolSize());
        options.setWorker(vertxProperties.isWorker());
        options.setInstances(vertxProperties.getInstances());
        if (options.getConfig() == null) {
            options.setConfig(new JsonObject());
        }

        Router router = Router.router(vertx);
        // Default Handlers Settings
        router.route().handler(BodyHandler.create());
        router.route().handler(CookieHandler.create());
        router.route().handler(StaticHandler.create("assets"));
        router.route().handler(CORSHandlerCreate("*"));
        
        // Deploying Sub Verticles
        DeploymentOptions subDeployOptions = new DeploymentOptions();
        subDeployOptions.setWorker(true);
        subDeployOptions.setMultiThreaded(true);

        StringBuffer strb = new StringBuffer();
        strb.append("\n############### deploymentOptions ###############");
        strb.append("\n>>> Thread.currentThread.name : " + Thread.currentThread().getName());
        strb.append("\n>>> vertx deploymentIDs : " + vertx.deploymentIDs());
        strb.append("\n>>> vertx toString : " + vertx.toString());
        strb.append("\n>>> vertx hashCode : " + vertx.hashCode());
        strb.append("\n>>> vertx sharedData : " + vertx.sharedData());
        strb.append("\n############### vertx properties ###############");
        strb.append("\n>>> vertx host : " + vertxProperties.getHttp().getHost());
        strb.append("\n>>> vertx port : " + vertxProperties.getHttp().getPort());
        log.debug(strb.toString());       

        return options;
    }

    
    /**
     * Create CORSHandler
     * @param allowedOriginPattern
     * @return
     */
    private CorsHandler CORSHandlerCreate(String allowedOriginPattern) {
        Set<String> allowHeaders = new HashSet<>();
        allowHeaders.add("x-requested-with");
        allowHeaders.add("Access-Control-Allow-Origin");
        allowHeaders.add("origin");
        allowHeaders.add("Content-Type");
        allowHeaders.add("accept");
        allowHeaders.add("api_key");
        allowHeaders.add("Authorization");
        allowHeaders.add("meta");
        
        Set<HttpMethod> allowMethods = new HashSet<>();
        allowMethods.add(HttpMethod.GET);
        allowMethods.add(HttpMethod.PUT);
        allowMethods.add(HttpMethod.OPTIONS);
        allowMethods.add(HttpMethod.POST);
        allowMethods.add(HttpMethod.DELETE);
        allowMethods.add(HttpMethod.PATCH);
        
        return CorsHandler.create(allowedOriginPattern)
                .allowedHeaders(allowHeaders)
                .allowedMethods(allowMethods);
    }
    
}