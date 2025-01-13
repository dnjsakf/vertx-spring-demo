package com.dms.apps.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.dms.apps.config.vertx.annotations.AllowMethod;
import com.dms.apps.config.vertx.annotations.RequestDomain;
import com.dms.apps.user.domain.UserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// @Aspect
// @Component
public class AnnotationAspect {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private Validator validator;

    @Before("@annotation(allowMethod) && args(ctx,..)")
    public void checkMethod(JoinPoint joinPoint, AllowMethod allowMethod, RoutingContext ctx) {
        HttpMethod requestMethod = ctx.request().method();
        HttpMethod[] allowedMethods = allowMethod.methods();
        try {
            for (HttpMethod method : allowedMethods) {
                log.info("method={}, requestMethod={}, matched={}", method, requestMethod, method == requestMethod);
                if (method == requestMethod) {
                    return; // 요청된 HTTP 메소드가 허용된 메소드 중 하나이면 통과
                }
            }
            throw new UnsupportedOperationException("HTTP method not allowed");
        } catch ( Exception e ){
            ctx.response()
                .setStatusCode(500)
                .end(e.getMessage());
        }
    }

    @Before("@annotation(requestDomain) && args(ctx,..)")
    public void checkRequestDomain(JoinPoint joinPoint, RequestDomain requestDomain, RoutingContext ctx) {
        Class<?> domainClass = requestDomain.value();
        HttpMethod requestMethod = ctx.request().method();

        if (requestMethod == HttpMethod.GET) {
            try {
                JsonObject queryParams = new JsonObject();
                ctx.queryParams().forEach(entry -> queryParams.put(entry.getKey(), entry.getValue()));
                Object requestBody = objectMapper.convertValue(queryParams.getMap(), domainClass);

                Errors errors = new BeanPropertyBindingResult(requestBody, domainClass.getName());
                validator.validate(requestBody, errors);

                if (errors.hasErrors()) {
                    // throw new Exception(objectMapper.writeValueAsString(errors.getAllErrors()));
                } else {
                    ctx.put("requestBody", requestBody);
                }
            } catch ( Exception e ){
                throw new RuntimeException("Failed to convert query params to domain object", e);
            }
        } else if (requestMethod == HttpMethod.POST) {
            try {
                JsonObject requestBody = ctx.getBodyAsJson();
                ctx.put("requestBody", requestBody);
            } catch ( Exception e ){
                throw new RuntimeException("Failed to convert body to domain object", e);
            }
        }
    }

    // @AfterThrowing(pointcut = "@annotation(allowMethod) || @annotation(requestDomain)", throwing = "ex")
    // public void handleException(JoinPoint joinPoint, Throwable ex) {
    //     // 예외 로깅 및 처리
    //     System.err.println("Exception in Aspect: " + ex.getMessage());
    //     // 추가적인 예외 처리 로직을 여기에 추가할 수 있습니다.
    // }
    
}
