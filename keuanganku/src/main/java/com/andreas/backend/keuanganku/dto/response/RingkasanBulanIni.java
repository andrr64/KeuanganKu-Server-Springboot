package com.andreas.backend.keuanganku.dto.response;

import java.math.BigDecimal;

public record RingkasanBulanIni(
    BigDecimal totalSaldo,
    BigDecimal totalPemasukanBulanIni,
    BigDecimal totalPengeluaranBulanIni,
    BigDecimal cashflowBulanIni
) {}
