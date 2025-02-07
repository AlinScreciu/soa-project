package com.uni.notifications.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uni.notifications.domain.entity.NotificationEntity;
import com.uni.notifications.domain.messages.FollowEvent;
import com.uni.notifications.domain.messages.PostEvent;
import com.uni.notifications.repository.NotificationRepository;
import com.uni.notifications.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

  private static final Logger logger = LoggerFactory.getLogger(NotificationConsumer.class);



  private final ObjectMapper objectMapper = new ObjectMapper();
  private final FollowService followService;
  private final NotificationService notificationService;

  public NotificationConsumer(FollowService followService,
                              NotificationService notificationService) {
    this.followService = followService;
    this.notificationService = notificationService;
  }

//  @Autowired
//   // or any data store

  @KafkaListener(topics = "follow-events", groupId = "notification-service")
  public void handleFollowEventFromKafka(String messagePayload) throws JsonProcessingException {
    logger.info("consumed message {}", messagePayload);
    FollowEvent followEvent;
    try {
      followEvent = objectMapper.readValue(messagePayload, FollowEvent.class);
//    consumed message {"followerId":"alin","followeeId":"test",
//    "action":"FOLLOW","timestamp":1737780647.898526000}
//     1. Parse event to get userId & notification content
    } catch (Exception e) {
      logger.error(e.getMessage());
      return;
    }
    NotificationEntity notification = new NotificationEntity();
    notification.setDelivered(false);
    notification.setUserId(followEvent.getFolloweeId());
    notification.setContent(followEvent.getNotificationContent());

    notificationService.tryToSendNotification(notification);
  }

  @RabbitListener(queues = "post-notifications-queue")
  public void handleEventFromRabbit(String messagePayload) {
    logger.info("Received message from rabbitmq queue: {}", messagePayload);
    PostEvent postEvent;
    try {
      postEvent = objectMapper.readValue(messagePayload, PostEvent.class);

    } catch (Exception e) {
      logger.error(e.getMessage());
      return;
    }


    followService.getFollowersByUserId(postEvent.getUserId()).forEach(follower -> {

      NotificationEntity notification = new NotificationEntity();
      notification.setDelivered(false);
      notification.setUserId(follower);
      notification.setContent(String.format("@%s posted something new, check his profile!", postEvent.getUserId()));
      notificationService.tryToSendNotification(notification);

    });

  }


}
