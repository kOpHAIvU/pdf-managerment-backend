package com.pdfdesk.service.google.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class GoogleService {
  public String verify(String idToken) {
    String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
    RestTemplate restTemplate = new RestTemplate();
    Map response = restTemplate.getForObject(url, Map.class);
    return (String) response.get("email");
  }
}
