package com.uni.notifications.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "notifications")
public class NotificationEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String userId;      // The user who should receive this notification
  private String content;     // Notification text or payload

  private boolean delivered;  // Flag to indicate if the notification was delivered

  // Optional: Store a creation timestamp
  private Instant createdAt;

  public NotificationEntity() {
    this.createdAt = Instant.now();
  }

  public NotificationEntity(String userId, String content, boolean delivered) {
    this.userId = userId;
    this.content = content;
    this.delivered = delivered;
    this.createdAt = Instant.now();
  }
}
