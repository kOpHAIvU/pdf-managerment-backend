package com.pdfdesk.service.auth.graphql;

import com.pdfdesk.service.auth.dto.AuthPayload;
import com.pdfdesk.service.auth.service.AuthService;

public class AuthResolver {
  private final AuthService authService;

  public AuthResolver(AuthService authService) {
    this.authService = authService;
  }

  public AuthPayload login(String email, String password) {
    return authService.login(email, password);
  }

  public AuthPayload loginWithGoogle(String idToken) {
    return authService.loginWithGoogle(idToken);
  }

  public AuthPayload register(String email, String password) {
    return authService.register(email, password);
  }
}
