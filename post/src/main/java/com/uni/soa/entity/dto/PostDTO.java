package com.uni.soa.entity.dto;

import com.uni.soa.entity.Post;
import lombok.Data;

@Data
public class PostDTO {
  private Long id;
  private String userId;
  private String displayUsername;
  private String userAvatar;
  private String content;
  private String createdAt;
  private Integer likesCount;
  private Boolean liked;
  private Boolean following;

  public PostDTO(Post post){
    this.id = post.getId();
    this.userId = post.getUserId();
    this.content = post.getContent();
    this.createdAt = post.getCreatedAt();
    this.likesCount = post.getLikesCount();
  }

}
