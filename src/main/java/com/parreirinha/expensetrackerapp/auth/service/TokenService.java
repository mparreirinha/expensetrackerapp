package com.parreirinha.expensetrackerapp.auth.service;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private final StringRedisTemplate redisTemplate;

    public TokenService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void storeToken(String userId, String tokenId, long expirationTime) {
        String key = "user:" + userId + ":token";
        redisTemplate.opsForValue().set(key, tokenId, Duration.ofMillis(expirationTime));
    }

    public void revokeToken(String userId, String tokenId) {
        String key = "user:" + userId + ":token";
        redisTemplate.delete(key);
    }

    public boolean isTokenRevoked(String userId, String tokenId) {
        String key = "user:" + userId + ":token";
        return !tokenId.equals(redisTemplate.opsForValue().get(key));
    }
    
}
