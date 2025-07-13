package com.andreas.backend.keuanganku.service;

import java.util.UUID;

import com.andreas.backend.keuanganku.dto.request.AkunRequest;
import com.andreas.backend.keuanganku.model.Akun;

public interface AkunService {
    Akun tambahAkun(UUID idPengguna, AkunRequest request);
}