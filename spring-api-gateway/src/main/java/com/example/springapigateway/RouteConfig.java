package com.example.springapigateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.DedupeResponseHeaderGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {
    @Autowired
    @Qualifier("retainUserFilter")
    GatewayFilter retainUserFilter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("stream_spring", r->r.path("/ss/stream/rest/**")
                        .filters(f -> f.stripPrefix(1).filter(retainUserFilter)
                        )
                        .uri("http://localhost:8090"))
                .route("stream_spring", r->r.path("/ss/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("http://localhost:8090"))
                .build();
    }

    @Bean
    public GlobalFilter dedupeResponseHeaderGlobalFilter(DedupeResponseHeaderGatewayFilterFactory dedupeFactory) {
        return (exchange, chain) -> dedupeFactory.apply(config -> {
            config.setName("Access-Control-Allow-Origin");
            config.setStrategy(DedupeResponseHeaderGatewayFilterFactory.Strategy.RETAIN_UNIQUE);
        }).filter(exchange, chain);
    }
}
