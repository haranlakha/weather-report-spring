package com.example.weatherreportspring;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimitInterceptorTest {

    private final MutableClock clock = new MutableClock(Instant.parse("2026-01-01T12:00:00Z"));

    @Test
    void allowsRequestsUpToPerClientLimit() {
        RateLimitInterceptor limiter = new RateLimitInterceptor(3, 100, clock);

        assertThat(limiter.tryAcquire("1.1.1.1")).isTrue();
        assertThat(limiter.tryAcquire("1.1.1.1")).isTrue();
        assertThat(limiter.tryAcquire("1.1.1.1")).isTrue();
        assertThat(limiter.tryAcquire("1.1.1.1")).isFalse();
    }

    @Test
    void limitsEachClientSeparately() {
        RateLimitInterceptor limiter = new RateLimitInterceptor(1, 100, clock);

        assertThat(limiter.tryAcquire("1.1.1.1")).isTrue();
        assertThat(limiter.tryAcquire("1.1.1.1")).isFalse();
        assertThat(limiter.tryAcquire("2.2.2.2")).isTrue();
    }

    @Test
    void enforcesTotalLimitAcrossClients() {
        RateLimitInterceptor limiter = new RateLimitInterceptor(10, 2, clock);

        assertThat(limiter.tryAcquire("1.1.1.1")).isTrue();
        assertThat(limiter.tryAcquire("2.2.2.2")).isTrue();
        assertThat(limiter.tryAcquire("3.3.3.3")).isFalse();
    }

    @Test
    void resetsInNextMinute() {
        RateLimitInterceptor limiter = new RateLimitInterceptor(1, 1, clock);

        assertThat(limiter.tryAcquire("1.1.1.1")).isTrue();
        assertThat(limiter.tryAcquire("1.1.1.1")).isFalse();

        clock.advanceSeconds(60);

        assertThat(limiter.tryAcquire("1.1.1.1")).isTrue();
    }

    @Test
    void rejectsWithTooManyRequestsAndRetryAfter() {
        RateLimitInterceptor limiter = new RateLimitInterceptor(1, 100, clock);
        clock.advanceSeconds(45);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("1.1.1.1");

        assertThat(limiter.preHandle(request, new MockHttpServletResponse(), null)).isTrue();

        MockHttpServletResponse response = new MockHttpServletResponse();
        assertThat(limiter.preHandle(request, response, null)).isFalse();
        assertThat(response.getStatus()).isEqualTo(429);
        assertThat(response.getHeader("Retry-After")).isEqualTo("15");
    }

    private static class MutableClock extends Clock {
        private Instant now;

        MutableClock(Instant now) {
            this.now = now;
        }

        void advanceSeconds(long seconds) {
            now = now.plusSeconds(seconds);
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return now;
        }
    }
}
