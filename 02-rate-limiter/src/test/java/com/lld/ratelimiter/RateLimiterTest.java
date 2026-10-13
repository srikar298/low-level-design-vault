package com.lld.ratelimiter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RateLimiterTest {

    @Test
    @DisplayName("Should allow requests up to bucket capacity")
    void testAllowUpToCapacity() {
        RateLimiter limiter = new TokenBucketRateLimiter(3, 1);
        String client = "client_alpha";

        assertTrue(limiter.allowRequest(client));
        assertTrue(limiter.allowRequest(client));
        assertTrue(limiter.allowRequest(client));
        assertFalse(limiter.allowRequest(client)); // 4th request must be rejected!
    }

    @Test
    @DisplayName("Should track multiple clients independently")
    void testMultipleClients() {
        RateLimiter limiter = new TokenBucketRateLimiter(2, 1);

        assertTrue(limiter.allowRequest("user_1"));
        assertTrue(limiter.allowRequest("user_1"));
        assertFalse(limiter.allowRequest("user_1")); // user_1 exhausted

        // user_2 should still have full quota
        assertTrue(limiter.allowRequest("user_2"));
        assertTrue(limiter.allowRequest("user_2"));
        assertFalse(limiter.allowRequest("user_2"));
    }

    @Test
    @DisplayName("Should refill tokens over time")
    void testTokenRefill() throws InterruptedException {
        // 2 capacity, refills 10 tokens per second (1 token every 100ms)
        RateLimiter limiter = new TokenBucketRateLimiter(2, 10);
        String client = "bursty_client";

        assertTrue(limiter.allowRequest(client));
        assertTrue(limiter.allowRequest(client));
        assertFalse(limiter.allowRequest(client)); // Empty

        // Sleep 150ms to allow at least 1 token to refill
        Thread.sleep(150);

        assertTrue(limiter.allowRequest(client));
    }
}
