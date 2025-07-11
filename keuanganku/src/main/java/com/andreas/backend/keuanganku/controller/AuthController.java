package com.andreas.backend.keuanganku.controller;

import com.andreas.backend.keuanganku.dto.LoginRequest;
import com.andreas.backend.keuanganku.dto.RegisterPenggunaRequest;
import com.andreas.backend.keuanganku.model.Pengguna;
import com.andreas.backend.keuanganku.service.PenggunaService;
import com.andreas.backend.keuanganku.util.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private PenggunaService penggunaService;

    @Autowired
    private JWTUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterPenggunaRequest pengguna) {
        if (penggunaService.isEmailDipakai(pengguna.getEmail())) {
            return ResponseEntity.badRequest().body("Email sudah digunakan");
        }
        String passwordHashed = penggunaService.hashPassword(pengguna.getPassword());
        Pengguna penggunaBaru = new Pengguna();
        penggunaBaru.setSandiHash(passwordHashed);
        penggunaBaru.setEmail(pengguna.getEmail());
        penggunaBaru.setNama(pengguna.getNama());
        penggunaBaru.setDibuatPada(LocalDateTime.now());
        return ResponseEntity.ok(penggunaService.simpan(penggunaBaru));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Buat cookie baru dengan nama sama (token), kosong, dan expired
        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/"); // pastikan path sama dengan saat login
        cookie.setMaxAge(0); // langsung kadaluarsa (hapus)
        response.addCookie(cookie);

        return ResponseEntity.ok("Logout berhasil");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        if (request.getEmail() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest().body("Email dan password wajib diisi");
        }

        Optional<Pengguna> penggunaOpt = penggunaService.temukanByEmail(request.getEmail());

        if (penggunaOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Email tidak ditemukan");
        }

        Pengguna pengguna = penggunaOpt.get();

        if (!penggunaService.passwordCocok(request.getPassword(), pengguna.getSandiHash())) {
            return ResponseEntity.badRequest().body("Password salah");
        }

        // Buat token JWT
        String token = jwtUtil.generateToken(pengguna.getId(), pengguna.getEmail());

        // Simpan token di cookie HTTP-only
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60); // 1 hari
        response.addCookie(cookie);

        return ResponseEntity.ok("Login berhasil");
    }
}