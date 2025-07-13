package com.andreas.backend.keuanganku.controller.secure;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.andreas.backend.keuanganku.annotation.CurrentUserId;
import com.andreas.backend.keuanganku.model.Pengguna;
import com.andreas.backend.keuanganku.service.PenggunaService;

@RestController
@RequestMapping("/api/secure")
public class PenggunaController {

    private final PenggunaService penggunaService;

    public PenggunaController(PenggunaService penggunaService) {
        this.penggunaService = penggunaService;
    }

    @GetMapping("/detail-pengguna")
    public ResponseEntity<?> getDetailPengguna(@CurrentUserId UUID userId) {
        Pengguna pengguna = penggunaService.getById(userId)
                .orElseThrow(() -> new RuntimeException("Pengguna tidak ditemukan"));

        return ResponseEntity.ok(Map.of(
                "id", pengguna.getId(),
                "nama", pengguna.getNama(),
                "email", pengguna.getEmail()
        ));
    }
}
