package com.andreas.backend.keuanganku.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class KategoriStatistikResponse {
    private String namaKategori;
    private BigDecimal totalPengeluaran;
}
