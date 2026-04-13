package com.pdfdesk.service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {
  private final int capacity;
  private final int refillPerMinute;
  private final Map<String, BucketState> buckets = new ConcurrentHashMap<>();

  public RateLimitingFilter(
      @Value("${security.rate-limit.capacity:120}") int capacity,
      @Value("${security.rate-limit.refill-per-minute:120}") int refillPerMinute
  ) {
    this.capacity = capacity;
    this.refillPerMinute = refillPerMinute;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String key = request.getRemoteAddr();
    BucketState state = buckets.computeIfAbsent(key, ignored -> new BucketState(capacity, Instant.now().getEpochSecond()));
    synchronized (state) {
      refill(state);
      if (state.tokens <= 0) {
        response.setStatus(429);
        response.getWriter().write("Rate limit exceeded");
        return;
      }
      state.tokens -= 1;
    }
    filterChain.doFilter(request, response);
  }

  private void refill(BucketState state) {
    long now = Instant.now().getEpochSecond();
    long elapsedSeconds = Math.max(0, now - state.lastRefillEpochSeconds);
    if (elapsedSeconds == 0) {
      return;
    }
    int refillTokens = (int) ((elapsedSeconds * refillPerMinute) / 60);
    if (refillTokens > 0) {
      state.tokens = Math.min(capacity, state.tokens + refillTokens);
      state.lastRefillEpochSeconds = now;
    }
  }

  private static final class BucketState {
    private int tokens;
    private long lastRefillEpochSeconds;

    private BucketState(int tokens, long lastRefillEpochSeconds) {
      this.tokens = tokens;
      this.lastRefillEpochSeconds = lastRefillEpochSeconds;
    }
  }
}
