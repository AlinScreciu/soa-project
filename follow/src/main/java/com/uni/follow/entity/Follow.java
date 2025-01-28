package com.uni.follow.entity;

import jakarta.persistence.*;
import lombok.Data;

// Index this table by follower and followee
@Data
@Entity
@Table(name = "follows", uniqueConstraints = {
  @UniqueConstraint(columnNames = {"follower", "followee"})
}, indexes = {
  @Index(columnList = "follower"),
  @Index(columnList = "followee")
})
public class Follow {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String follower;
  private String followee;

  public Follow(String followerId, String followeeId) {
    this.follower = followerId;
    this.followee = followeeId;
  }

  public Follow() {

  }
}
