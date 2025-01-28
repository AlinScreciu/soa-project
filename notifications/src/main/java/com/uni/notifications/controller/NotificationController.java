package com.uni.notifications.controller;

import com.uni.notifications.domain.entity.NotificationEntity;
import com.uni.notifications.repository.NotificationRepository;
import com.uni.notifications.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {


  private final NotificationService notificationService;

  @Autowired
  public NotificationController(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @PostMapping("/trigger")
  public void postNotification(@RequestBody NotificationEntity notification) {
    notificationService.tryToSendNotification(notification);
  }

}
