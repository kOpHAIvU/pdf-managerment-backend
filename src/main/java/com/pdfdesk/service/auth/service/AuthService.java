package com.pdfdesk.service.auth.service;

import com.pdfdesk.service.auth.dto.AuthPayload;
import com.pdfdesk.service.google.service.GoogleService;
import com.pdfdesk.service.security.JwtService;
import com.pdfdesk.service.users.model.User;
import com.pdfdesk.service.users.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {
  private final UserRepository userRepository;
  private final JwtService jwtService;
  private final GoogleService googleService;

  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  public AuthService(UserRepository userRepository,
                     JwtService jwtService,
                     GoogleService googleService) {
    this.userRepository = userRepository;
    this.jwtService = jwtService;
    this.googleService = googleService;
  }

  public AuthPayload login(String email, String password) {

    User user = userRepository.findByEmail(email);

    if (user == null)
      throw new RuntimeException("User not found");

    if (!encoder.matches(password, user.getPasswordHash()))
      throw new RuntimeException("Invalid password");

    String token = jwtService.generateToken(user);

    return new AuthPayload(token, user);
  }

  public AuthPayload loginWithGoogle(String idToken) {

    String email = googleService.verify(idToken);

    User user = userRepository.findByEmail(email);

    if (user == null) {

      user = new User();

      user.setUserId(UUID.randomUUID().toString());
      user.setEmail(email);
      user.setProvider("GOOGLE");
      user.setCreatedAt(Instant.now().toString());

      userRepository.save(user);
    }

    String token = jwtService.generateToken(user);

    return new AuthPayload(token, user);
  }

  public AuthPayload register(String email, String password) {

    User user = new User();

    user.setUserId(UUID.randomUUID().toString());
    user.setEmail(email);
    user.setPasswordHash(encoder.encode(password));
    user.setProvider("LOCAL");
    user.setCreatedAt(Instant.now().toString());

    userRepository.save(user);

    String token = jwtService.generateToken(user);

    return new AuthPayload(token, user);
  }
}