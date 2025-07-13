package com.andreas.backend.keuanganku.service;

import java.util.List;
import java.util.UUID;

import com.andreas.backend.keuanganku.dto.request.TransaksiRequest;
import com.andreas.backend.keuanganku.dto.response.TransaksiResponse;

public interface TransaksiService {
    void tambahTransaksi(UUID idPengguna, TransaksiRequest request);
    List<TransaksiResponse> getFilteredTransaksi(UUID idPengguna, Integer jenis, UUID idAkun);
    void updateTransaksi(UUID idPengguna, UUID idTransaksi, TransaksiRequest request);
    void hapusTransaksi(UUID idPengguna, UUID idTransaksi);
}
