package com.dms.apps.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dms.apps.user.domain.UserVO;
import com.dms.apps.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.web.reactive.function.BodyExtractors;

@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/mvc/user")
    public Mono<String> getUserList(ServerHttpResponse response) {
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.getHeaders().add("Custom-Header", "CustomValue");

        return Mono.just(userService.getUserList()).map(userList -> {
            try {
                return objectMapper.writeValueAsString(userList);
            } catch (Exception e) {
                log.error("Error converting user list to JSON", e);
                return "[]";
            }
        });
    }

    @RequestMapping("/mvc/user/{id}")
    public Mono<String> getUser(@PathVariable String id, ServerHttpRequest request, ServerHttpResponse response) {
        // request.bodyToMono(UserVO.class).map(user -> { return "User Created: " + user.getId(); });

        String name = request.getQueryParams().getFirst("name");

        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.getHeaders().add("Custom-Header", "CustomValue");

        return Mono.just(userService.getUserList()).map(userList -> {
            try {
                return objectMapper.writeValueAsString(userList);
            } catch (Exception e) {
                log.error("Error converting user list to JSON", e);
                return "[]";
            }
        });
    }
}

