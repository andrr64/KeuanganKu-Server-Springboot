package com.andreas.backend.keuanganku.controller.secure;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
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

@RestController
@RequestMapping("/api/secure/transaksi")
@RequiredArgsConstructor
public class TransaksiController {

    private final TransaksiService transaksiService;

    /**
     * Menambahkan transaksi baru milik pengguna.
     *
     * @param idPengguna ID pengguna yang melakukan request
     * @param request Data transaksi yang akan ditambahkan
     * @return Response sukses jika berhasil menambahkan
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
     * Mengambil daftar transaksi berdasarkan filter.
     *
     * @param idPengguna ID pengguna yang meminta data
     * @param startDate Tanggal mulai filter (opsional, format dd/MM/yyyy)
     * @param endDate Tanggal akhir filter (opsional, format dd/MM/yyyy)
     * @param jenis Jenis kategori (1 = pengeluaran, 2 = pemasukan, null =
     * semua)
     * @param idAkun ID akun tertentu untuk memfilter (opsional)
     * @param page Nomor halaman (dimulai dari 0)
     * @param keyword Kata kunci pencarian pada catatan atau nama kategori
     * (opsional)
     * @param size Jumlah data per halaman
     * @return Data transaksi yang sudah difilter dalam bentuk paginasi
     */
    @GetMapping()
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

        Map<String, Object> response = new HashMap<>();
        response.put("content", daftar.getContent());
        response.put("currentPage", daftar.getNumber());
        response.put("totalItems", daftar.getTotalElements());
        response.put("totalPages", daftar.getTotalPages());

        return ResponseEntity.ok(new GeneralResponse<>("Ok", response, true));
    }

    /**
     * Memperbarui transaksi berdasarkan ID transaksi.
     *
     * @param idPengguna ID pengguna saat ini
     * @param idTransaksi ID transaksi yang akan diperbarui
     * @param request Data transaksi yang baru
     * @return Respon sukses jika berhasil diperbarui
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
     * @param idPengguna ID pengguna saat ini
     * @param idTransaksi ID transaksi yang akan dihapus
     * @return Respon sukses jika berhasil dihapus
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
