package com.andreas.backend.keuanganku.controller.secure;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.andreas.backend.keuanganku.annotation.CurrentUserId;
import com.andreas.backend.keuanganku.dto.request.kategori.TambahKategoriRequest;
import com.andreas.backend.keuanganku.dto.request.kategori.UpdateKategoriRequest;
import com.andreas.backend.keuanganku.dto.response.GeneralResponse;
import com.andreas.backend.keuanganku.model.Kategori;
import com.andreas.backend.keuanganku.service.KategoriService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller untuk menangani operasi terkait kategori keuangan
 * (pemasukan/pengeluaran) yang aman (menggunakan otentikasi).
 */
@RestController
@RequestMapping("/api/secure/kategori")
@RequiredArgsConstructor
public class KategoriController {

    private final KategoriService kategoriService;

    @GetMapping()
    public ResponseEntity<?> getKategoriFiltered(
            @CurrentUserId UUID idPengguna,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "jenis", defaultValue = "0") int jenis
    ) {
        Page<Kategori> result = kategoriService.getFilteredKategori(idPengguna, jenis, keyword, page, size);
        return ResponseEntity.ok(GeneralResponse.fromPage(result));
    }

    @PutMapping("/{id_kategori}")
    public ResponseEntity<?> updateKategori(
            @CurrentUserId UUID idPengguna,
            @PathVariable("id_kategori") UUID idKategori,
            @Valid @RequestBody UpdateKategoriRequest request
    ) {
        kategoriService.updateKategori(idPengguna, idKategori, request.getNama());
        return ResponseEntity.ok(new GeneralResponse<>("Kategori berhasil diperbarui", null, true));
    }

    @DeleteMapping("/{id_kategori}")
    public ResponseEntity<?> hapusKategori(
            @CurrentUserId UUID idPengguna,
            @PathVariable("id_kategori") UUID idKategori
    ) {
        kategoriService.hapusKategori(idPengguna, idKategori);
        return ResponseEntity.ok(new GeneralResponse<>("Kategori berhasil dihapus", null, true));
    }

    @PostMapping()
    public ResponseEntity<?> tambahKategori(
            @CurrentUserId UUID idPengguna,
            @Valid @RequestBody TambahKategoriRequest request
    ) {
        kategoriService.tambahKategori(idPengguna, request.getJenis(), request.getNama());
        return ResponseEntity.ok(new GeneralResponse<>("Kategori berhasil disimpan", null, true));
    }
}
