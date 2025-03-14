package com.uni.soa.service;

import com.uni.soa.entity.dto.UserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserService {

  @Value("${identity.service.url}")
  private String identityServiceUrl;
  private final RestTemplate restTemplate;

  public UserService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public UserDTO getUser(String userId) {
    String url = identityServiceUrl + "/api/users/" + userId;
    return restTemplate.getForObject(url, UserDTO.class);
  }
}
