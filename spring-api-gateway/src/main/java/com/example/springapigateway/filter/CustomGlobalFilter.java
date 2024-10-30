package com.example.springapigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class CustomGlobalFilter implements GlobalFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(CustomGlobalFilter.class);
    @Autowired
    RedisTemplate<String, Integer> redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String hostAddress = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        if (Boolean.TRUE.equals(redisTemplate.hasKey(hostAddress))) {
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            log.info("exist custom global filter: hostAddress -> {}", hostAddress);
            return exchange.getResponse().setComplete();
        } else {
            redisTemplate.opsForValue().set(hostAddress, 1);
            log.info("before custom global filter: hostAddress -> {}", hostAddress);
        }

        return chain.filter(exchange).then(
                Mono.fromRunnable(() -> {
                    redisTemplate.delete(hostAddress);
                    log.info("after custom global filter: hostAddress -> {}", hostAddress);
                })
        );
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
