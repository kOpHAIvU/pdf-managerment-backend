package com.pdfdesk.service.users.controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import lombok.RequiredArgsConstructor;

import com.pdfdesk.service.users.model.User;
import com.pdfdesk.service.users.repository.UserRepository;
import com.pdfdesk.service.users.service.UserService;
import com.pdfdesk.service.security.CurrentUserProvider;

@Controller
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final UserRepository userRepository;
  private final CurrentUserProvider currentUserProvider;

  @MutationMapping
  public void createUser(@Argument User user) {
    userService.createUser(user);
  }

  @QueryMapping
  public User me() {
    String userId = currentUserProvider.getCurrentUserId();
    if (userId == null) {
      return null;
    }
    return userRepository.findById(userId);
  }
}