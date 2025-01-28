package com.uni.follow.security;

import com.uni.follow.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
  private final JwtUtil jwtUtil;

  public JwtAuthenticationFilter(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil; // Injecting JwtUtil
  }


  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
          throws ServletException, IOException {

    String path = request.getRequestURI();

    if (path.startsWith("/public/")) {
      logger.debug("Public endpoint accessed, skipping JWT validation.");
      filterChain.doFilter(request, response);
      return;
    }


    String authHeader = request.getHeader("Authorization");
    logger.info("Filtering request for URI: {}", request.getRequestURI());

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);

      try {
        if (token.equals("notification-service")) {
          UsernamePasswordAuthenticationToken authentication =
                  new UsernamePasswordAuthenticationToken("notification-service", null, null);
          SecurityContextHolder.getContext().setAuthentication(authentication);

        } else {

          // Extract user ID using JwtUtil
          String userId = jwtUtil.extractUserId(token);

          if (jwtUtil.isTokenExpired(token)) {
            logger.error("Token expired");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token expired");
            return;
          }

          // Set authentication in the SecurityContext
          UsernamePasswordAuthenticationToken authentication =
                  new UsernamePasswordAuthenticationToken(userId, null, null);
          SecurityContextHolder.getContext().setAuthentication(authentication);
        }
      } catch (Exception e) {
        logger.error("Failed to parse token: {}", e.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Invalid or expired token");
        return;
      }
    } else {
      logger.error("Authorization header missing or invalid");
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write("Missing Authorization header");
      return;
    }

    logger.info("JWT token validated successfully");
    filterChain.doFilter(request, response);
  }
}
