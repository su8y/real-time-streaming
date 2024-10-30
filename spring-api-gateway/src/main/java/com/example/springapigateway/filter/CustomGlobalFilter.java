package com.example.springapigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class CustomGlobalFilter implements GlobalFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(CustomGlobalFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("before custom global filter: request id -> {}",exchange.getRequest().getId());
        Mono<Void> filter = chain.filter(exchange);
        return filter.then(
            Mono.fromRunnable(() -> {
                log.info("custom global filter completed: request id -> {}",exchange.getRequest().getId());
            })
        );
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
