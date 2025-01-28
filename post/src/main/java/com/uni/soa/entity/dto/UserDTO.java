package com.uni.soa.entity.dto;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
  private final String username;
  private final String displayName;
  private final String contact;
  private final LocalDateTime createdAt;
  private final String profilePic;
}