package com.pdfdesk.service.users.controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import com.pdfdesk.service.users.model.User;
import com.pdfdesk.service.users.service.UserService;

@Controller
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @MutationMapping
  public void createUser(@Argument User user) {
    userService.createUser(user);
  }
//
//  @QueryMapping
//  public User getUser(@Argument String id) {
//    return userService.getUser(id);
//  }
//
//  @MutationMapping
//  public Boolean deleteUser(@Argument String id) {
//    userService.deleteUser(id);
//    return true;
//  }
}