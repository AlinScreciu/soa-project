package com.uni.notifications.config;

import com.uni.notifications.jwt.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private static final Logger logger =
          LoggerFactory.getLogger(WebSocketConfig.class);
  private final TokenHolder tokenHolder;

  @Value("${jwt.secret}")
  private String secretKeyData;
  private final JwtUtil jwtUtil;

  public WebSocketConfig(JwtUtil jwtUtil, TokenHolder tokenHolder) {
    this.jwtUtil = jwtUtil;
    this.tokenHolder = tokenHolder;
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    // Where the client subscribes for messages
    registry.enableSimpleBroker("/topic", "/queue");

    // Prefix for messages from client to server
    registry.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    // SockJS endpoint
    registry.addEndpoint("/ws")
            .setAllowedOrigins("http://localhost:4200")
            .withSockJS();
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(new ChannelInterceptor() {
      @Override
      public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
          // 1) Extract the Authorization header from the native headers
          String authHeader = accessor.getFirstNativeHeader("Authorization");
          if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // 2) Validate the token (parse claims, check expiration, etc.)
            //    You can reuse logic from JwtAuthenticationFilter or a JwtUtil service
            try {
              // For example, if you wrote a JwtUtil class:
              String username = jwtUtil.extractUserId(token);
              tokenHolder.setToken(username, token);

              logger.info("Found username {}", username);
              // 3) If valid, set the user Principal
              //    Typically, we create an Authentication or simple UsernamePasswordAuthenticationToken
              UsernamePasswordAuthenticationToken user =
                      new UsernamePasswordAuthenticationToken(username, null, null);
              accessor.setUser(user);
              SecurityContextHolder.getContext().setAuthentication(user);


            } catch (Exception e) {
              logger.error("JWT validation error: ", e);   // Log the stack trace!

              // If the token is invalid or expired, you can deny the connection:
              throw new IllegalArgumentException("Invalid or expired token in STOMP CONNECT", e);
            }
          } else {
            // If no header or not Bearer, reject or allow fallback
            throw new IllegalArgumentException("Missing or invalid Authorization header in STOMP CONNECT");
          }
        }

        return message;
      }
    });
  }
}
