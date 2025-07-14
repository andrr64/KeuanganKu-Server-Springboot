package com.andreas.backend.keuanganku.service;

import com.andreas.backend.keuanganku.dto.request.UbahPasswordRequest;
import com.andreas.backend.keuanganku.dto.request.UpdatePenggunaRequest;
import com.andreas.backend.keuanganku.model.Pengguna;

import java.util.UUID;

public interface PenggunaService {

    Pengguna getById(UUID idPengguna);

    void updateNamaAtauEmail(UUID id, UpdatePenggunaRequest request);

    void ubahPassword(UUID id, UbahPasswordRequest request);

    Pengguna login(String email, String rawPassword);

    Pengguna register(String nama, String email, String rawPassword);

    boolean isPasswordLengthOk(String password);
}
