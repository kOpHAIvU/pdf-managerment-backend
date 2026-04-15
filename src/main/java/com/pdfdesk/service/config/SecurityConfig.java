package com.pdfdesk.service.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.SecurityFilterChain;
import com.pdfdesk.service.security.JwtAuthenticationFilter;
import com.pdfdesk.service.security.RateLimitingFilter;

@Configuration
public class SecurityConfig {
  @Value("${cors.allowed-origins:http://localhost:3000}")
  private String allowedOrigins;

  private final RateLimitingFilter rateLimitingFilter;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  public SecurityConfig(RateLimitingFilter rateLimitingFilter, JwtAuthenticationFilter jwtAuthenticationFilter) {
    this.rateLimitingFilter = rateLimitingFilter;
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.cors(cors -> {})
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .headers(headers -> headers
            .xssProtection(xss -> {})
            .contentTypeOptions(options -> {})
            .frameOptions(frame -> frame.sameOrigin()))
        .authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers(
                            "/api/auth/**",
                            "/graphql",
                            "/graphiql",
                            "/oauth2/**",
                            "/actuator/health",
                            "/actuator/info"
                    ).permitAll()
                    .anyRequest().authenticated()
            )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(rateLimitingFilter, JwtAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowCredentials(true);
    configuration.setAllowedOrigins(parseAllowedOrigins());
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setExposedHeaders(List.of("Authorization"));
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  private List<String> parseAllowedOrigins() {
    return Arrays.stream(allowedOrigins.split(","))
        .map(String::trim)
        .filter(origin -> !origin.isEmpty())
        .toList();
  }
}