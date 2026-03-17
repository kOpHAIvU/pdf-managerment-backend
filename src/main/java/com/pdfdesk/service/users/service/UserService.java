package com.pdfdesk.service.users.service;

import com.pdfdesk.service.users.model.User;
import com.pdfdesk.service.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository repository;

  public void createUser(User user) {
    repository.save(user);
  }
}