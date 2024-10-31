package com.example.springapigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class UserReqeustThrottlingFilter implements GatewayFilter, Ordered {
    private static final String REDIS_UNIQUE_USER = "unique_user::";


    @Autowired
    RedisTemplate<String, Integer> redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final String hostAddress = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        final String throttlingIPKey = REDIS_UNIQUE_USER + hostAddress;
        boolean existProcess = Boolean.TRUE.equals(redisTemplate.hasKey(throttlingIPKey));
        if (existProcess) {
            exchange.getResponse().setStatusCode(HttpStatus.CONFLICT);
            return exchange.getResponse().setComplete();
        }

        redisTemplate.opsForValue().set(throttlingIPKey, 1);

        return chain.filter(exchange).doFinally(v -> {
            redisTemplate.delete(throttlingIPKey);
        });
    }

    @Override
    public int getOrder() {
        return -3;
    }
}
