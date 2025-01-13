package com.dms.apps.user.verticle;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dms.apps.user.domain.UserVO;
import com.dms.apps.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.impl.RouterImpl;

@Component
public class UserVerticle extends AbstractVerticle {

    @Autowired
    private UserService userService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = new RouterImpl(vertx);

        router.route("/user").handler(req -> {
            List<UserVO> userList = userService.getUserList();
            try {
                String json = objectMapper.writeValueAsString(userList);
                req.response()
                    .putHeader("content-type", "application/json")
                    .end(json);
            } catch (Exception e) {
                req.response()
                    .setStatusCode(500)
                    .end("Failed to process user list");
            }
        });

        vertx.createHttpServer().requestHandler(router).listen(8081, http -> {
            if (http.succeeded()) {
                startPromise.complete();
            } else {
                startPromise.fail(http.cause());
            }
        });
    }
}
