package com.uni.soa.repository;

import com.uni.soa.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
  List<Post> findAllByUserId(String userId);
}
