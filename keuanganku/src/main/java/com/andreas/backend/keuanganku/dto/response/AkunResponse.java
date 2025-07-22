package com.andreas.backend.keuanganku.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AkunResponse {
    private UUID id;
    private String nama;
    private BigDecimal saldo;
    private LocalDateTime dibuatPada;
}
