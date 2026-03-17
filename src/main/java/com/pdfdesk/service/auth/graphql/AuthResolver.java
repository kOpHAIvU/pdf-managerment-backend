package com.pdfdesk.service.auth.graphql;

import com.pdfdesk.service.auth.dto.AuthPayload;
import com.pdfdesk.service.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthResolver {
  private final AuthService authService;

  @MutationMapping
  public AuthPayload login(@Argument String email, @Argument String password) {
    return authService.login(email, password);
  }

  @MutationMapping
  public AuthPayload loginWithGoogle(@Argument String idToken) {
    return authService.loginWithGoogle(idToken);
  }

  @MutationMapping
  public AuthPayload register(@Argument String fullName, @Argument String email, @Argument String password) {
    return authService.register(fullName, email, password);
  }
}
