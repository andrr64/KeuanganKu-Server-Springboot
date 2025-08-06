package com.andreas.backend.keuanganku.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class GoalResponse {
    private UUID id;
    private String nama;
    private BigDecimal target;
    private BigDecimal terkumpul;
    private OffsetDateTime tanggalTarget;
    private Boolean tercapai;
}
