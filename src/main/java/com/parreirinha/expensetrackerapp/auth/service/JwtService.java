package com.parreirinha.expensetrackerapp.auth.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final TokenService tokenService;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    public JwtService(TokenService revokeTokenService) {
        this.tokenService = revokeTokenService;
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> claims, UserDetails userDetails) {
        return buildToken(claims, userDetails, expirationTime);
    }

    public void revokeToken(String token) {
        String jwt = token.substring(7);
        String userId = extractUsername(jwt);
        String tokenId = extractTokenId(jwt);
       tokenService.revokeToken(userId, tokenId);
    }

    public boolean isTokenRevoked(String token) {
        String userId = extractUsername(token);
        String tokenId = extractTokenId(token);
        return tokenService.isTokenRevoked(userId, tokenId);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractTokenId(String token) {
        return extractClaim(token, Claims::getId);
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public<T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private String buildToken(Map<String, Object> claims, UserDetails userDetails, long expirationTime) {
        String tokenId = UUID.randomUUID().toString();
        tokenService.storeToken(userDetails.getUsername(), tokenId, expirationTime);
        return Jwts
                .builder()
                .setClaims(claims)
                .setId(tokenId)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignKeys(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKeys())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignKeys() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}