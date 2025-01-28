package com.uni.notifications.service;

import com.uni.notifications.config.TokenHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class FollowService {

  private final RestTemplate restTemplate;
  private final String followServiceUrl;

  @Autowired
  public FollowService(RestTemplate restTemplate, @Value("${follow.service.url}") String followServiceUrl) {
    this.restTemplate = restTemplate;
    this.followServiceUrl = followServiceUrl;
  }

  public List<String> getFollowersByUserId(String userId) {
    HttpHeaders headers = new HttpHeaders();

    headers.setBearerAuth("notification-service");

    HttpEntity<Void> request = new HttpEntity<>(headers);

    // 3) Make the request using those headers
    ResponseEntity<List<String>> response =
            restTemplate.exchange(followServiceUrl + "/api/follow/" + userId + "/followers", HttpMethod.GET, request,
                    new ParameterizedTypeReference<>() {
                    });

    return response.getBody();
  }
}
