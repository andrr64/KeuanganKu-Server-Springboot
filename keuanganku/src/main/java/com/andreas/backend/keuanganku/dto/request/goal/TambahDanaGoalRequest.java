package com.andreas.backend.keuanganku.dto.request.goal;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class TambahDanaGoalRequest {
    private BigDecimal jumlah;
}
