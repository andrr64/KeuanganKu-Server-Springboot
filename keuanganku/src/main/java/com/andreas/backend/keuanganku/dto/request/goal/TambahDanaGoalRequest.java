package com.andreas.backend.keuanganku.dto.request.goal;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TambahDanaGoalRequest {
    private BigDecimal jumlah;
}
