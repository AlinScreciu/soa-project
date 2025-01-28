package com.uni.identity.controller;

import com.uni.identity.entity.LoginResponse;
import com.uni.identity.entity.User;
import com.uni.identity.entity.UserDto;
import com.uni.identity.jwt.JwtUtil;
import com.uni.identity.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/auth")
public class AuthController {
  private final UserService userService;
  private final JwtUtil jwtUtil;

  public AuthController(UserService userService, JwtUtil jwtUtil) {
    this.userService = userService;
    this.jwtUtil = jwtUtil;
  }

  @PostMapping("/register")
  public ResponseEntity<UserDto> register(@RequestBody User user) {

    return ResponseEntity.ok(new UserDto(userService.register(user)));
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody Map<String, String> request) {
    String username = request.get("username");
    String password = request.get("password");
    User user = userService.authenticate(username, password);
    String token = jwtUtil.generateToken(user.getUsername());
    return ResponseEntity.ok(new LoginResponse(user, token));
  }
}
