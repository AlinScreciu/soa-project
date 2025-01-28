package com.uni.soa.service;

import com.uni.soa.entity.Post;
import com.uni.soa.entity.PostNotificationEvent;
import com.uni.soa.repository.PostRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

  private final PostRepository postRepository;
  private final RabbitTemplate rabbitTemplate;


  @Autowired
  public PostService(PostRepository postRepository,
                     RabbitTemplate rabbitTemplate) {
    this.postRepository = postRepository;
    this.rabbitTemplate = rabbitTemplate;

  }

  public List<Post> getAllPosts() {
    return postRepository.findAll();
  }

  public Optional<Post> getPostById(Long id) {
    return postRepository.findById(id);
  }

  public List<Post> getPostsByUserId(String userId) {
    return postRepository.findAllByUserId(userId);
  }

  public List<Post> getBulkPosts(List<Long> idList) {
    return postRepository.findAllById(idList);
  }

  public Post createPost(Post post) {
    post.setCreatedAt(String.valueOf(System.currentTimeMillis()));
    Post savedPost = postRepository.save(post);

    // 2. Publish an event/message to RabbitMQ for each follower
    PostNotificationEvent event = new PostNotificationEvent(
            savedPost.getUserId(),
            savedPost.getId()
    );

    // Convert event to JSON or a specific message format
    rabbitTemplate.convertAndSend(
            "post-exchange",  // Exchange name
            "post.notification", // Routing key
            event
    );


    return savedPost;
  }

  public void deletePost(Long id) {
    postRepository.deleteById(id);
  }

  public Post updatePost(Long id, Post post) {
    return postRepository.findById(id)
            .map(existingPost -> {
              existingPost.setContent(post.getContent());
              existingPost.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
              return postRepository.save(existingPost);
            }).orElseThrow(() -> new RuntimeException("Post not found"));
  }
}
