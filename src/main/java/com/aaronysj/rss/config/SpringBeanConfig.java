package com.aaronysj.rss.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
public class SpringBeanConfig {

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
