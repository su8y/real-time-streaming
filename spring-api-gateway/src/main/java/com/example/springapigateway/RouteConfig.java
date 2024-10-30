package com.example.springapigateway;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.DedupeResponseHeaderGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {
    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("stream_spring", r -> r.path("/ss/**")
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
