package com.pdfdesk.service.security;

import com.pdfdesk.service.users.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

  private final String SECRET = "secret-key";

  public String generateToken(User user) {

    return Jwts.builder()
            .setSubject(user.getId())
            .claim("email", user.getEmail())
            .setIssuedAt(new Date())
            .signWith(SignatureAlgorithm.HS256, SECRET)
            .compact();
  }
}