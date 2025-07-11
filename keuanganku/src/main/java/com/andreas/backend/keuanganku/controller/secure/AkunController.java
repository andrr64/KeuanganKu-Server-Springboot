package com.andreas.backend.keuanganku.controller.secure;

import com.andreas.backend.keuanganku.annotation.CurrentUserId;
import com.andreas.backend.keuanganku.dto.TambahAkunRequest;
import com.andreas.backend.keuanganku.model.Akun;
import com.andreas.backend.keuanganku.model.Pengguna;
import com.andreas.backend.keuanganku.service.AkunService;
import com.andreas.backend.keuanganku.service.PenggunaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/secure/akun")
public class AkunController {
    @Autowired
    private AkunService akunService;

    @Autowired
    private PenggunaService penggunaService;

    public ResponseEntity<?> tambahAkun(@CurrentUserId UUID currentUserId, @RequestBody TambahAkunRequest dto) {
        Akun akun = new Akun();
        akun.setNama(dto.getNama());
        akun.setSaldo(dto.getSaldo());
        akun.setDibuatPada(LocalDateTime.now());

        Pengguna pengguna = penggunaService.getById(currentUserId).orElse(null);
        if (pengguna == null) return ResponseEntity.badRequest().body("Pengguna tidak ditemukan");

        akun.setPengguna(pengguna);

        Akun baru = akunService.simpan(akun);
        return ResponseEntity.ok(baru);
    }
}
