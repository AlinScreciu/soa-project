package com.uni.soa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import org.springframework.web.client.RestTemplate;


import java.util.List;

@Configuration
public class RestTemplateConfig {
  private final TokenHolder tokenHolder;

  public RestTemplateConfig(TokenHolder tokenHolder) {
    this.tokenHolder = tokenHolder;
  }

  @Bean
  public RestTemplate restTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setInterceptors(List.of((request, body, execution) -> {
      String token = tokenHolder.getToken();
      if (token != null) {
        request.getHeaders().set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
      }
      return execution.execute(request, body);
    }));
    return restTemplate;
  }
}
