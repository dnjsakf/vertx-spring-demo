package com.dms.apps.user.verticle;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.dms.apps.config.vertx.abs.AbstractRestVerticle;
import com.dms.apps.config.vertx.annotations.RestVerticle;
import com.dms.apps.user.domain.UserVO;
import com.dms.apps.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestVerticle(path = "/rest/user")
public class UserListRestVerticle extends AbstractRestVerticle {

    @Autowired
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(RoutingContext ctx) {
        List<UserVO> userList = userService.getUserList();
        try {
            String json = objectMapper.writeValueAsString(userList);
            ctx.response()
               .putHeader("content-type", "application/json")
               .end(json);
        } catch (Exception e) {
            ctx.response()
               .setStatusCode(500)
               .end("Failed to process user list");
        }
    }
    
}

