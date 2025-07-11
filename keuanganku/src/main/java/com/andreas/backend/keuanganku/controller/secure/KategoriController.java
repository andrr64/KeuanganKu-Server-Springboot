package com.andreas.backend.keuanganku.controller.secure;

import com.andreas.backend.keuanganku.annotation.CurrentUserId;
import com.andreas.backend.keuanganku.dto.KategoriRequest;
import com.andreas.backend.keuanganku.model.Kategori;
import com.andreas.backend.keuanganku.service.KategoriService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/secure/kategori")
public class KategoriController {

    @Autowired
    private final KategoriService kategoriService;

    public KategoriController(KategoriService kategoriService) {
        this.kategoriService = kategoriService;
    }

    @PostMapping
    public ResponseEntity<?> buatKategori(@CurrentUserId UUID userId, @RequestBody KategoriRequest request) {
        Kategori baru = kategoriService.simpanKategori(userId, request.getNama(), request.getJenis());
        return ResponseEntity.ok("Kategori berhasil dibuat");
    }

    @GetMapping
    public ResponseEntity<?> getSemuaKategori(@CurrentUserId UUID userId) {
        List<Kategori> kategoriList = kategoriService.getAllByPenggunaId(userId);
        return ResponseEntity.ok(kategoriList);
    }
}
