package com.andreas.backend.keuanganku.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class KategoriRequest {
    @NotBlank(message = "Nama kategori tidak boleh kosong")
    private String nama;
}
