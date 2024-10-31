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
public class DailyRequestLimitFilter implements GatewayFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(DailyRequestLimitFilter.class);
    private static final String REDIS_DAILY_LIMIT = "daily-request::";
    private static final String REDIS_BLACK_LIST = "blacklist::";
    private static final Integer MAX_REQUEST = 1000;
    private static final String REMAIN_REQUEST_HEADER_VALUE = "REMAIN_REQUESTS";

    @Autowired
    RedisTemplate<String, Integer> redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final String hostAddress = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        final String blackIPKey = REDIS_BLACK_LIST + hostAddress;
        final String dailyIPKey = REDIS_DAILY_LIMIT + hostAddress;
        Optional<Integer> currentRequests = Optional.ofNullable(redisTemplate.opsForValue().get(dailyIPKey));

        boolean isBlockUser = Boolean.TRUE.equals(redisTemplate.hasKey(blackIPKey));
        boolean isOverDailyRequest = currentRequests.orElse(0) > MAX_REQUEST;
        if (isBlockUser) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS);
            return exchange.getResponse().setComplete();
        }
        if (isOverDailyRequest) {
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            return exchange.getResponse().setComplete();
        }

        int remainingRequests = MAX_REQUEST - currentRequests.orElse(0) - 1;
        HttpHeaders headers = exchange.getResponse().getHeaders();
        headers.set(REMAIN_REQUEST_HEADER_VALUE, String.valueOf(remainingRequests));

        redisTemplate.opsForValue().increment(dailyIPKey);
        redisTemplate.expire(dailyIPKey, 1, TimeUnit.DAYS);

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
