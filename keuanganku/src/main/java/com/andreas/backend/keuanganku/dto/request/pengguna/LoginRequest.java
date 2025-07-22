package com.andreas.backend.keuanganku.dto.request.pengguna;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @Email(message = "Format email tidak valid")
    @NotBlank(message = "Email wajib diisi")
    private String email;

    @NotBlank(message = "Password wajib diisi")
    private String password;

    public LoginRequest() {}
}
