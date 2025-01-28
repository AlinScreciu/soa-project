package com.uni.soa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Post {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Size(max = 256)
  private String content;

  @Column(name = "user_id")
  private String userId;

  private String createdAt;

  private String updatedAt;

  @Column(columnDefinition = "int default 0")
  private Integer likesCount = 0;

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<PostLike> likes;
  // Add any other relevant fields (e.g., tags, mediaUrl, etc.)
}

