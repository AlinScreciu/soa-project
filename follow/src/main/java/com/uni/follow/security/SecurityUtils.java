package com.uni.follow.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

  public static String getAuthenticatedUserId() {
    // Get the authentication object from the SecurityContext
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    // Get the user ID from the authentication object
    return (String) authentication.getPrincipal();
  }

}
