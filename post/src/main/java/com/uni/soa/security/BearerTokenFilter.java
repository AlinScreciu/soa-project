package com.uni.soa.security;

import com.uni.soa.config.TokenHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class BearerTokenFilter extends OncePerRequestFilter {
  private final TokenHolder tokenHolder;

  public BearerTokenFilter(TokenHolder tokenHolder) {
    this.tokenHolder = tokenHolder;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
          throws ServletException, IOException {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7); // Extract the token
      tokenHolder.setToken(token);
    }
    try {
      filterChain.doFilter(request, response);
    } finally {
      tokenHolder.clear(); // Ensure thread safety
    }
  }
}
