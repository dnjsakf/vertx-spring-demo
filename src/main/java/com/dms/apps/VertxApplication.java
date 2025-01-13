package com.dms.apps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// @EnableAspectJAutoProxy
// public class VertxApplication implements CommandLineRunner {
public class VertxApplication{

    // @Autowired
    // private Vertx vertx;

    // @Autowired
    // private UserVerticle userVerticle;

    public static void main(String[] args) {
        SpringApplication.run(VertxApplication.class, args);
    }

    // @Override
    // public void run(String... args) throws Exception {
    //     vertx.deployVerticle(userVerticle);
    // }
}
