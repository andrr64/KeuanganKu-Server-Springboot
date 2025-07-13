package com.andreas.backend.keuanganku.service;

import java.util.UUID;

import com.andreas.backend.keuanganku.dto.request.TransaksiRequest;

public interface TransaksiService {
    void tambahTransaksi(UUID idPengguna, TransaksiRequest request);
}
