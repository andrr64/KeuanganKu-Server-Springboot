package com.andreas.backend.keuanganku.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class DetailTransferResponse {
    private UUID id;
    private UUID idDariAkun;
    private String namaDariAkun;
    private UUID idKeAkun;
    private String namaKeAkun;
    private BigDecimal jumlah;
    private LocalDateTime tanggal;
    private String catatan;
}