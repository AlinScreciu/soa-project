package com.uni.soa.config;

import org.springframework.stereotype.Component;

@Component
public class TokenHolder {
  private static final ThreadLocal<String> currentToken = new ThreadLocal<>();

  public void setToken(String token) {
    currentToken.set(token);
  }

  public String getToken() {
    return currentToken.get();
  }

  public void clear() {
    currentToken.remove();
  }
}
