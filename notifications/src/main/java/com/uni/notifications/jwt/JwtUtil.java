package com.uni.notifications.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {


  private final SecretKey secretKey;

  public JwtUtil(@Value("${jwt.secret}") String secretKeyData) {
    this.secretKey = Keys.hmacShaKeyFor(secretKeyData.getBytes());
  }

  public String generateToken(String userId) {
    return Jwts.builder()
            .setSubject(userId)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))  // 10 hours
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact();
  }

  public String extractUserId(String token) {
    Claims claims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .getBody();

    return claims.getSubject();
  }

  public boolean isTokenExpired(String token) {
    Claims claims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .getBody();

    Date expiration = claims.getExpiration();
    return expiration != null && expiration.before(new Date());
  }
}
