package com.andreas.backend.keuanganku.controller.secure;

import java.util.List;

import com.andreas.backend.keuanganku.annotation.CurrentUserId;
import com.andreas.backend.keuanganku.dto.request.TransaksiRequest;
import com.andreas.backend.keuanganku.dto.response.GeneralResponse;
import com.andreas.backend.keuanganku.dto.response.TransaksiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import com.andreas.backend.keuanganku.service.TransaksiService;

@RestController
@RequestMapping("/api/secure/transaksi")
@RequiredArgsConstructor
public class TransaksiController {

    private final TransaksiService transaksiService;

    @PostMapping
    public ResponseEntity<?> tambahTransaksi(
            @CurrentUserId UUID idPengguna,
            @Valid @RequestBody TransaksiRequest request
    ) {
        transaksiService.tambahTransaksi(idPengguna, request);
        return ResponseEntity.ok(new GeneralResponse<>("Transaksi berhasil ditambahkan", null, true));
    }

    @GetMapping
    public ResponseEntity<?> getTransaksi(
            @CurrentUserId UUID idPengguna,
            @RequestParam(name = "jenis", required = false) Integer jenis,
            @RequestParam(name = "idAkun", required = false) UUID idAkun
    ) {
        List<TransaksiResponse> daftar = transaksiService.getFilteredTransaksi(idPengguna, jenis, idAkun);
        return ResponseEntity.ok(new GeneralResponse<>("Ok", daftar, true));
    }

    @PutMapping("/{id_transaksi}")
    public ResponseEntity<?> updateTransaksi(
            @CurrentUserId UUID idPengguna,
            @PathVariable("id_transaksi") UUID idTransaksi,
            @Valid @RequestBody TransaksiRequest request
    ) {
        transaksiService.updateTransaksi(idPengguna, idTransaksi, request);
        return ResponseEntity.ok(new GeneralResponse<>("Transaksi berhasil diperbarui", null, true));
    }

    @DeleteMapping("/{id_transaksi}")
    public ResponseEntity<?> hapusTransaksi(
            @CurrentUserId UUID idPengguna,
            @PathVariable("id_transaksi") UUID idTransaksi
    ) {
        transaksiService.hapusTransaksi(idPengguna, idTransaksi);
        return ResponseEntity.ok(new GeneralResponse<>("Transaksi berhasil dihapus", null, true));
    }

}
