package com.makotopay.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class IdempotencyService {

    private final RedisTemplate<String, String> redisTemplate;

    public IdempotencyService(
            RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Key already exists గా check చేయడం
    public boolean isDuplicate(String idempotencyKey) {
        return Boolean.TRUE.equals(
            redisTemplate.hasKey("idempotency:" + idempotencyKey));
    }

    // Key Redis లో save చేయడం — 24 hours expire అవుతుంది
    public void saveKey(String idempotencyKey) {
        redisTemplate.opsForValue().set(
            "idempotency:" + idempotencyKey,
            "processed",
            24,
            TimeUnit.HOURS
        );
    }
}