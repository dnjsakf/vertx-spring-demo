package com.dms.apps.user.handler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.dms.apps.user.domain.UserRequest;
import com.dms.apps.user.domain.UserVO;
import com.dms.apps.user.service.UserService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class UserHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private Validator validator;

    public Mono<ServerResponse> getUsers(ServerRequest request) {
        List<UserVO> userList = userService.getUserList();
        log.info("info");
        log.debug("debug");
        log.error("error");
        return ServerResponse.ok().bodyValue(userList);
    }

    public Mono<ServerResponse> getUserById(ServerRequest request) {
        return request.bodyToMono(UserRequest.class)
          .flatMap(userRequest -> {
              Errors errors = new BeanPropertyBindingResult(userRequest, UserRequest.class.getName());
              validator.validate(userRequest, errors);

              if (errors.hasErrors()) {
                  return ServerResponse.badRequest().bodyValue(errors.getAllErrors());
              }

              UserVO user = userService.getUser(userRequest.getUserId());

              return ServerResponse.ok().bodyValue(user);
          });
    }

}
