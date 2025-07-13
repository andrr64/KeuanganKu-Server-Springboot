package com.andreas.backend.keuanganku.service;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;

@Service
public class JwtService {

    // Kunci rahasia, sebaiknya disimpan di environment variable dan di-encode Base64.
    // Gunakan kunci yang lebih kuat, minimal 256 bit (32 karakter sebelum Base64 encoding).
    // Contoh kunci yang dihasilkan dengan Keys.hmacShaKeyFor(SignatureAlgorithm.HS256)
    // dan kemudian di-encode Base64. Jangan gunakan kunci ini di produksi!
    private static final String SECRET_KEY = "iniAdalahKunciRahasiaYangSangatAmanDanPanjangUntukJWTDanHarusDiGantiDenganKeyYangBenar"; // Ganti dengan kunci yang lebih kuat!

    // Token berlaku 7 hari
    private static final long EXPIRATION_MS = 1000 * 60 * 60 * 24 * 7;

    /**
     * Mengambil signing key dari secret key yang di-encode Base64.
     * @return Objek Key yang digunakan untuk sign dan verify JWT.
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UUID idPengguna) {
        return Jwts.builder()
                .setSubject(idPengguna.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                // Menggunakan signWith(Key, SignatureAlgorithm) yang tidak deprecated
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public UUID extractUserId(String token) {
        try {
            // Menggunakan Jwts.parserBuilder() yang baru
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSignInKey()) // Menggunakan setSigningKey(Key) yang tidak deprecated
                    .build() // Membangun parser
                    .parseClaimsJws(token) // parseClaimsJws masih valid, tetapi parseSignedClaims() adalah alternatif yang lebih baru
                    .getBody(); // getBody() masih valid, tetapi getPayload() adalah alternatif yang lebih baru
            return UUID.fromString(claims.getSubject());
        } catch (SecurityException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            // Handle specific JWT exceptions more gracefully if needed
            // For now, re-throwing or logging the exception is common
            throw new RuntimeException("Error extracting user ID from token: " + e.getMessage(), e);
        }
    }

    public boolean isTokenValid(String token, UUID expectedId) {
        try {
            UUID actualId = extractUserId(token);
            // Additionally check if the token is expired here, as extractUserId might throw ExpiredJwtException
            Date expiration = Jwts.parserBuilder()
                                  .setSigningKey(getSignInKey())
                                  .build()
                                  .parseClaimsJws(token)
                                  .getBody()
                                  .getExpiration();
            boolean isExpired = expiration.before(new Date());

            return actualId.equals(expectedId) && !isExpired;
        } catch (ExpiredJwtException e) {
            // Token is expired
            return false;
        } catch (Exception e) {
            // Other validation errors (e.g., malformed, unsupported, bad signature)
            return false;
        }
    }
}