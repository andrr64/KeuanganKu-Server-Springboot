package com.andreas.backend.keuanganku.controller.secure;

import java.util.List;
import java.util.UUID;

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
