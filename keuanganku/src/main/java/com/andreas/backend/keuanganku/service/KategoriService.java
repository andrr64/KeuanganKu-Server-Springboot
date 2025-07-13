package com.andreas.backend.keuanganku.service;

import java.util.List;
import java.util.UUID;

import com.andreas.backend.keuanganku.model.Kategori;

public interface KategoriService {
    void tambahKategori(UUID idPengguna, Integer jenis, String nama);
    List<Kategori> getKategoriByJenis(UUID idPengguna, Integer jenis);
}