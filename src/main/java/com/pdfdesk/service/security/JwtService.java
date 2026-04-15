package com.pdfdesk.service.security;

import com.pdfdesk.service.users.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class JwtService {
  private final SecretKey key;
  private final long expirationMs;

  public JwtService(
      @Value("${security.jwt.secret}") String secret,
      @Value("${security.jwt.expiration-ms:86400000}") long expirationMs
  ) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expirationMs = expirationMs;
  }

  public String generateToken(User user) {
    return Jwts.builder()
            .subject(user.getId())
            .claim("email", user.getEmail())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(key)
            .compact();
  }

  public String extractUserId(String token) {
    try {
      return Jwts.parser()
          .verifyWith(key)
          .build()
          .parseSignedClaims(token)
          .getPayload()
          .getSubject();
    } catch (Exception ex) {
      log.warn("Failed to parse JWT: {}", ex.getMessage());
      return null;
    }
  }
}