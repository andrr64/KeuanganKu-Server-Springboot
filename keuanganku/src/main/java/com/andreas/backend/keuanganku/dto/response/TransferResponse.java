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
public class TransferResponse {
    private UUID id;
    private UUID idDariAkun;
    private UUID idKeAkun;
    private BigDecimal jumlah;
    private LocalDateTime tanggal;
    private String catatan;

}

