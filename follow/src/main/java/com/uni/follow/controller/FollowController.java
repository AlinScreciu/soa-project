package com.uni.follow.controller;


import com.uni.follow.security.SecurityUtils;
import com.uni.follow.service.FollowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/follow")
public class FollowController {
  public final FollowService followService;
  private final Logger logger = LoggerFactory.getLogger(FollowController.class);
  public FollowController(FollowService followService) {
    this.followService = followService;
  }

  @PostMapping("/{followeeId}/follow")
  public void followUser(@PathVariable String followeeId) {
    String followerId = SecurityUtils.getAuthenticatedUserId();
    followService.followUser(followerId, followeeId);
  }

  @DeleteMapping("/{followeeId}/follow")
  public void unfollowUser(@PathVariable String followeeId) {
    String followerId = SecurityUtils.getAuthenticatedUserId();
    followService.unfollowUser(followerId, followeeId);
  }

  @GetMapping("/{followeeId}/is-following/{followerId}")
  public boolean isFollowing(@PathVariable String followeeId, @PathVariable String followerId) {
    return followService.isFollowing(followerId, followeeId);
  }

  @GetMapping("/{userId}/followers")
  public List<String> getFollowers(@PathVariable String userId) {
    return followService.getFollowers(userId);
  }

}
