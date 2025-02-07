package com.uni.soa.controller;

import com.uni.soa.entity.FeedType;
import com.uni.soa.entity.Post;
import com.uni.soa.entity.PostLike;
import com.uni.soa.entity.dto.PostDTO;
import com.uni.soa.entity.dto.UserDTO;
import com.uni.soa.security.SecurityUtils;
import com.uni.soa.service.FollowService;
import com.uni.soa.service.PostLikeService;
import com.uni.soa.service.PostService;
import com.uni.soa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/posts")
public class PostController {

  private final PostService postService;
  private final UserService userService;
  private final FollowService followService;
  private final PostLikeService postLikeService;

  @Autowired
  public PostController(PostService postService, UserService userService, FollowService followService,
                        PostLikeService postLikeService) {
    this.postService = postService;
    this.userService = userService;
    this.followService = followService;
    this.postLikeService = postLikeService;
  }

  @GetMapping(params = "feedType")
  public List<PostDTO> getAllPosts(@RequestParam FeedType feedType) {
    /*TODO: When follow service is implemented, get the posts of the users that the authenticated user follows
     *  and the authenticated user's posts.
     * If the feedType is ALL, return all posts.
     */
    String authenticatedUserId = SecurityUtils.getAuthenticatedUserId();

    List<Post> allPosts = postService.getAllPosts().stream().filter(post -> {
      switch (feedType) {
        case FOLLOWING -> {
          return post.getUserId().equals(authenticatedUserId) || followService.isFollowing(authenticatedUserId, post.getUserId());
        }

        default -> {
          return true;
        }
      }
    }).toList();

    return allPosts.stream().map(p -> populatePostDTO(p, authenticatedUserId)).toList();

  }

  @GetMapping(params = "userId")
  public List<PostDTO> getAllPostsByUserId(@RequestParam String userId) {
    String authenticatedUserId = SecurityUtils.getAuthenticatedUserId();

    return postService.getPostsByUserId(userId).stream().map(p -> populatePostDTO(p, authenticatedUserId)).toList();
  }

  private PostDTO populatePostDTO(Post post, String authenticatedUserId) {
    UserDTO user = userService.getUser(post.getUserId());
    PostDTO postDTO = new PostDTO(post);
    postDTO.setDisplayUsername(user.getDisplayName());
    postDTO.setUserAvatar(user.getProfilePic());

    //TODO Really inefficient, but it's fine for now, which means forever :)
    Boolean liked = post.getLikes().stream().anyMatch(postLike -> postLike.getUserId().equals(authenticatedUserId));
    postDTO.setLiked(liked);


    postDTO.setFollowing(followService.isFollowing(authenticatedUserId, post.getUserId()));


    return postDTO;
  }

  @GetMapping("/{id}")
  public PostDTO getPostById(@PathVariable Long id) {
    String authenticatedUserId = SecurityUtils.getAuthenticatedUserId();

    return postService.getPostById(id).map((post -> populatePostDTO(post, authenticatedUserId))).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
  }

  @PostMapping("/bulk")
  public List<PostDTO> getBulkPostsById(@RequestBody List<Long> idList) {
    String authenticatedUserId = SecurityUtils.getAuthenticatedUserId();

    return idList.stream().map(postLikeService::getById).map(PostLike::getPost).map(post -> populatePostDTO(post,
            authenticatedUserId)).toList();
  }

  @PostMapping
  public Post createPost(@RequestBody Post post) {

    // Set the user ID to the post object
    post.setUserId(SecurityUtils.getAuthenticatedUserId());
    return postService.createPost(post);
  }

  @PutMapping("/{id}")
  public Post updatePost(@PathVariable Long id, @RequestBody Post post) {

    if (!post.getUserId().equals(SecurityUtils.getAuthenticatedUserId())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only the author` can update posts");
    }

    return postService.updatePost(id, post);
  }

  @DeleteMapping("/{id}")
  public Post deletePost(@PathVariable Long id) {

    Post post = postService.getPostById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

    if (!post.getUserId().equals(SecurityUtils.getAuthenticatedUserId())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authorized");
    }

    postService.deletePost(id);

    return post;
  }


}
