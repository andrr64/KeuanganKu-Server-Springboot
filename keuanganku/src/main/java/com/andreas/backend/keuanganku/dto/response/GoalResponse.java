package com.andreas.backend.keuanganku.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import lombok.Data;

@Data
public class GoalResponse {
    private UUID id;
    private String nama;
    private BigDecimal target;
    private BigDecimal terkumpul;
    private LocalDate tanggalTarget;
    private Boolean tercapai;
}
