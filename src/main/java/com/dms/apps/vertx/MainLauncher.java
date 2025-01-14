package com.dms.apps.vertx;

import com.dms.apps.vertx.configs.VertxDatabase;
import com.dms.apps.vertx.configs.VertxProperties;

import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Launcher;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainLauncher extends Launcher {

    public static void main(String[] args) {
        new MainLauncher().dispatch(args);
    }

    public MainLauncher(){
        log.info("MainLauncher");
        new VertxProperties();
    }
  
    @Override
    public void beforeStartingVertx(VertxOptions options) {
        log.info("beforeStartingVertx");

        VertxProperties.getConfig(ar -> {
            if (ar.succeeded()) {
                JsonObject config = ar.result();

                // VertxOptions 설정
                options.setEventLoopPoolSize(config.getJsonObject("vertx").getInteger("eventLoopPoolSize"))
                       .setWorkerPoolSize(config.getJsonObject("vertx").getInteger("workerPoolSize"))
                       .setInternalBlockingPoolSize(config.getJsonObject("vertx").getInteger("internalBlockingPoolSize"));
            }
        });
    }

    @Override
    public void afterStartingVertx(Vertx vertx) {
        log.info("afterStartingVertx");
        
        new VertxDatabase(vertx);
        enableCircuitBraker(vertx);
        enableServiceDiscovery(vertx);
    }

    @Override
    public void beforeDeployingVerticle(DeploymentOptions deploymentOptions) {
        log.info("beforeDeployingVerticle");
    }

    /**
     * 
     * @param vertx
     */
    private void enableCircuitBraker(Vertx vertx){
        VertxProperties.getConfig(ar -> {
            if (ar.succeeded()) {
                JsonObject config = ar.result();

                // Circuit Breaker 설정
                CircuitBreaker breaker = CircuitBreaker.create("dms-circuit-breaker", vertx,
                    new CircuitBreakerOptions()
                        .setMaxFailures(5) // 최대 실패 횟수 설정
                        .setTimeout(2000) // 타임아웃 설정 (밀리초)
                        .setFallbackOnFailure(true) // 실패 시 대체 동작 설정
                        .setResetTimeout(10000) // 회로 재설정 시간 설정 (밀리초)
                );
        
                // 요청 처리 예시
                breaker.executeWithFallback(future -> {
                    // 여기에 서비스 호출 코드 작성
                    future.complete("Service call succeeded!");
                }, v -> {
                    // 실패 시 실행할 코드
                    return "Service call failed!";
                }).onComplete(result -> {
                    if (result.succeeded()) {
                        log.info(result.result());
                    } else {
                        log.info("Service call failed permanently!");
                    }
                });
            }
        });
    }

    private void enableServiceDiscovery(Vertx vertx){
        ServiceDiscovery discovery = ServiceDiscovery.create(vertx);
        discovery = ServiceDiscovery.create(vertx,
            new ServiceDiscoveryOptions()
                .setAnnounceAddress("service-announce")
                .setName("my-name"));
    }

}
