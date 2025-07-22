package com.andreas.backend.keuanganku.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UbahPasswordRequest {

    @NotBlank
    private String passwordLama;

    @NotBlank
    @Size(min = 8, message = "Password minimal 8 karakter")
    private String passwordBaru;
}
