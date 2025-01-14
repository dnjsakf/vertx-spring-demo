package com.dms.apps.vertx.configs;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VertxDatabase {

    private static PgConnectOptions connectOptions;
    private static PoolOptions poolOptions;
    private static PgPool pool;

    public VertxDatabase(Vertx vertx){
        connect(vertx);
    }

    public void connect(Vertx vertx){
        VertxProperties.getConfig(ar -> {
            if( ar.succeeded() ){
                JsonObject config = ar.result();
                JsonObject datasource = config
                    .getJsonObject("spring")
                    .getJsonObject("datasource")
                    .getJsonObject("postgres");
                
                connectOptions = new PgConnectOptions()
                    .setPort(datasource.getInteger("port", 5432))
                    .setHost(datasource.getString("host", "localhost"))
                    .setDatabase(datasource.getString("database", "postgres"))
                    .setUser(datasource.getString("username", "postgres"))
                    .setPassword(datasource.getString("password", "postgres"));
                
                poolOptions = new PoolOptions()
                    .setMaxSize(5);

                pool = PgPool.pool(vertx, connectOptions, poolOptions);
            }
        });
    }

    public static PgPool getClient(){
        return pool;
    }
    
}