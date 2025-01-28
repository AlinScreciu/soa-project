package com.uni.notifications.service;

import com.uni.notifications.config.TokenHolder;
import com.uni.notifications.domain.entity.NotificationEntity;
import com.uni.notifications.repository.NotificationRepository;
import com.uni.notifications.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class UserSessionService implements ApplicationListener<SessionDisconnectEvent> {

  private final ConcurrentMap<String, String> sessionIdToUserId = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, String> userIdToSessionId = new ConcurrentHashMap<>();
  private static final Logger logger =
          LoggerFactory.getLogger(UserSessionService.class);
  private final TokenHolder tokenHolder;
  private final NotificationRepository notificationRepository;
  private final SimpMessagingTemplate simpMessagingTemplate;

  public UserSessionService(TokenHolder tokenHolder, NotificationRepository notificationRepository,
                            SimpMessagingTemplate simpMessagingTemplate) {
    this.tokenHolder = tokenHolder;
    this.notificationRepository = notificationRepository;
    this.simpMessagingTemplate = simpMessagingTemplate;
  }

  @EventListener
  public void handleSessionConnectEvent(SessionConnectEvent event) {
    // 1. Extract userId from the event or from the StompHeaderAccessor

    StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
    String sessionId = sha.getSessionId();

    String userId = SecurityUtils.getAuthenticatedUserId();
    logger.info("New session {} from {}", sessionId, userId);
    sessionIdToUserId.put(sessionId, userId);
    userIdToSessionId.put(userId, sessionId);

    ArrayList<NotificationEntity> unreadList = notificationRepository.findAllByUserIdAndDeliveredFalse(userId);

    for (NotificationEntity notificationEntity : unreadList) {
      simpMessagingTemplate.convertAndSendToUser(
              userId,
              "/queue/notifications",
              notificationEntity.getContent()
      );
      notificationEntity.setDelivered(true);
    }

    notificationRepository.saveAll(unreadList);
  }

  @Override
  public void onApplicationEvent(SessionDisconnectEvent event) {
    String sessionId = event.getSessionId();
    String userId = sessionIdToUserId.remove(sessionId);
    logger.info("Removing user {} from session {}", userId, sessionId);
    if (userId != null) {
      userIdToSessionId.remove(userId);
      tokenHolder.removeToken(userId);
    }
  }

  public boolean isUserOnline(String userId) {
    return userIdToSessionId.containsKey(userId);
  }

  public String getSessionIdForUser(String userId) {
    return userIdToSessionId.get(userId);
  }
}
