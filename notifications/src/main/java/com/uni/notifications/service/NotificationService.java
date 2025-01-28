package com.uni.notifications.service;

import com.uni.notifications.domain.entity.NotificationEntity;
import com.uni.notifications.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
  private final UserSessionService userSessionService;
  private final SimpMessagingTemplate messagingTemplate;
  private final Logger logger = LoggerFactory.getLogger(NotificationService.class);
  private final NotificationRepository notificationRepository;

  @Autowired
  public NotificationService(UserSessionService userSessionService, SimpMessagingTemplate messagingTemplate,
                             NotificationRepository notificationRepository) {
    this.userSessionService = userSessionService;
    this.messagingTemplate = messagingTemplate;
    this.notificationRepository = notificationRepository;
  }

  public void tryToSendNotification(NotificationEntity notification) {

    logger.info("Received notification for {}", notification.getUserId());

    String userId = notification.getUserId();
    if (userSessionService.isUserOnline(userId)) {
      logger.info("User {} is online, sending notification", userId);
      messagingTemplate.convertAndSendToUser(userId, "/queue/notifications",  // or your chosen destination
              notification.getContent());

      return;
    }
    logger.info("User {} is offline, saving notification", userId);
    notification.setDelivered(false);
    notificationRepository.save(notification);
  }
}
