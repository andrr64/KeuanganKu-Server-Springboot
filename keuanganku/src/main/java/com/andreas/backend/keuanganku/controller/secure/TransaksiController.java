package com.andreas.backend.keuanganku.controller.secure;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
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
import com.andreas.backend.keuanganku.dto.request.TransaksiRequest;
import com.andreas.backend.keuanganku.dto.response.GeneralResponse;
import com.andreas.backend.keuanganku.dto.response.TransaksiResponse;
import com.andreas.backend.keuanganku.service.TransaksiService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller untuk mengelola transaksi pengguna secara aman (butuh autentikasi).
 */
@RestController
@RequestMapping("/api/secure/transaksi")
@RequiredArgsConstructor
public class TransaksiController {

    private final TransaksiService transaksiService;

    /**
     * Menambahkan transaksi baru ke akun pengguna.
     *
     * @param idPengguna ID pengguna yang sedang login
     * @param request    Data transaksi yang akan ditambahkan
     * @return Respon sukses
     */
    @PostMapping
    public ResponseEntity<?> tambahTransaksi(
            @CurrentUserId UUID idPengguna,
            @Valid @RequestBody TransaksiRequest request
    ) {
        transaksiService.tambahTransaksi(idPengguna, request);
        return ResponseEntity.ok(new GeneralResponse<>("Transaksi berhasil ditambahkan", null, true));
    }

    /**
     * Mengambil daftar transaksi berdasarkan filter dan pagination.
     *
     * @param idPengguna ID pengguna
     * @param startDate  Tanggal awal (opsional)
     * @param endDate    Tanggal akhir (opsional)
     * @param jenis      Jenis transaksi (1 = pengeluaran, 2 = pemasukan)
     * @param idAkun     ID akun (opsional)
     * @param page       Nomor halaman
     * @param keyword    Kata kunci pencarian
     * @param size       Jumlah data per halaman
     * @return Daftar transaksi yang difilter
     */
    @GetMapping
    public ResponseEntity<?> getTransaksi(
            @CurrentUserId UUID idPengguna,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate endDate,
            @RequestParam(name = "jenis", required = false) Integer jenis,
            @RequestParam(name = "idAkun", required = false) UUID idAkun,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        Page<TransaksiResponse> daftar = transaksiService.getFilteredTransaksi(
                idPengguna, keyword, startDate, endDate, jenis, idAkun, page, size
        );
        return ResponseEntity.ok(GeneralResponse.fromPage(daftar));
    }

    /**
     * Mengambil daftar transaksi terbaru dengan jumlah maksimal tertentu.
     *
     * @param idPengguna ID pengguna
     * @param limit      Jumlah maksimum transaksi
     * @return Daftar transaksi terbaru
     */
    @GetMapping("/data-terbaru")
    public ResponseEntity<?> getRecentTransaksi(
            @CurrentUserId UUID idPengguna,
            @RequestParam(name = "limit", defaultValue = "5") int limit
    ) {
        List<TransaksiResponse> recentTransaksi = transaksiService.getRecentTransaksi(idPengguna, limit);
        return ResponseEntity.ok(new GeneralResponse<>("Ok", recentTransaksi, true));
    }

    /**
     * Memperbarui transaksi berdasarkan ID.
     *
     * @param idPengguna  ID pengguna
     * @param idTransaksi ID transaksi yang akan diupdate
     * @param request     Data transaksi baru
     * @return Respon sukses
     */
    @PutMapping("/{id_transaksi}")
    public ResponseEntity<?> updateTransaksi(
            @CurrentUserId UUID idPengguna,
            @PathVariable("id_transaksi") UUID idTransaksi,
            @Valid @RequestBody TransaksiRequest request
    ) {
        transaksiService.updateTransaksi(idPengguna, idTransaksi, request);
        return ResponseEntity.ok(new GeneralResponse<>("Transaksi berhasil diperbarui", null, true));
    }

    /**
     * Menghapus transaksi berdasarkan ID.
     *
     * @param idPengguna  ID pengguna
     * @param idTransaksi ID transaksi yang akan dihapus
     * @return Respon sukses
     */
    @DeleteMapping("/{id_transaksi}")
    public ResponseEntity<?> hapusTransaksi(
            @CurrentUserId UUID idPengguna,
            @PathVariable("id_transaksi") UUID idTransaksi
    ) {
        transaksiService.hapusTransaksi(idPengguna, idTransaksi);
        return ResponseEntity.ok(new GeneralResponse<>("Transaksi berhasil dihapus", null, true));
    }
}
