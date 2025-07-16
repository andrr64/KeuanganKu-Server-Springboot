package com.andreas.backend.keuanganku.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransaksiResponse {
    private UUID id;
    private UUID idAkun;
    private String namaKategori;
    private String namaAkun;
    private Integer jenisTransaksi; // 1 pengeluarna, 2 pemasukan
    private BigDecimal jumlah;
    private String catatan;
    private LocalDateTime tanggal;
}
    