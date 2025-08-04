package com.andreas.backend.keuanganku.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AkunRequest {
    @NotNull(message = "Nama akun tidak boleh kosong")
    @NotBlank(message = "Nama akun tidak boleh kosong")
    private String namaAkun;

    @NotNull(message = "Saldo awal tidak boleh kosong")
    @DecimalMin(value="0.001", message = "Saldo awal harus lebih besar dari 0")
    private BigDecimal saldoAwal;
}
