package com.andreas.backend.keuanganku.dto.response;

import com.andreas.backend.keuanganku.model.Kategori;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class TransaksiResponse {
    private UUID id;
    private UUID idAkun;
    private String namaKategori;
    private String namaAkun;
    private Integer jenisTransaksi; // 1 = pengeluaran, 2 = pemasukan
    private BigDecimal jumlah;
    private String catatan;
    private OffsetDateTime tanggal; // ✅ Diperbaiki: LocalDateTime → OffsetDateTime
    private Kategori kategori;
}