package com.andreas.backend.keuanganku.controller.secure;

import java.util.HashMap;
import java.util.Map;
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
 * Controller untuk menangani operasi terkait kategori keuangan (pemasukan/pengeluaran)
 * yang aman (menggunakan otentikasi).
 */
@RestController
@RequestMapping("/api/secure/kategori")
@RequiredArgsConstructor
public class KategoriController {

    private final KategoriService kategoriService;

    /**
     * Mengambil daftar kategori milik pengguna dengan filter, pencarian, dan pagination.
     *
     * @param idPengguna ID pengguna dari token JWT (di-inject otomatis)
     * @param page Halaman data yang diminta (dimulai dari 0)
     * @param size Jumlah data per halaman
     * @param keyword Kata kunci pencarian nama kategori (opsional)
     * @param jenis Jenis kategori: 0 = semua, 1 = Pengeluaran, 2 = Pemasukan
     * @return ResponseEntity berisi data kategori dalam format paginasi
     */
    @GetMapping("/filter")
    public ResponseEntity<?> getKategoriFiltered(
            @CurrentUserId UUID idPengguna,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "jenis", defaultValue = "0") int jenis
    ) {
        Page<Kategori> result = kategoriService.getFilteredKategori(idPengguna, jenis, keyword, page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("content", result.getContent());
        response.put("currentPage", result.getNumber());
        response.put("totalItems", result.getTotalElements());
        response.put("totalPages", result.getTotalPages());

        return ResponseEntity.ok(new GeneralResponse<>("Ok", response, true));
    }

    /**
     * Memperbarui nama kategori berdasarkan ID kategori.
     *
     * @param idPengguna ID pengguna dari token JWT (di-inject otomatis)
     * @param idKategori ID kategori yang ingin diperbarui
     * @param request Payload berisi nama baru kategori
     * @return ResponseEntity sukses jika pembaruan berhasil
     */
    @PutMapping("/{id_kategori}")
    public ResponseEntity<?> updateKategori(
            @CurrentUserId UUID idPengguna,
            @PathVariable("id_kategori") UUID idKategori,
            @Valid @RequestBody UpdateKategoriRequest request
    ) {
        kategoriService.updateKategori(idPengguna, idKategori, request.getNama());
        return ResponseEntity.ok(new GeneralResponse<>("Kategori berhasil diperbarui", null, true));
    }

    /**
     * Menghapus kategori berdasarkan ID kategori.
     *
     * @param idPengguna ID pengguna dari token JWT (di-inject otomatis)
     * @param idKategori ID kategori yang akan dihapus
     * @param ubahTransaksiKategori Jika true, pindahkan transaksi ke kategori lain
     * @param targetKategori (Opsional) ID kategori tujuan jika transaksi ingin dipindahkan
     * @return ResponseEntity sukses jika penghapusan berhasil
     */
    @DeleteMapping("/{id_kategori}")
    public ResponseEntity<?> hapusKategori(
            @CurrentUserId UUID idPengguna,
            @PathVariable("id_kategori") UUID idKategori,
            @RequestParam("ubahTransaksiKategori") boolean ubahTransaksiKategori,
            @RequestParam(value = "targetKategori", required = false) UUID targetKategori
    ) {
        kategoriService.hapusKategori(idPengguna, idKategori, ubahTransaksiKategori, targetKategori);
        return ResponseEntity.ok(new GeneralResponse<>("Kategori berhasil dihapus", null, true));
    }

    /**
     * Menambahkan kategori baru untuk pengguna.
     *
     * @param idPengguna ID pengguna dari token JWT (di-inject otomatis)
     * @param request Payload berisi nama dan jenis kategori
     * @return ResponseEntity sukses jika penambahan berhasil
     */
    @PostMapping()
    public ResponseEntity<?> tambahKategori(
            @CurrentUserId UUID idPengguna,
            @Valid @RequestBody TambahKategoriRequest request
    ) {
        kategoriService.tambahKategori(idPengguna, request.getJenis(), request.getNama());
        return ResponseEntity.ok(new GeneralResponse<>("Kategori berhasil disimpan", null, true));
    }
}
