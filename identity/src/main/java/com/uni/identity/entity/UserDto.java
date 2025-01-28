package com.uni.identity.entity;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserDto {
  private final String username;
  private final String displayName;
  private final String contact;
  private final LocalDateTime createdAt;
  private final String profilePic;

  public UserDto(User user) {
    this.username = user.getUsername();
    this.displayName = user.getDisplayName();
    this.contact = user.getContact();
    this.createdAt = user.getCreatedAt();
    this.profilePic = String.format("https://picsum.photos/seed/%s/256/256", this.username);
  }

}
