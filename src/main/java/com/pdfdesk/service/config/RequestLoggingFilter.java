package com.pdfdesk.service.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    long start = System.currentTimeMillis();
    filterChain.doFilter(request, response);
    long durationMs = System.currentTimeMillis() - start;
    log.info(
        "http_request method={} path={} status={} durationMs={} remoteIp={}",
        request.getMethod(),
        request.getRequestURI(),
        response.getStatus(),
        durationMs,
        request.getRemoteAddr()
    );
  }
}
