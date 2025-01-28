package com.uni.soa.controller;


import com.uni.soa.entity.dto.PostLikeDTO;
import com.uni.soa.service.PostLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/likes")
public class LikesController {

  private final PostLikeService postLikeService;

  @Autowired
  public LikesController(PostLikeService postLikeService) {
    this.postLikeService = postLikeService;

  }

  @GetMapping("/post/{postId}")
  public List<PostLikeDTO> getPostLikes(@PathVariable Long postId, @RequestParam(required = false) String userId) {
    return postLikeService.getPostLikes(postId).stream()
            .filter(pl -> userId == null || pl.getUserId().equals(userId))
            .map(pl -> new PostLikeDTO(pl.getId(), pl.getUserId(), postId)).collect(Collectors.toList());
  }

  @GetMapping("/user/{userId}")
  public List<PostLikeDTO> getUserLikes(@PathVariable String userId) {
    return postLikeService.getPostsLikedByUserId(userId).stream().map(pl -> new PostLikeDTO(pl.getId(), pl.getUserId(), pl.getPost().getId())).collect(Collectors.toList());
  }

}
