package com.andreas.backend.keuanganku.service;

import java.util.UUID;

import com.andreas.backend.keuanganku.dto.request.UbahPasswordRequest;
import com.andreas.backend.keuanganku.dto.request.UpdateAkunRequest;
import com.andreas.backend.keuanganku.dto.request.UpdatePenggunaRequest;
import com.andreas.backend.keuanganku.model.Pengguna;

public interface PenggunaService {

    Pengguna getById(UUID idPengguna);

    void updateNamaAtauEmail(UUID id, UpdatePenggunaRequest request);

    void ubahPassword(UUID id, UbahPasswordRequest request);

    Pengguna login(String email, String rawPassword);

    Pengguna register(String nama, String email, String rawPassword);
    void updateAkun(UUID idPengguna, UpdateAkunRequest request);
    boolean isPasswordLengthOk(String password);
}
