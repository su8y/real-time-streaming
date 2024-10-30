package com.example.springapigateway;

import com.example.springapigateway.filter.CustomGlobalFilter;
import com.example.springapigateway.filter.RetainUserFilterFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public CustomGlobalFilter customGlobalFilter(){
        return new CustomGlobalFilter();
    }

    @Bean
    RetainUserFilterFactory retainUserFilterFactory(){
        return new RetainUserFilterFactory();
    }
    @Bean
    public GatewayFilter retainUserFilter(){
        return  retainUserFilterFactory().apply(new RetainUserFilterFactory.Config("hello",true,true));

    }
}
