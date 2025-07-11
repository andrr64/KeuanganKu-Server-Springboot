package com.andreas.backend.keuanganku.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;
@Component
public class JWTUtil {

    @Value("${jwt.secret}")
    private String secret;

    private final long EXPIRATION = 1000 * 60 * 60 * 24; // 24 jam

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(UUID penggunaId, String email) {
        return Jwts.builder()
                .setSubject(penggunaId.toString())
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSigningKey())
                .compact();
    }

    public Jws<Claims> validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
    }

    public String ambilUserIdDariToken(String token) {
        return validateToken(token).getBody().getSubject();
    }
}
