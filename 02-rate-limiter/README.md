# 02 - Distributed Rate Limiter

## 🎯 Problem Statement
Design a thread-safe Rate Limiter to protect APIs against traffic bursts and abuse:
* `allowRequest(String clientId)`: Returns `true` if the request is permitted within the configured rate window, `false` otherwise.
* Implement the **Token Bucket Algorithm**:
  - A bucket with a fixed capacity $C$ fills at a constant rate of $R$ tokens per second.
  - Each request consumes 1 token.
  - If no tokens remain, the request is rejected ($429$ Too Many Requests).
* Must support multiple distinct clients (`clientId`) concurrently with thread-safe atomicity.

---

## 🏗 Class Diagram

```mermaid
classDiagram
    class RateLimiter {
        <<interface>>
        +allowRequest(String clientId) boolean
        +allowRequest(String clientId, int tokens) boolean
    }

    class TokenBucketRateLimiter {
        -long capacity
        -long refillRatePerSecond
        -ConcurrentMap~String, Bucket~ clientBuckets
        +allowRequest(String clientId) boolean
    }

    class Bucket {
        -long capacity
        -long refillRate
        -double currentTokens
        -long lastRefillTimestamp
        +synchronized tryConsume(int tokens) boolean
        -refill()
    }

    RateLimiter <|.. TokenBucketRateLimiter
    TokenBucketRateLimiter *-- Bucket
```

---

## 🚀 Key Implementation Highlights
1. **Strategy Pattern**: Extensible interface allowing drop-in implementations for Leaky Bucket, Sliding Window Log, or Token Bucket.
2. **Lazy Token Refill**: Tokens are calculated lazily based on elapsed time $(\Delta t \times R)$ upon each request, avoiding wasteful background timer threads.
3. **Thread Safety**: Synchronized bucket state transitions per client using `ConcurrentHashMap`.
