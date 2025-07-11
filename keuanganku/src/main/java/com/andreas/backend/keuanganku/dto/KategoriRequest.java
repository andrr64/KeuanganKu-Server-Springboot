package com.andreas.backend.keuanganku.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KategoriRequest {

    @NotBlank(message = "Nama kategori tidak boleh kosong")
    private String nama;

    @Min(value = 1, message = "Jenis harus 1 (pemasukan) atau 2 (pengeluaran)")
    @Max(value = 2, message = "Jenis harus 1 (pemasukan) atau 2 (pengeluaran)")
    private Integer jenis;
}
