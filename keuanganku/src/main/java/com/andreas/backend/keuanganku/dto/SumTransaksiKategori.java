package com.andreas.backend.keuanganku.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SumTransaksiKategori {
    private String label;
    private BigDecimal value;
}
