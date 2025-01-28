package com.uni.notifications.domain.messages;


import lombok.Data;

//Received message from rabbitmq queue: {"followerId":"test","userId":"alin",
// "postId":26}
@Data
public class PostEvent {

  private String userId;
  private Long postId;

}
