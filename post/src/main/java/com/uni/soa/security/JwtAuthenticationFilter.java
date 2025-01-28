package com.uni.soa.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
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
import java.security.Key;
import java.util.Date;

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


    // Skip token validation for public endpoints

    if (path.startsWith("/public/")) {
      logger.debug("Public endpoint accessed, skipping JWT validation.");
      filterChain.doFilter(request, response);
      return;
    }

    String authHeader = request.getHeader("Authorization");
    logger.debug("Filtering request {}", authHeader);

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);

      try {
        // Parse the JWT
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();

        // Check token expiration
        Date expiration = claims.getExpiration();
        if (expiration != null && expiration.before(new Date())) {
          logger.error("Token expired");
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          response.getWriter().write("Token expired");
          return;
        }

        // Extract user info from claims
        String username = claims.getSubject();
        // Set authentication in Spring Security context
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(username, null, null);  // No roles are set
        SecurityContextHolder.getContext().setAuthentication(authentication);

      } catch (Exception e) {
        logger.error("Failed to parse token: {}", e.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Invalid or expired token");
        return;
      }
    } else {
      logger.error("Failed to read token");
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write("Missing Authorization header");
      return;
    }

    logger.debug("Successfully validated jwt token");
    filterChain.doFilter(request, response);
  }
}
