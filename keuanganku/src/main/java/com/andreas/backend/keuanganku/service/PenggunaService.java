package com.andreas.backend.keuanganku.service;

import com.andreas.backend.keuanganku.dto.request.pengguna.UpdatePenggunaRequest;
import com.andreas.backend.keuanganku.model.Pengguna;

import java.util.UUID;

public interface PenggunaService {

    Pengguna getById(UUID idPengguna);
    Pengguna login(String email, String rawPassword);
    Pengguna register(String nama, String email, String rawPassword);

    void updateAkun(UUID idPengguna, UpdatePenggunaRequest request);

    boolean isPasswordLengthOk(String password);
}