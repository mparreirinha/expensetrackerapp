package com.parreirinha.expensetrackerapp.auth.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RevokeTokenService {

    private final StringRedisTemplate redisTemplate;

    public RevokeTokenService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void revokeToken(String token, long expirationTime) {
        redisTemplate.opsForValue().set(token, "revoked", expirationTime);
    }

    public boolean isTokenRevoked(String token) {
        return redisTemplate.hasKey(token);
    }
    
}
