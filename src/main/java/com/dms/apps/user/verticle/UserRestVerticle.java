package com.dms.apps.user.verticle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.dms.apps.config.vertx.abs.AbstractRestVerticle;
import com.dms.apps.config.vertx.annotations.AllowMethod;
import com.dms.apps.config.vertx.annotations.RequestDomain;
import com.dms.apps.config.vertx.annotations.RestVerticle;
import com.dms.apps.user.domain.UserRequest;
import com.dms.apps.user.domain.UserVO;
import com.dms.apps.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestVerticle(path = "/rest/user/:id")
public class UserRestVerticle extends AbstractRestVerticle {

    @Autowired
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @AllowMethod(methods = { HttpMethod.GET })
    @RequestDomain(UserRequest.class)
    public void handle(RoutingContext ctx) {
        try {
            String id = ctx.pathParam("id");
            UserRequest body = ctx.get("requestBody");
            log.info("method={}, id={}, body={}", method, id, body);

            UserVO user = userService.getUser(id);

            String json = objectMapper.writeValueAsString(user);

            log.info(json);

            ctx.response()
               .putHeader("content-type", "application/json")
               .end(json);
        } catch ( Exception e ) {
            e.printStackTrace();
            ctx.response()
               .setStatusCode(500)
               .end("Failed to process user");
        }
    }
    
}

