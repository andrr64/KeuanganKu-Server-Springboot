package com.andreas.backend.keuanganku.controller.secure;

import java.util.HashMap;
import java.util.List;
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
import com.andreas.backend.keuanganku.dto.request.KategoriRequest;
import com.andreas.backend.keuanganku.dto.request.TambahKategoriRequest;
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

    /**
     * Endpoint untuk menambahkan kategori berdasarkan jenis.
     *
     * @param idPengguna ID pengguna saat ini (diambil dari token)
     * @param jenis Jenis kategori (1 = Pengeluaran, 2 = Pemasukan)
     * @param req Body permintaan berisi nama kategori
     * @return Respon sukses jika berhasil ditambahkan
     */
    @PostMapping("/{jenis}")
    public ResponseEntity<?> tambahKategori(
            @CurrentUserId UUID idPengguna,
            @PathVariable Integer jenis,
            @Valid @RequestBody KategoriRequest req
    ) {
        kategoriService.tambahKategori(idPengguna, jenis, req.getNama());
        return ResponseEntity.ok(new GeneralResponse<>("Kategori berhasil ditambahkan", null, true));
    }

    /**
     * Endpoint lama (deprecated) untuk mengambil semua kategori milik pengguna
     * dan sistem.
     *
     * @param idPengguna ID pengguna saat ini
     * @return Daftar kategori
     */
    @Deprecated
    @GetMapping()
    public ResponseEntity<?> getKategori(
            @CurrentUserId UUID idPengguna
    ) {
        List<Kategori> list = kategoriService.getAllKategori(idPengguna);
        List<KategoriResponse> response = list.stream()
                .map(k -> new KategoriResponse(k.getId(), k.getNama(), k.getJenis()))
                .toList();
        return ResponseEntity.ok(new GeneralResponse<>("Ok", response, true));
    }

    /**
     * Endpoint lama (deprecated) untuk mengambil kategori berdasarkan jenis.
     *
     * @param idPengguna ID pengguna saat ini
     * @param jenis Jenis kategori (1 = Pengeluaran, 2 = Pemasukan)
     * @return Daftar kategori sesuai jenis
     */
    @Deprecated
    @GetMapping("/{jenis}")
    public ResponseEntity<?> getKategoriByJenis(
            @CurrentUserId UUID idPengguna,
            @PathVariable Integer jenis
    ) {
        List<Kategori> list = kategoriService.getKategoriByJenis(idPengguna, jenis);
        List<KategoriResponse> response = list.stream()
                .map(k -> new KategoriResponse(k.getId(), k.getNama(), k.getJenis()))
                .toList();
        return ResponseEntity.ok(new GeneralResponse<>("Ok", response, true));
    }

    /**
     * Endpoint baru untuk mengambil kategori dengan filter pencarian, jenis,
     * dan pagination.
     *
     * @param idPengguna ID pengguna saat ini
     * @param page Nomor halaman (dimulai dari 0)
     * @param size Jumlah data per halaman
     * @param keyword Kata kunci pencarian berdasarkan nama kategori (opsional)
     * @param jenis Jenis kategori (0 = semua, 1 = Pengeluaran, 2 = Pemasukan)
     * @return Hasil pencarian kategori dalam bentuk paginasi
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
     * Memperbarui nama kategori.
     *
     * @param idPengguna ID pengguna saat ini
     * @param idKategori ID kategori yang ingin diperbarui
     * @param request Permintaan berisi nama baru kategori
     * @return Respon sukses jika berhasil diperbarui
     */
    @PutMapping("/{id_kategori}")
    public ResponseEntity<?> updateKategori(
            @CurrentUserId UUID idPengguna,
            @PathVariable("id_kategori") UUID idKategori,
            @Valid @RequestBody KategoriRequest request
    ) {
        kategoriService.updateKategori(idPengguna, idKategori, request.getNama());
        return ResponseEntity.ok(new GeneralResponse<>("Kategori berhasil diperbarui", null, true));
    }

    /**
     * Menghapus kategori.
     *
     * @param idPengguna ID pengguna saat ini
     * @param idKategori ID kategori yang akan dihapus
     * @param ubahTransaksiKategori Jika true, pindahkan transaksi ke kategori
     * lain
     * @param targetKategori ID kategori tujuan jika transaksi ingin dipindahkan
     * (opsional)
     * @return Respon sukses jika berhasil dihapus
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

    @PostMapping()
    public ResponseEntity<?> tambahTransaksi(
            @CurrentUserId UUID idPengguna,
            @Valid @RequestBody TambahKategoriRequest request
    ) {
        kategoriService.tambahKategori(idPengguna, request.getJenis(), request.getNama());
        return ResponseEntity.ok(new GeneralResponse<>("Kategori berhasil disimpan", null, true));
    }
}
