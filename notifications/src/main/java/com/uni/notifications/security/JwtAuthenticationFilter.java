package com.uni.notifications.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.el.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final Key key;
  private final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

  public JwtAuthenticationFilter(String secretKey) {
    // Decode the Base64 encoded secret key
    this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
          throws ServletException, IOException {

    String path = request.getRequestURI();

    // Skip token validation for public and WebSocket endpoints
    if (path.startsWith("/public/") || path.startsWith("/ws")) {
      filterChain.doFilter(request, response);
      return;
    }

    String authHeader = request.getHeader("Authorization");
    logger.info("Filtering request URI: {} with Authorization: {}", path,
            authHeader != null ? "Provided" : "Not Provided");

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);


      try {
        // Parse the JWT
        if (token.startsWith("api-token")) {
          logger.info("Processing internal service request with token: {}", token);
          UsernamePasswordAuthenticationToken authentication =
                  new UsernamePasswordAuthenticationToken("internal-service", null, Collections.emptyList());
          SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
          Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
          logger.debug("Parsed claims: {}", claims);

          // Check token expiration
          Date expiration = claims.getExpiration();
          if (expiration != null && expiration.before(new Date())) {
            logger.error("Token expired for request to {}", path);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token expired");
            return;
          }

          // Extract user information from claims
          String username = claims.getSubject();
          logger.info("Successfully authenticated user: {}", username);

          // Set authentication in Spring Security context
          UsernamePasswordAuthenticationToken authentication =
                  new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
          SecurityContextHolder.getContext().setAuthentication(authentication);
        }
      } catch (Exception e) {
        logger.error("Failed to parse token for request to {}: {}", path, e.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Invalid or expired token");
        return;
      }

    } else {
      logger.warn("Missing or invalid Authorization header for request to {}", path);
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write("Missing Authorization header");
      return;
    }

    logger.info("JWT authentication completed successfully for request to {}", path);
    filterChain.doFilter(request, response);
  }

}
