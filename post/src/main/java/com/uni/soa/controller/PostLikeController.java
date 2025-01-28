package com.uni.soa.controller;

import com.uni.soa.entity.PostLike;
import com.uni.soa.entity.dto.PostLikeDTO;
import com.uni.soa.service.PostLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/posts")
public class PostLikeController {

  private final PostLikeService postLikeService;

  @Autowired
  public PostLikeController(PostLikeService postLikeService) {
    this.postLikeService = postLikeService;
  }

  @PostMapping("/{postId}/like")
  public void likePost(@PathVariable Long postId) {
    postLikeService.likePost(postId);
  }

  @DeleteMapping("/{postId}/like")
  public void removeLike(@PathVariable Long postId) {
    postLikeService.removeLike(postId);
  }



}
