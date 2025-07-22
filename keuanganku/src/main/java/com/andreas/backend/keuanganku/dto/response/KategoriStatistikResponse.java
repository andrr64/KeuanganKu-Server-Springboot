package com.andreas.backend.keuanganku.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KategoriStatistikResponse {
    private String namaKategori;
    private BigDecimal totalPengeluaran;
}
