package com.example.springapigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


public class UserDuplicateFilter extends AbstractGatewayFilterFactory<UserDuplicateFilter.Config> {
    private static final Logger log = LoggerFactory.getLogger(UserDuplicateFilter.class);


    @Override
    public GatewayFilter apply(Config config) {
        return new OrderedGatewayFilter((exchange,chain)->{
            log.info("Custom Pre filter: request id -> {}", exchange.getRequest().getId());
            return chain.filter(exchange).then(Mono.fromRunnable(() ->
                    log.info("Custom Post filter: request id -> {}", exchange.getRequest().getId()))
            );
        },-3);
    }

    public static class Config{

    }

}
