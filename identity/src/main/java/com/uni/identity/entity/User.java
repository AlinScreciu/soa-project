package com.uni.identity.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {
  @Id
  private String username;

  private String password;  // Hash this using a library like BCrypt
  private String displayName;
  private String contact;
  private String profilePic;
  private LocalDateTime createdAt = LocalDateTime.now();
}
