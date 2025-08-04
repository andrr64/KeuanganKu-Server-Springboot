// dto/response/TransferResponse.java
package com.andreas.backend.keuanganku.dto.response;

import com.andreas.backend.keuanganku.dto.AkunTransfer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class TransferResponse {
    private UUID id;
    private AkunTransfer dataDariAkun;
    private AkunTransfer dataKeAkun;
    private BigDecimal jumlah;
    private OffsetDateTime tanggal;
    private String catatan;
}