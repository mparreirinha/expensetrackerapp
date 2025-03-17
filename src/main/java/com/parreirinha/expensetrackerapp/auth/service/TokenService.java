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
        String key = "user:" + userId + ":tokens";
        redisTemplate.opsForSet().add(key, tokenId);
        redisTemplate.expire(tokenId, Duration.ofMillis(expirationTime));
    }

    public void revokeToken(String userId, String tokenId) {
        String key = "user:" + userId + ":tokens";
        redisTemplate.opsForSet().remove(key, tokenId);
    }

    public boolean isTokenRevoked(String userId, String tokenId) {
        String key = "user:" + userId + ":tokens";
        return !Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, tokenId));
    }
    
}
