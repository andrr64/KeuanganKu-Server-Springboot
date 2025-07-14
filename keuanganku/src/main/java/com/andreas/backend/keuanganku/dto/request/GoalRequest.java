package com.andreas.backend.keuanganku.dto.request;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class GoalRequest {
    private String nama;
    private BigDecimal target;
    private String tanggalTarget;
}
