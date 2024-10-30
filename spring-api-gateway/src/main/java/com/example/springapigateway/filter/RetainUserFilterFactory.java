package com.example.springapigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;


public class RetainUserFilterFactory extends AbstractGatewayFilterFactory<RetainUserFilterFactory.Config> implements Ordered {
    private static final Logger log = LoggerFactory.getLogger(RetainUserFilterFactory.class);

    public RetainUserFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        //Custom Pre Filter
        return (exchange, chain) -> {
            String id = exchange.getRequest().getId();
            log.info("Custom Pre filter: request id -> {}",id);

            Mono<Void> filter = chain.filter(exchange);

            return filter.then(Mono.fromRunnable(() -> {
                log.info("Custom Post filter: request id -> {}",id);
            }));
        };
    }

    @Override
    public int getOrder() {
        return 0;
    }

    public static class Config {
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;

        public Config(String baseMessage, boolean preLogger, boolean postLogger) {
            this.baseMessage = baseMessage;
            this.preLogger = preLogger;
            this.postLogger = postLogger;
        }

        public Config() {
            this.baseMessage = "HELLO";
            this.preLogger = true;
            this.postLogger = true;
        }

        public String getBaseMessage() {
            return baseMessage;
        }

        public void setBaseMessage(String baseMessage) {
            this.baseMessage = baseMessage;
        }

        public boolean isPreLogger() {
            return preLogger;
        }

        public void setPreLogger(boolean preLogger) {
            this.preLogger = preLogger;
        }

        public boolean isPostLogger() {
            return postLogger;
        }

        public void setPostLogger(boolean postLogger) {
            this.postLogger = postLogger;
        }
    }
}
