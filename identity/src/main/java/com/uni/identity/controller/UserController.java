package com.uni.identity.controller;

import com.uni.identity.entity.User;
import com.uni.identity.entity.UserDto;
import com.uni.identity.repository.UserRepository;
import com.uni.identity.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/users")
public class UserController {
  private final UserService userService;
  private final UserRepository userRepository;

  public UserController(UserService userService, UserRepository userRepository) {
    this.userService = userService;
    this.userRepository = userRepository;
  }

  @GetMapping("/{username}")
  public ResponseEntity<UserDto> getUserInfo(@PathVariable String username) {
    User user = userService.getUserByUsername(username).orElseThrow();
    return ResponseEntity.ok(new UserDto(user));
  }

  @PostMapping("/bulk-info")
  public ResponseEntity<List<UserDto>> getUsersInfo(@RequestBody List<String> usernames) {
    List<User> users = userRepository.findByUsernameIn(usernames);
    List<UserDto> userDtos = users.stream().map(UserDto::new).toList();
    return ResponseEntity.ok(userDtos);
  }

  @GetMapping("/search")
  public ResponseEntity<List<UserDto>> searchUsers(@RequestParam String query) {
    List<User> users = userRepository.findByUsernameContainingOrDisplayNameContaining(query, query);
    List<UserDto> userDtos = users.stream().map(UserDto::new).toList();
    return ResponseEntity.ok(userDtos);
  }


}
