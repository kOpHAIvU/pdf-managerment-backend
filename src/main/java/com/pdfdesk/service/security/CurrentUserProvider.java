package com.pdfdesk.service.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentUserProvider {
  private final HttpServletRequest request;
  private final JwtService jwtService;

  public String getCurrentUserId() {
    String header = request.getHeader("Authorization");
    if (header == null || !header.startsWith("Bearer ")) {
      return null;
    }
    return jwtService.extractUserId(header.substring(7));
  }
}
