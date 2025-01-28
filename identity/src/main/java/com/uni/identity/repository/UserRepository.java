package com.uni.identity.repository;

import com.uni.identity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);

  List<User> findByUsernameContainingOrDisplayNameContaining(String username, String displayName);

  List<User> findByUsernameIn(Collection<String> usernames);
}
