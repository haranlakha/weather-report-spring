package com.example.weatherreportspring;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;

/**
 * Limits requests per client IP and in total, in fixed one-minute windows, so the
 * OpenWeatherMap API key's quota can't be used up by a single client or a flood of them.
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    private static final long WINDOW_MILLIS = 60_000;

    private final int perClientLimit;
    private final int totalLimit;
    private final Clock clock;

    // Only clients that were let through get an entry, so this holds at most totalLimit entries
    private final Map<String, Integer> clientCounts = new HashMap<>();
    private int totalCount;
    private long currentWindow = -1;

    @Autowired
    public RateLimitInterceptor(@Value("${rate-limit.per-client-per-minute:10}") int perClientLimit,
                                @Value("${rate-limit.total-per-minute:50}") int totalLimit) {
        this(perClientLimit, totalLimit, Clock.systemUTC());
    }

    RateLimitInterceptor(int perClientLimit, int totalLimit, Clock clock) {
        this.perClientLimit = perClientLimit;
        this.totalLimit = totalLimit;
        this.clock = clock;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (tryAcquire(request.getRemoteAddr())) {
            return true;
        }
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setHeader("Retry-After", String.valueOf(secondsUntilNextWindow()));
        return false;
    }

    synchronized boolean tryAcquire(String clientId) {
        long window = clock.millis() / WINDOW_MILLIS;
        if (window != currentWindow) {
            currentWindow = window;
            clientCounts.clear();
            totalCount = 0;
        }

        int clientCount = clientCounts.getOrDefault(clientId, 0);
        if (totalCount >= totalLimit || clientCount >= perClientLimit) {
            return false;
        }
        clientCounts.put(clientId, clientCount + 1);
        totalCount++;
        return true;
    }

    private long secondsUntilNextWindow() {
        long millisLeft = WINDOW_MILLIS - clock.millis() % WINDOW_MILLIS;
        return (millisLeft + 999) / 1000;
    }
}
