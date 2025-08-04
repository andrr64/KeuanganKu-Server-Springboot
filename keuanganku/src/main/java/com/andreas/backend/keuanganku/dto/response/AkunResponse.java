package com.andreas.backend.keuanganku.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class AkunResponse {
    private UUID id;
    private String nama;
    private BigDecimal saldo;
    private OffsetDateTime dibuatPada;
}
