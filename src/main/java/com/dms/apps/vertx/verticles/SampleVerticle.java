package com.dms.apps.vertx.verticles;

import com.dms.apps.vertx.abstracts.AbstractRestVerticle;
import com.dms.apps.vertx.annotations.RestMapping;
import com.dms.apps.vertx.annotations.RestVerticle;

import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestVerticle("/sample")
public class SampleVerticle extends AbstractRestVerticle {

    @RestMapping("/list")
    public void handleGetSampleList(RoutingContext context) {
        log.info("Sample List");
        context.response().end("Sample List");
    }

    @RestMapping("/:id")
    public void handleGetSample(RoutingContext context) {
        String userId = context.pathParam("id");
        log.info("Sample ID");
        context.response().end("Sample ID: " + userId);
    }

}
