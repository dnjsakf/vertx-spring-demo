package com.dms.apps.vertx.verticles;

import com.dms.apps.vertx.abstracts.AbstractRestVerticle;
import com.dms.apps.vertx.annotations.RestMapping;
import com.dms.apps.vertx.annotations.RestVerticle;
import com.dms.apps.vertx.configs.VertxDatabase;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlConnection;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestVerticle("/user")
public class MainVerticle extends AbstractRestVerticle {

    @RestMapping(value = "/list", methods = { HttpMethod.GET })
    public void handleGetUserList(RoutingContext context) {
        log.info("User List: {}", context.request().method().name());
        context.response().headers().add("content-type", "application/json");
        try {
            PgPool pool = VertxDatabase.getClient();
            pool.getConnection(ar -> {
                if( ar.succeeded() ){
                    SqlConnection conn = ar.result();

                    conn.query("select * from cm_menu_new_t")
                        .execute(ar2 -> {
                            if (ar2.succeeded()) {
                                RowSet<Row> rows = ar2.result();
                                rows.forEach(row -> {
                                    log.info(row.deepToString());
                                });
                                context.response().end(rows.toString());
                                conn.close();
                            } else {
                                // Release the connection to the pool
                                conn.close();
                                context.fail(ar2.cause());
                            }
                        });
                } else {
                    context.fail(ar.cause());
                }
            });
        } catch ( Exception e ){
            context.fail(e);
        }
    }

    @RestMapping(value = "/:id", methods = { HttpMethod.POST })
    public void handleGetUser(RoutingContext context) {
        try {
            String userId = context.pathParam("id");
            log.info("User ID: {}", userId);
            context.response().end("User ID: " + userId);

        } catch ( Exception e ){
            context.fail(e);
        }
    }

}
