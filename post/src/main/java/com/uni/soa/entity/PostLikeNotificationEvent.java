package com.uni.soa.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostLikeNotificationEvent {
  private String userId;
  private String postAuthorId;
  private Long postId;
  private PostLikeType type;
}
