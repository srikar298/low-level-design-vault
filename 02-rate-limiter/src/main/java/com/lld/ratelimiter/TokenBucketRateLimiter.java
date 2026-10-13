package com.lld.ratelimiter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TokenBucketRateLimiter implements RateLimiter {

    private final long capacity;
    private final long refillRatePerSecond;
    private final ConcurrentMap<String, TokenBucket> clientBuckets;

    public TokenBucketRateLimiter(long capacity, long refillRatePerSecond) {
        if (capacity <= 0 || refillRatePerSecond <= 0) {
            throw new IllegalArgumentException("Capacity and refill rate must be strictly positive");
        }
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
        this.clientBuckets = new ConcurrentHashMap<>();
    }

    @Override
    public boolean allowRequest(String clientId) {
        return allowRequest(clientId, 1);
    }

    @Override
    public boolean allowRequest(String clientId, int tokens) {
        TokenBucket bucket = clientBuckets.computeIfAbsent(
            clientId,
            k -> new TokenBucket(capacity, refillRatePerSecond)
        );
        return bucket.tryConsume(tokens);
    }

    private static class TokenBucket {
        private final long capacity;
        private final long refillRatePerSecond;
        private double currentTokens;
        private long lastRefillTimestampNanos;

        TokenBucket(long capacity, long refillRatePerSecond) {
            this.capacity = capacity;
            this.refillRatePerSecond = refillRatePerSecond;
            this.currentTokens = capacity;
            this.lastRefillTimestampNanos = System.nanoTime();
        }

        synchronized boolean tryConsume(int tokens) {
            refill();
            if (currentTokens >= tokens) {
                currentTokens -= tokens;
                return true;
            }
            return false;
        }

        private void refill() {
            long now = System.nanoTime();
            double secondsElapsed = (now - lastRefillTimestampNanos) / 1_000_000_000.0;
            if (secondsElapsed > 0) {
                double tokensToAdd = secondsElapsed * refillRatePerSecond;
                this.currentTokens = Math.min(capacity, this.currentTokens + tokensToAdd);
                this.lastRefillTimestampNanos = now;
            }
        }
    }
}
