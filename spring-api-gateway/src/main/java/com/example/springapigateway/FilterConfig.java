package com.example.springapigateway;

import com.example.springapigateway.filter.CustomGlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public CustomGlobalFilter customGlobalFilter() {
        return new CustomGlobalFilter();
    }
}
