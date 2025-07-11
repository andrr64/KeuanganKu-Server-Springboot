package com.andreas.backend.keuanganku.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class TambahAkunRequest {
    @NotBlank(message = "Nama akun tidak boleh kosong")
    private String nama;
    @DecimalMin(value = "0.0", inclusive = true, message = "Saldo awal harus lebih dari sama dengan 0")
    private BigDecimal saldo;

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }
}
