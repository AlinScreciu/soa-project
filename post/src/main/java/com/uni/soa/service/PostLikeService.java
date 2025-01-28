package com.uni.soa.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uni.soa.entity.Post;
import com.uni.soa.entity.PostLike;
import com.uni.soa.entity.PostLikeNotificationEvent;
import com.uni.soa.entity.PostLikeType;
import com.uni.soa.repository.PostLikeRepository;
import com.uni.soa.repository.PostRepository;
import com.uni.soa.security.SecurityUtils;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.http.HttpMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Service
public class PostLikeService {

  private final PostLikeRepository postLikeRepository;
  private final PostRepository postRepository;

  private final Logger logger = LoggerFactory.getLogger(PostLikeService.class);

  @Value("${broker.url}")
  private String brokerUrl;
  private final ObjectMapper objectMapper;

  public PostLikeService(PostLikeRepository postLikeRepository, PostRepository postRepository,
                         ObjectMapper objectMapper) {
    this.postLikeRepository = postLikeRepository;
    this.postRepository = postRepository;
    this.objectMapper = objectMapper;
  }

  public void likePost(Long postId) {

    String userId = SecurityUtils.getAuthenticatedUserId();
    // Check if the user has already liked the post
    if (postLikeRepository.existsByUserIdAndPostId(userId, postId)) {
      throw new RuntimeException("User has already liked this post");
    }

    // Add the like
    Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

    // Update the like count in the post
    post.setLikesCount(post.getLikesCount() + 1);
    postRepository.save(post);

    // Create and save the like
    PostLike postLike = new PostLike();
    postLike.setUserId(userId);
    postLike.setPost(post);
    postLike.setCreatedAt(String.valueOf(System.currentTimeMillis()));
    postLikeRepository.save(postLike);

    PostLikeNotificationEvent postLikeNotificationEvent =
            new PostLikeNotificationEvent(userId, post.getUserId(), post.getId(), PostLikeType.LIKE);

    logger.info("Sending post like notification event to CloudEvent broker");

    if (!brokerUrl.isEmpty()) {
      sendCloudEvent(postLikeNotificationEvent, "post.like.notification");
    }
  }

  @Transactional
  public void removeLike(Long postId) {
    String userId = SecurityUtils.getAuthenticatedUserId();
    Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

    // Update the like count in the post
    post.setLikesCount(post.getLikesCount() - 1);
    postRepository.save(post);

    postLikeRepository.deleteByUserIdAndPostId(userId, postId);

    PostLikeNotificationEvent postLikeNotificationEvent =
            new PostLikeNotificationEvent(userId, post.getUserId(), post.getId(), PostLikeType.DISLIKE);

    logger.info("Sending post dislike notification event to CloudEvent broker");
    if (!brokerUrl.isEmpty()) {
      sendCloudEvent(postLikeNotificationEvent, "post.dislike.notification");
    }
  }

  private void sendCloudEvent(Object data, String type) {
    try {
      // Serialize the data object to JSON
      ObjectMapper objectMapper = new ObjectMapper();
      String jsonData = objectMapper.writeValueAsString(data);

      // Build the CloudEvent
      CloudEvent event = CloudEventBuilder.v1()
              .withId(UUID.randomUUID().toString())
              .withSource(URI.create("http://example.com/source"))
              .withType(type)
              .withDataContentType("application/json")
              .withData(jsonData.getBytes(StandardCharsets.UTF_8))
              .build();

      // Prepare the HTTP request
      HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
              .uri(URI.create(brokerUrl));

      // Use HttpMessageFactory to add headers and send the body
      HttpMessageFactory.createWriter(
              requestBuilder::header, // Lambda to add headers to the request
              body -> {
                try {
                  HttpRequest request = requestBuilder
                          .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                          .build();

                  // Send the HTTP request
                  HttpClient httpClient = HttpClient.newHttpClient();
                  HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                  if (response.statusCode() >= 200 && response.statusCode() < 300) {
                    logger.info("CloudEvent sent successfully. Status: {}", response.statusCode());
                  } else {
                    logger.error("Failed to send CloudEvent. Status: {}", response.statusCode());
                  }
                } catch (IOException | InterruptedException e) {
                  logger.error("Error sending CloudEvent HTTP request", e);
                  Thread.currentThread().interrupt();
                }
              }
      ).writeBinary(event); // Write the CloudEvent in binary mode
    } catch (Exception e) {
      logger.error("Failed to send CloudEvent", e);
    }
  }
  public List<PostLike> getPostsLikedByUserId(String userId) {
    return postLikeRepository.findByUserId(userId);
  }

  public List<PostLike> getPostLikes(Long postId) {
    return postLikeRepository.findByPostId(postId);
  }
}
