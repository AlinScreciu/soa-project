package com.uni.soa.repository;

import com.uni.soa.entity.PostLike;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
  boolean existsByUserIdAndPostId(String userId, Long postId);
  List<PostLike> findByPostId(Long postId); // Get all likes for a post
  List<PostLike> findByUserId(String userId); // Get all likes for a user


  void deleteByUserIdAndPostId(String userId, Long postId);
}
