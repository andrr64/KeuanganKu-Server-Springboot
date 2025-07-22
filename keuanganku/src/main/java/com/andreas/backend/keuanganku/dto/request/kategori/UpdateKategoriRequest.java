package com.andreas.backend.keuanganku.dto.request.kategori;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateKategoriRequest {
    @NotBlank(message = "Nama kategori tidak boleh kosong")
    private String nama;
}
