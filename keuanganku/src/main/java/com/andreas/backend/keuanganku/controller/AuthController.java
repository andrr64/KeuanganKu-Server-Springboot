package com.andreas.backend.keuanganku.controller;

import com.andreas.backend.keuanganku.dto.request.pengguna.LoginRequest;
import com.andreas.backend.keuanganku.dto.request.pengguna.RegisterRequest;
import com.andreas.backend.keuanganku.dto.response.GeneralResponse;
import com.andreas.backend.keuanganku.model.Pengguna;
import com.andreas.backend.keuanganku.service.JwtService;
import com.andreas.backend.keuanganku.service.PenggunaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final PenggunaService penggunaService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        penggunaService.register(req.getNama(), req.getEmail(), req.getPassword());
        return ResponseEntity.ok(
                new GeneralResponse<>("Registrasi berhasil", null, true)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        Pengguna pengguna = penggunaService.login(req.getEmail(), req.getPassword());
        String accessToken = jwtService.generateToken(pengguna.getId());

        ResponseCookie cookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(false) // karena http
                .sameSite("Lax") // default behavior, cookie tetap dikirim di navigasi biasa
                .path("/")
                .maxAge(Duration.ofDays(3))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new GeneralResponse<>("Login berhasil", null, true)); // <- ini body() letakkan di sini
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
                .body(new GeneralResponse<>("Logout berhasil", null, true));
    }
}
