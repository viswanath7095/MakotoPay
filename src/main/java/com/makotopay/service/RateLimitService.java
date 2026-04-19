package com.makotopay.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RateLimitService {

    private final RedisTemplate<String, String> redisTemplate;

    // 2 నిమిషాల్లో max 5 transfers
    private static final int MAX_TRANSFERS = 5;
    private static final int TIME_WINDOW_MINUTES = 2;

    public RateLimitService(
            RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Transfer count check చేయడం
    public boolean isRateLimitExceeded(String email) {
        String key = "rate_limit:" + email;
        String count = redisTemplate.opsForValue().get(key);

        if (count == null) {
            return false;
        }

        return Integer.parseInt(count) >= MAX_TRANSFERS;
    }

    // Transfer count increment చేయడం
    public void incrementTransferCount(String email) {
        String key = "rate_limit:" + email;
        String count = redisTemplate.opsForValue().get(key);

        if (count == null) {
            // First transfer — 2 నిమిషాల timer start
            redisTemplate.opsForValue().set(
                key, "1",
                TIME_WINDOW_MINUTES,
                TimeUnit.MINUTES
            );
        } else {
            // Increment చేయడం
            redisTemplate.opsForValue().increment(key);
        }
    }

    // Current count చూడడం
    public int getCurrentCount(String email) {
        String key = "rate_limit:" + email;
        String count = redisTemplate.opsForValue().get(key);
        return count == null ? 0 : Integer.parseInt(count);
    }
}