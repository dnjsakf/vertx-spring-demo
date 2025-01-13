package com.dms.apps.config;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.dms.apps.user.handler.UserHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class WebFluxConfig {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(UserHandler userHandler) {
        return route(GET("/user/{id}"), userHandler::getUserById)
                .andRoute(GET("/user"), userHandler::getUsers);
    }
}

