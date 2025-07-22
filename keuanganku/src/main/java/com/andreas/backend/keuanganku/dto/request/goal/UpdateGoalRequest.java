package com.andreas.backend.keuanganku.dto.request.goal;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class UpdateGoalRequest {
    private String nama;
    private BigDecimal target;
    private String tanggalTarget;
}

