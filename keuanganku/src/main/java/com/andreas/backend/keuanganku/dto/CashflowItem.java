package com.andreas.backend.keuanganku.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CashflowItem {
    String tanggal;
    Double pemasukan;
    Double pengeluaran;
}
