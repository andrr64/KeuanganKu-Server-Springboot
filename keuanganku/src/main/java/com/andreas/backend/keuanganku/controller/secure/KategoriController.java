package com.andreas.backend.keuanganku.controller.secure;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.andreas.backend.keuanganku.annotation.CurrentUserId;
import com.andreas.backend.keuanganku.dto.request.KategoriRequest;
import com.andreas.backend.keuanganku.dto.response.GeneralResponse;
import com.andreas.backend.keuanganku.dto.response.KategoriResponse;
import com.andreas.backend.keuanganku.model.Kategori;
import com.andreas.backend.keuanganku.service.KategoriService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/secure/kategori")
@RequiredArgsConstructor
public class KategoriController {

    private final KategoriService kategoriService;

    // POST /kategori/{jenis}
    @PostMapping("/{jenis}")
    public ResponseEntity<?> tambahKategori(
            @CurrentUserId UUID idPengguna,
            @PathVariable Integer jenis,
            @Valid @RequestBody KategoriRequest req
    ) {
        kategoriService.tambahKategori(idPengguna, jenis, req.getNama());
        return ResponseEntity.ok(new GeneralResponse<>("Kategori berhasil ditambahkan"));
    }

    // GET /kategori/{jenis}
    @GetMapping("/{jenis}")
    public ResponseEntity<?> getKategoriByJenis(
            @CurrentUserId UUID idPengguna,
            @PathVariable Integer jenis
    ) {
        List<Kategori> list = kategoriService.getKategoriByJenis(idPengguna, jenis);

        List<KategoriResponse> response = list.stream()
                .map(k -> new KategoriResponse(k.getId(), k.getNama(), k.getJenis()))
                .toList();

        return ResponseEntity.ok(new GeneralResponse<>("Ok", response));
    }
}
