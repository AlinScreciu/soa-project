package com.uni.soa.config;

import com.uni.soa.security.BearerTokenFilter;
import com.uni.soa.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
  @Value("${jwt.secret}")
  private String secretKey;

  private final TokenHolder tokenHolder;

  public SecurityConfig(TokenHolder tokenHolder) {
    this.tokenHolder = tokenHolder; // Inject TokenHolder via constructor
  }
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/public/**").permitAll()
                    .requestMatchers(HttpMethod.OPTIONS,"/**").permitAll()
                    .anyRequest().authenticated() // All requests require authentication

            )
            .addFilterBefore(new BearerTokenFilter(tokenHolder), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new JwtAuthenticationFilter(secretKey), UsernamePasswordAuthenticationFilter.class)
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .build();
  }

}
