package com.andreas.backend.keuanganku.controller;

import java.time.Duration;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.andreas.backend.keuanganku.dto.request.LoginRequest;
import com.andreas.backend.keuanganku.dto.request.RegisterRequest;
import com.andreas.backend.keuanganku.model.Pengguna;
import com.andreas.backend.keuanganku.service.AuthService;
import com.andreas.backend.keuanganku.service.JwtService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        Pengguna pengguna = authService.register(req.getNama(), req.getEmail(), req.getPassword());

        return ResponseEntity.ok(Map.of(
                "message", "Registrasi berhasil!",
                "id", pengguna.getId(),
                "email", pengguna.getEmail(),
                "nama", pengguna.getNama()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        Pengguna pengguna = authService.login(req.getEmail(), req.getPassword());
        String accessToken = jwtService.generateToken(pengguna.getId());

        ResponseCookie cookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(true) // pastikan HTTPS di production
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofHours(1))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("message", "Login berhasil!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie expiredCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, expiredCookie.toString())
                .body(Map.of("message", "Logout berhasil!"));
    }
}
