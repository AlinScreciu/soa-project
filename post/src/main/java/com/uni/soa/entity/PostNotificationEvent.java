package com.uni.soa.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostNotificationEvent {
  private String userId;
  private Long postId;

}
