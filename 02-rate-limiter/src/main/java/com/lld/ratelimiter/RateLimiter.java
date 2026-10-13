package com.lld.ratelimiter;

public interface RateLimiter {
    boolean allowRequest(String clientId);
    boolean allowRequest(String clientId, int tokens);
}
