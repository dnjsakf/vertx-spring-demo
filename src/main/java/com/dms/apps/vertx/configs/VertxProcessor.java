package com.dms.apps.vertx.configs;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.Reflections;

import com.dms.apps.vertx.abstracts.AbstractRestVerticle;
import com.dms.apps.vertx.annotations.RestMapping;
import com.dms.apps.vertx.annotations.RestVerticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VertxProcessor extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        VertxProperties.getConfig(ar -> {
            if (ar.succeeded()) {
                log.info("VertxProcessor starting...");

                JsonObject config = ar.result();
                JsonObject httpConfig = config.getJsonObject("vertx").getJsonObject("http");

                String host = httpConfig.getString("host", "localhost");
                Integer port = httpConfig.getInteger("port", 8080);

                Router router = createRouter();

                vertx.createHttpServer()
                    .requestHandler(router)
                    .listen(port, host, http -> {
                        if (http.succeeded()) {
                            startPromise.complete();
                            log.info("HTTP 서버가 시작되었습니다: http://"+host+":"+port);
                        } else {
                            startPromise.fail(http.cause());
                        }
                    });
            }
        });
    }

    /**
     * 라우터 생성
     * @return
     */
    private Router createRouter() {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        try {
            setDynamicRoutes(router);
        } catch ( Exception e ){
            log.error("라우터 생성 오류: {}", e.getMessage());
        }

        return router;
    }

    // Reflection을 사용하여 어노테이션이 적용된 클래스를 가져오는 메소드
    private Set<Class<?>> getClassesWithRestVerticleAnnotation() throws Exception {
        Reflections reflections = new Reflections("com.dms.apps.vertx");
        return reflections.getTypesAnnotatedWith(RestVerticle.class);
    }

    /**
     * RestVerticle이 적용된 클래스에서
     * RestMapping 어노테이션이 적용된 메소드를 찾아서
     * 라우팅 처리
     * @param router
     */
    private void setDynamicRoutes(Router router) throws Exception {
        // 어노테이션을 적용받은 클래스들을 찾아 라우팅 설정
        for (Class<?> cls : getClassesWithRestVerticleAnnotation()) {
            if (cls.isAnnotationPresent(RestVerticle.class)) {
                RestVerticle annotation = cls.getAnnotation(RestVerticle.class);
                final String path = annotation.value();

                AbstractRestVerticle verticleInstance = (AbstractRestVerticle) cls.getDeclaredConstructor().newInstance();
                vertx.deployVerticle(verticleInstance);

                for (Method method : cls.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(RestMapping.class)) {
                        RestMapping mappingAnnotation = method.getAnnotation(RestMapping.class);
                        String fullPath = path + mappingAnnotation.value();
                        Set<HttpMethod> methods = Arrays.stream(mappingAnnotation.methods()) .collect(Collectors.toSet());
                        

                        log.info("Route: {}, Allow: {}", fullPath, methods);
                        for(HttpMethod httpMethod : methods){
                            Route route = router.route(httpMethod, fullPath);
                            route.handler(
                                CorsHandler.create("*")
                                .allowedMethods(methods)
                            );
                            route.handler(ctx -> {
                                try {
                                    log.info("[{}] {}", ctx.request().method().name(), ctx.request().path());
                                    method.invoke(verticleInstance, ctx);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                            route.failureHandler(ctx -> {
                                ctx.response().end(ctx.failure().getMessage());
                            });
                        }
                    }
                }
            }
        }
    }
}
