package com.dms.apps.config.vertx;

import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.dms.apps.config.vertx.abs.AbstractRestVerticle;
import com.dms.apps.config.vertx.annotations.RestVerticle;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class VertxRouterConfig {

    private final Vertx vertx;
    private final Router router;
    private final ApplicationContext applicationContext;

    public VertxRouterConfig(Vertx vertx, ApplicationContext applicationContext) {
        this.vertx = vertx;
        this.router = Router.router(vertx);
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        String[] beanNames = applicationContext.getBeanNamesForAnnotation(RestVerticle.class);
        for (String beanName : beanNames) {
            log.info("beanName={}", beanName != null ? beanName : null);
            Object bean = applicationContext.getBean(beanName);
            Class<?> beanClass = AopProxyUtils.ultimateTargetClass(bean);
            RestVerticle annotation = beanClass.getAnnotation(RestVerticle.class);
            // RestVerticle annotation = bean.getClass().getAnnotation(RestVerticle.class);
            String path = annotation.path();
            router.route(path).handler(ctx -> {
                try {
                    // ((AbstractVerticle) bean).start(Promise.promise());
                    ((AbstractRestVerticle) bean).init(ctx);
                    ((AbstractRestVerticle) bean).handle(ctx);
                } catch (Exception e) {
                    ctx.fail(e); // 예외 발생 시 실패 처리
                }
            });
        }

        vertx.createHttpServer().requestHandler(router).listen(8081);
    }
}
