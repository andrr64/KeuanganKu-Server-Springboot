package com.andreas.backend.keuanganku.dto.response;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.andreas.backend.keuanganku.dto.AkunTransfer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TransferResponse {
    private UUID id;
    private AkunTransfer dataDariAkun;
    private AkunTransfer dataKeAkun;
    private BigDecimal jumlah;
    private LocalDateTime tanggal;
    private String catatan;
}

