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
import com.andreas.backend.keuanganku.middleware.RequestLoggingFilter;
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

        RequestLoggingFilter.log.info("=== REQUEST TRANSAKSI DENGAN PARAMETER ===");
        RequestLoggingFilter.log.info("idPengguna : {}", idPengguna);
        RequestLoggingFilter.log.info("startDate  : {}", startDate);
        RequestLoggingFilter.log.info("endDate    : {}", endDate);
        RequestLoggingFilter.log.info("jenis      : {}", jenis);
        RequestLoggingFilter.log.info("idAkun     : {}", idAkun);
        RequestLoggingFilter.log.info("page       : {}", page);
        RequestLoggingFilter.log.info("size       : {}", size);

        Map<String, Object> response = new HashMap<>();
        response.put("content", daftar.getContent());
        response.put("currentPage", daftar.getNumber());
        response.put("totalItems", daftar.getTotalElements());
        response.put("totalPages", daftar.getTotalPages());

        return ResponseEntity.ok(new GeneralResponse<>("Ok", response, true));
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
