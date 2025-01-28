package com.uni.notifications.domain.messages;

import lombok.Data;


//    consumed message {"followerId":"alin","followeeId":"test","action":"FOLLOW","timestamp":1737780647.898526000}

@Data
public class FollowEvent {
  private String followerId;
  private String followeeId;
  private FollowEventAction action;
  private Double timestamp;

  public String getNotificationContent() {
    return switch (this.action) {
      case FOLLOW -> String.format("@%s has followed you", followerId);
      case UNFOLLOW -> String.format("@%s has unfollowed you", followerId);
    };
  }

}
