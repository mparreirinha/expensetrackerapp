package com.parreirinha.expensetrackerapp.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

public class TokenServiceTest {

    private StringRedisTemplate redisTemplate;
    private ValueOperations<String, String> valueOperations;
    private TokenService tokenService;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setup() {
        redisTemplate = mock(StringRedisTemplate.class);
        valueOperations = (ValueOperations<String, String>) mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        tokenService = new TokenService(redisTemplate);
    }

    @Test
    void storeToken_ShouldStoreTokenWithExpiration() {
        // Arrange
        String userId = "user1";
        String tokenId = "token123";
        long expiration = 1000L;
        // Act
        tokenService.storeToken(userId, tokenId, expiration);
        // Assert
        verify(valueOperations).set(eq("user:" + userId + ":token"), eq(tokenId), eq(Duration.ofMillis(expiration)));
    }

    @Test
    void revokeToken_ShouldDeleteTokenKey() {
        // Arrange
        String userId = "user1";
        // Act
        tokenService.revokeToken(userId);
        // Assert
        verify(redisTemplate).delete("user:" + userId + ":token");
    }

    @Test
    void isTokenRevoked_ShouldReturnFalse_WhenTokenMatches() {
        // Arrange
        String userId = "user1";
        String tokenId = "token123";
        when(valueOperations.get("user:" + userId + ":token")).thenReturn(tokenId);
        // Act
        boolean revoked = tokenService.isTokenRevoked(userId, tokenId);
        // Assert
        assertThat(revoked).isFalse();
    }

    @Test
    void isTokenRevoked_ShouldReturnTrue_WhenTokenDoesNotMatch() {
        // Arrange
        String userId = "user1";
        String tokenId = "token123";
        when(valueOperations.get("user:" + userId + ":token")).thenReturn("otherToken");
        // Act
        boolean revoked = tokenService.isTokenRevoked(userId, tokenId);
        // Assert
        assertThat(revoked).isTrue();
    }

    @Test
    void isTokenRevoked_ShouldReturnTrue_WhenNoTokenStored() {
        // Arrange
        String userId = "user1";
        String tokenId = "token123";
        when(valueOperations.get("user:" + userId + ":token")).thenReturn(null);
        // Act
        boolean revoked = tokenService.isTokenRevoked(userId, tokenId);
        // Assert
        assertThat(revoked).isTrue();
    }

} 