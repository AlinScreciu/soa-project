package com.uni.identity.entity;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse extends UserDto {
  private final String token;


  public LoginResponse(User user, String token) {
    super(user);
    this.token = token;
  }
}