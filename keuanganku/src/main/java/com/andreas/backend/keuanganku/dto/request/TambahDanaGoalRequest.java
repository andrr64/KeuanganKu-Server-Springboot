package com.andreas.backend.keuanganku.dto.request;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class TambahDanaGoalRequest {
    private BigDecimal jumlah;
}
