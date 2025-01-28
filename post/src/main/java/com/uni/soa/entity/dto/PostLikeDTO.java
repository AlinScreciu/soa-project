package com.uni.soa.entity.dto;

import lombok.Data;

@Data
public class PostLikeDTO {
  private Long id;
  private String userId;
  private Long postId;

  public PostLikeDTO(Long id, String userId, Long postId) {
    this.id = id;
    this.userId = userId;
    this.postId = postId;
  }


  // Constructor, getters, and setters
}
