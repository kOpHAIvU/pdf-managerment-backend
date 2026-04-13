package com.pdfdesk.service.security;

import com.pdfdesk.service.users.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Service
public class JwtService {
  private final String SECRET = "c2VjcmV0LWtleS1zZWNyZXQta2V5LXNlY3JldC1rZXktMTIzNDU2";
  private final SecretKey key = Keys.hmacShaKeyFor(
          java.util.Base64.getUrlDecoder().decode(SECRET)
  );

  public String generateToken(User user) {
    return Jwts.builder()
            .subject(user.getId())
            .claim("email", user.getEmail())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 86_400_000))
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