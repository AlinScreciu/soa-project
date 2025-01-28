package com.uni.notifications.config;

import com.uni.notifications.security.SecurityUtils;
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
    return new RestTemplate();
  }
}
