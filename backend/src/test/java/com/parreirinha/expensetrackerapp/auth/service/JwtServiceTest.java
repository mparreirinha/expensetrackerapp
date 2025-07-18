package com.parreirinha.expensetrackerapp.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

public class JwtServiceTest {

    private JwtService jwtService;
    private TokenService tokenService;
    private UserDetails userDetails;
    private final String secretKey = "dGVzdGVzZWNyZXRrZXl0ZXN0ZXNlY3JldGtleTEyMzQ1Ng=="; // base64 for 'testsecretkeytestsecretkey123456'
    private final long expirationTime = 3600000L;

    @BeforeEach
    void setup() throws Exception {
        tokenService = mock(TokenService.class);
        jwtService = new JwtService(tokenService);
        userDetails = mock(UserDetails.class);
        Field secretKeyField = JwtService.class.getDeclaredField("secretKey");
        secretKeyField.setAccessible(true);
        secretKeyField.set(jwtService, secretKey);
        Field expirationField = JwtService.class.getDeclaredField("expirationTime");
        expirationField.setAccessible(true);
        expirationField.set(jwtService, expirationTime);
    }

    @Test
    void generateToken_ShouldReturnValidJwt() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("user1");
        // Act
        String token = jwtService.generateToken(userDetails);
        // Assert
        assertThat(token).isNotNull();
        String username = jwtService.extractUsername(token);
        assertThat(username).isEqualTo("user1");
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("user2");
        // Act
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);
        // Assert
        assertThat(username).isEqualTo("user2");
    }

    @Test
    void extractTokenId_ShouldReturnCorrectTokenId() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("user3");
        // Act
        String token = jwtService.generateToken(userDetails);
        String tokenId = jwtService.extractTokenId(token);
        // Assert
        assertThat(tokenId).isNotNull();
    }

    @Test
    void isTokenValid_ShouldReturnTrueForValidToken() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("user4");
        // Act
        String token = jwtService.generateToken(userDetails);
        // Arrange
        when(userDetails.getUsername()).thenReturn("user4");
        // Act
        boolean valid = jwtService.isTokenValid(token, userDetails);
        // Assert
        assertThat(valid).isTrue();
    }

    @Test
    void isTokenExpired_ShouldReturnFalseForFreshToken() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("user5");
        // Act
        String token = jwtService.generateToken(userDetails);
        boolean expired = jwtService.isTokenExpired(token);
        // Assert
        assertThat(expired).isFalse();
    }

    @Test
    void revokeToken_ShouldCallTokenService() {
        // Arrange
        String fakeToken = "Bearer sometoken";
        // Act
        JwtService spyJwtService = spy(jwtService);
        doReturn("user6").when(spyJwtService).extractUsername(anyString());
        spyJwtService.revokeToken(fakeToken);
        // Assert
        verify(tokenService).revokeToken("user6");
    }

    @Test
    void isTokenRevoked_ShouldCallTokenService() {
        // Arrange
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);
        // Act
        String tokenId = jwtService.extractTokenId(token);
        when(tokenService.isTokenRevoked(username, tokenId)).thenReturn(true);
        boolean revoked = jwtService.isTokenRevoked(token);
        // Assert
        assertThat(revoked).isTrue();
        verify(tokenService).isTokenRevoked(username, tokenId);
    }
} 