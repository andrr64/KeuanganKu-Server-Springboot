package com.andreas.backend.keuanganku.dto.request.goal;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateGoalRequest {
    private String nama;
    private BigDecimal target;
    private String tanggalTarget;
}

