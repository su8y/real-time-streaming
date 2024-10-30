package com.example.springapigateway.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import redis.embedded.RedisServer;

import java.io.IOException;

@Configuration
@Order(-1)
public class EmbededRedisConfig {
    private RedisServer redisServer;

    public EmbededRedisConfig(@Value("${spring.data.redis.port}") int port) throws IOException {
        this.redisServer = new RedisServer(port);
    }

    @PostConstruct
    public void startRedisServer(){
        this.redisServer.start();
    }

    @PreDestroy
    public void stopRedisServer(){
        this.redisServer.stop();
    }
}
