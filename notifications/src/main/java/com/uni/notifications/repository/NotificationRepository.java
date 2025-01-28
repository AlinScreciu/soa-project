package com.uni.notifications.repository;

import com.uni.notifications.domain.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
  // Example: find all notifications for a user that haven't been delivered yet
  List<NotificationEntity> findByUserIdAndDeliveredFalse(String userId);

  ArrayList<NotificationEntity> findAllByUserIdAndDeliveredFalse(String userId);
}
