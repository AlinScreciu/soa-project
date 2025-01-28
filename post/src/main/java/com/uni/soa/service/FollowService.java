package com.uni.soa.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class FollowService {

  @Value("${follow.service.url}")
  private String followServiceUrl;
  private final RestTemplate restTemplate;

  public FollowService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public boolean isFollowing(String followerId, String followeeId) {
    String url = followServiceUrl + "/api/follow/" + followeeId + "/is-following/" + followerId;
    return Boolean.TRUE.equals(restTemplate.getForObject(url, Boolean.class));
  }

  public List<String> getFollowersByUserId(String userId) {
      String url = followServiceUrl + "/api/follow/" + userId + "/followers";
      return Arrays.stream(Objects.requireNonNull(restTemplate.getForObject(url, String[].class))).toList();
  }

}
