package com.andreas.backend.keuanganku.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateNamaAkunRequest {

    @NotBlank(message = "Nama akun baru tidak boleh kosong")
    private String nama;
}
