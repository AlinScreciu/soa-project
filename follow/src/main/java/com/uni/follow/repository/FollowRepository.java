package com.uni.follow.repository;

import com.uni.follow.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface FollowRepository extends JpaRepository<Follow, Long> {


  void deleteFollowByFolloweeAndFollower(String followee, String follower);

  boolean existsByFollowerAndFollowee(String followerId, String followeeId);

  List<Follow> findFollowersByFollowee(String followeeId);
}