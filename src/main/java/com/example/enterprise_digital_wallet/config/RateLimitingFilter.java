package com.example.enterprise_digital_wallet.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS = 10;
    private static final long WINDOW_SECONDS = 60;

    private final Map<String, Deque<Long>> requestHistory = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        if (!isRateLimitedEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientKey = getClientKey(request);
        long now = Instant.now().getEpochSecond();

        requestHistory.putIfAbsent(clientKey, new ArrayDeque<>());

        Deque<Long> timestamps = requestHistory.get(clientKey);

        synchronized (timestamps) {
            while (!timestamps.isEmpty() && now - timestamps.peekFirst() >= WINDOW_SECONDS) {
                timestamps.pollFirst();
            }

            if (timestamps.size() >= MAX_REQUESTS) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("""
                        {
                          "success": false,
                          "message": "Too many requests. Please try again later.",
                          "data": null
                        }
                        """);
                return;
            }

            timestamps.addLast(now);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isRateLimitedEndpoint(HttpServletRequest request) {
        String method = request.getMethod();
        String uri = request.getRequestURI();

        return "POST".equalsIgnoreCase(method)
                && (
                uri.matches("/api/v1/wallets/users/.*/deposit")
                        || uri.matches("/api/v1/wallets/users/.*/withdraw")
                        || uri.equals("/api/v1/transactions/transfer")
        );
    }

    private String getClientKey(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");

        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}