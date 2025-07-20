package com.andreas.backend.keuanganku.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransaksiRequest {

    @NotBlank(message = "idKategori wajib diisi")
    private String idKategori;

    @NotBlank(message = "idAkun wajib diisi")
    private String idAkun;

    @DecimalMin(value = "0.001", message = "Jumlah harus lebih dari 0")
    private BigDecimal jumlah;

    @NotBlank(message = "Tanggal wajib diisi dan dalam format dd/MM/yyyy HH:mm")
    private String tanggal;

    private String catatan;
}
