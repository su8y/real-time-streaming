package com.example.springapigateway;

import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.context.annotation.Configuration;

// @Configuration
public class RateLimiterConfig {
    private final RateLimiterRegistry rateLimiterRegistry;
    public RateLimiterConfig() {
        rateLimiterRegistry = RateLimiterRegistry.ofDefaults();
    }
}
