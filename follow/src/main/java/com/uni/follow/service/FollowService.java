package com.uni.follow.service;


import com.uni.follow.entity.Follow;

import com.uni.follow.entity.events.FollowEvent;
import com.uni.follow.repository.FollowRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class FollowService {

  private final FollowRepository followRepository;
  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final Logger logger = LoggerFactory.getLogger(FollowService.class);

  private static final String FOLLOW_TOPIC = "follow-events";


  public FollowService(FollowRepository followRepository, KafkaTemplate<String, Object> kafkaTemplate) {
    this.followRepository = followRepository;
    this.kafkaTemplate = kafkaTemplate;
  }

  public void followUser(String followerId, String followeeId) {
    followRepository.save(new Follow(followerId, followeeId));

    FollowEvent event =
            new FollowEvent(followerId, followeeId, FollowEvent.FollowEventActionType.FOLLOW, Instant.now());
    kafkaTemplate.send(FOLLOW_TOPIC, event);
  }

@Transactional
public void unfollowUser(String followerId, String followeeId) {
  followRepository.deleteFollowByFolloweeAndFollower(followeeId, followerId);

  FollowEvent event =
          new FollowEvent(followerId, followeeId, FollowEvent.FollowEventActionType.UNFOLLOW, Instant.now());

  kafkaTemplate.send(FOLLOW_TOPIC, event).whenComplete((result, e) -> {
    if (e != null) {
      logger.error("Exception when sending follow kafka event: {}", e.getMessage());
      return;
    }

    logger.info("Kafka send follow event result producer record: {}, record metadata: {}", result.getProducerRecord(),
            result.getRecordMetadata());
  });

}

  public boolean isFollowing(String followerId, String followeeId) {
    return followRepository.existsByFollowerAndFollowee(followerId, followeeId);
  }

  public List<String> getFollowers(String followeeId) {
    return followRepository.findFollowersByFollowee(followeeId).stream().map(Follow::getFollower).toList();
  }
}
