package com.uni.notifications.config;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class TokenHolder {

  // A concurrency-safe map from userId (or username) to token
  private final ConcurrentMap<String, String> userTokens = new ConcurrentHashMap<>();

  /**
   * Sets (or updates) the token for a given userId.
   */
  public void setToken(String userId, String token) {
    userTokens.put(userId, token);
  }

  /**
   * Retrieves the token for a given userId.
   */
  public String getToken(String userId) {
    return userTokens.get(userId);
  }

  /**
   * Removes the token for a given userId.
   */
  public void removeToken(String userId) {
    userTokens.remove(userId);
  }
}
