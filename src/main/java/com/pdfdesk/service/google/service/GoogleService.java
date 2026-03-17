package com.pdfdesk.service.google.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Locale;
import java.util.Map;

@Service
@Slf4j
public class GoogleService {

  @Value("${google.client-id:}")
  private String clientId;

  private final RestTemplate restTemplate = new RestTemplate();

  public String verify(String idToken) {
    GoogleTokenInfo info = verifyAndGetSubAndEmail(idToken);
    return info.email;
  }

  public GoogleTokenInfo verifyAndGetSubAndEmail(String idToken) {
    String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
    Map<String, Object> response = restTemplate.getForObject(url, Map.class);
    if (response == null)
      throw new RuntimeException("Invalid Google id_token.");

    if (clientId != null && !clientId.isBlank()) {
      Object aud = response.get("aud");
      if (aud == null || !clientId.equals(aud.toString()))
        throw new RuntimeException("Google id_token audience does not match this application.");
    }

    Object email = response.get("email");
    if (email == null || email.toString().isBlank())
      throw new RuntimeException("Google id_token has no email.");
    Object emailVerified = response.get("email_verified");
    if (emailVerified != null && !"true".equalsIgnoreCase(emailVerified.toString()))
      throw new RuntimeException("Google account email is not verified.");
    Object name = response.get("name");
    if ((name == null || name.toString().isBlank()) && response.get("given_name") != null) {
      name = response.get("given_name");
    }

    return new GoogleTokenInfo(
            email.toString().trim().toLowerCase(Locale.ROOT),
            name != null ? name.toString().trim() : null
    );
  }

  public record GoogleTokenInfo(String email, String name) {}
}
