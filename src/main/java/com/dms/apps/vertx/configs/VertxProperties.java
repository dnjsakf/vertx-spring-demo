package com.dms.apps.vertx.configs;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VertxProperties {

    private static final String VERTX_CONFIG_PATH = "src/main/resources/application.yml";

    private static JsonObject config = null;

    public VertxProperties(){
        getConfig(ar -> {
            if( ar.succeeded() ){
                log.info("속성 파일 조회 성공");
            } else {
                log.info("속성 파일 조회 실패", ar.cause());
            }
        });
    }

    public static void getConfig(Handler<AsyncResult<JsonObject>> handler){
        if( config != null ){
            handler.handle(Future.succeededFuture(config));
        } else {
            // 설정 파일에서 구성 읽기
            ConfigStoreOptions fileStore = new ConfigStoreOptions()
                .setType("file")
                .setFormat("yaml")
                .setConfig(new JsonObject().put("path", VERTX_CONFIG_PATH));
    
            ConfigRetrieverOptions retrieverOptions = new ConfigRetrieverOptions().addStore(fileStore);
            ConfigRetriever retriever = ConfigRetriever.create(Vertx.vertx(), retrieverOptions);

            // config = retriever.getCachedConfig();
            retriever.getConfig(ar -> {
                if( ar.succeeded() ){
                    config = ar.result();
                    handler.handle(Future.succeededFuture(config));
                } else {
                    handler.handle(Future.failedFuture(ar.cause()));
                }
            });
        }
    }

}
