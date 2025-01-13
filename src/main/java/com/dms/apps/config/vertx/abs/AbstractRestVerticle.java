package com.dms.apps.config.vertx.abs;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public abstract class AbstractRestVerticle extends io.vertx.core.AbstractVerticle {

    public String method;
    public JsonObject body;

    public void init(RoutingContext ctx){
        HttpMethod httpMethod = ctx.request().method();

        method = httpMethod.name();
        body = ctx.getBodyAsJson();

    }

    public void handle(RoutingContext ctx){

    }

}
