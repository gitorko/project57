package com.demo.project57.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestConfig {

    /**
     * Use RestClient instead of RestTemplate
     */
    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .requestFactory(getClientHttpRequestFactory())
                .baseUrl("http://jsonplaceholder.typicode.com")
                .build();
    }

    /**
     * Override timeouts in request factory
     */
    private SimpleClientHttpRequestFactory getClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        //Connect timeout
        clientHttpRequestFactory.setConnectTimeout(5_000);
        //Read timeout
        clientHttpRequestFactory.setReadTimeout(5_000);
        return clientHttpRequestFactory;
    }

}
