package com.pdfdesk.service.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentUserProvider {
  private final HttpServletRequest request;
  private final JwtService jwtService;

  public String getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof String principal) {
      return principal;
    }
    String header = request.getHeader("Authorization");
    if (header == null || !header.startsWith("Bearer ")) {
      return null;
    }
    return jwtService.extractUserId(header.substring(7));
  }
}
