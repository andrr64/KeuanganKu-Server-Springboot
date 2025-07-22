package com.andreas.backend.keuanganku.dto.response;

import java.math.BigDecimal;

public record DashboardResponse(
    BigDecimal totalSaldo,
    BigDecimal totalPemasukanBulanIni,
    BigDecimal totalPengeluaranBulanIni,
    BigDecimal cashflowBulanIni
) {}
