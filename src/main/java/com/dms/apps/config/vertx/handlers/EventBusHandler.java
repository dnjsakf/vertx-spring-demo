package com.dms.apps.config.vertx.handlers;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventBusHandler<T> implements Handler<Message<T>> {

    protected Vertx vertx;
    public void setVertx(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void handle(Message<T> message) {
        JsonObject body = (JsonObject) message.body();
        log.info(message.address()+":"+body.encodePrettily());
    }
    
}
