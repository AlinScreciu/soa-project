package com.uni.follow.entity.events;

import lombok.Data;

import java.time.Instant;

@Data
public class FollowEvent {
  private String followerId;
  private String followeeId;
  private FollowEventActionType action; // e.g. "FOLLOW" or "UNFOLLOW"
  private Instant timestamp;

  public FollowEvent() {
  }

  public FollowEvent(String followerId, String followeeId, FollowEventActionType action, Instant timestamp) {
    this.followerId = followerId;
    this.followeeId = followeeId;
    this.action = action;
    this.timestamp = timestamp;
  }

  // Getters and setters...

  @Override
  public String toString() {
    return "FollowEvent{" +
            "followerId='" + followerId + '\'' +
            ", followeeId='" + followeeId + '\'' +
            ", action='" + action + '\'' +
            ", timestamp=" + timestamp +
            '}';
  }

  public enum FollowEventActionType {
    FOLLOW, UNFOLLOW
  }
}
