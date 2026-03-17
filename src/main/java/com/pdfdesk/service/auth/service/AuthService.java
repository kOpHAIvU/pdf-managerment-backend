package com.pdfdesk.service.auth.service;

import com.pdfdesk.constants.AuthErrorCode;
import com.pdfdesk.service.auth.dto.AuthPayload;
import com.pdfdesk.service.auth.exception.AuthException;
import com.pdfdesk.service.google.service.GoogleService;
import com.pdfdesk.service.security.JwtService;
import com.pdfdesk.service.users.model.User;
import com.pdfdesk.service.users.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

@Service
@Slf4j
public class AuthService {
  public static final String SIGN_IN_TYPE_EMAIL_PASSWORD = "EMAIL_PASSWORD";
  public static final String SIGN_IN_TYPE_GOOGLE_OAUTH = "GOOGLE_OAUTH";

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
    String normalizedEmail = normalizeEmail(email);
    User user = userRepository.findByEmail(normalizedEmail);

    if (user == null)
      throw new AuthException(AuthErrorCode.USER_NOT_FOUND);

    if (!SIGN_IN_TYPE_EMAIL_PASSWORD.equals(user.getProvider()))
      throw new AuthException(AuthErrorCode.IS_GOOGLE_PROVIDER);

    if (user.getPasswordHash() == null || !encoder.matches(password, user.getPasswordHash()))
      throw new AuthException(AuthErrorCode.INVALID_PASSWORD);

    return new AuthPayload(jwtService.generateToken(user), user);
  }

  public AuthPayload loginWithGoogle(String idToken) {
    var info = googleService.verifyAndGetSubAndEmail(idToken);
    String email = normalizeEmail(info.email());
    String fullName = normalizeFullName(info.name(), email);
    User user = userRepository.findByEmail(email);

    if (user == null) {
      user = new User();
      user.setId(UUID.randomUUID().toString());
      user.setEmail(email);
      user.setFullName(fullName);
      user.setProvider(SIGN_IN_TYPE_GOOGLE_OAUTH);
      user.setCreatedAt(Instant.now().toString());
      userRepository.save(user);
    } else {
      if (!SIGN_IN_TYPE_GOOGLE_OAUTH.equals(user.getProvider()))
        throw new AuthException(AuthErrorCode.IS_GMAIL_PASS_PROVIDER);
      if (user.getFullName() == null || user.getFullName().isBlank()) {
        user.setFullName(fullName);
      }
      userRepository.save(user);
    }

    return new AuthPayload(jwtService.generateToken(user), user);
  }

  public AuthPayload register(String fullName, String email, String password) {
    String normalizedEmail = normalizeEmail(email);
    User existing = userRepository.findByEmail(normalizedEmail);
    if (existing != null)
      throw new AuthException(AuthErrorCode.USED_EMAIL);

    User user = new User();
    user.setId(UUID.randomUUID().toString());
    user.setEmail(normalizedEmail);
    user.setFullName(normalizeFullName(fullName, normalizedEmail));
    user.setPasswordHash(encoder.encode(password));
    user.setProvider(SIGN_IN_TYPE_EMAIL_PASSWORD);
    user.setCreatedAt(Instant.now().toString());
    userRepository.save(user);

    return new AuthPayload(jwtService.generateToken(user), user);
  }

  private String normalizeEmail(String email) {
    if (email == null)
      return null;
    return email.trim().toLowerCase(Locale.ROOT);
  }

  private String normalizeFullName(String fullName, String email) {
    if (fullName != null && !fullName.isBlank()) {
      return fullName.trim();
    }
    if (email == null || email.isBlank()) {
      return null;
    }
    int delimiterIndex = email.indexOf('@');
    if (delimiterIndex <= 0) {
      return email;
    }
    return email.substring(0, delimiterIndex);
  }
}