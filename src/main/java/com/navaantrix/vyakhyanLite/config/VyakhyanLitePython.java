package com.navaantrix.vyakhyanLite.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class VyakhyanLitePython {

    @Value("${python.api.base-url}")
    private String baseUrl;

    @Bean
    public WebClient pythonWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
