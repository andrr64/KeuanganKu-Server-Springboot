package com.andreas.backend.keuanganku.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SumTransaksiKategori {
    private String label;
    private BigDecimal value;
}
