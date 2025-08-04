package com.andreas.backend.keuanganku.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class TransferRequest {
    private UUID idDariAkun;
    private UUID idKeAkun;
    private BigDecimal jumlah;
    private String tanggal; // format dd/MM/yyyy HH:mm
    private String catatan;
}
