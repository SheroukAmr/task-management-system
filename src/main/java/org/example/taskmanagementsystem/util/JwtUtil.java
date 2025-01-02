package org.example.taskmanagementsystem.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    // Generate a JWT token
    public String generateToken(String userName) {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8); // Ensure UTF-8 encoding is used for consistency
        return Jwts.builder()
                .setSubject(userName)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))  // 1 hour expiration
                .signWith(Keys.hmacShaKeyFor(keyBytes), SignatureAlgorithm.HS256) // Ensure we're using the same key
                .compact();
    }

    // Extract claims from the token
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8))) // Use the same key for validation
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Extract username from the token
    public String getUsernameFromToken(String token) {
        return extractClaims(token).getSubject(); // Get the username from the "sub" claim
    }

    // Validate the token
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Extract expiration date from token
    public Date extractExpiration(String token) {
        return extractClaims(token).getExpiration();
    }

    // Check if the token has expired
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject(); // The 'sub' claim contains the username
    }
}
