package com.andreas.backend.keuanganku.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.andreas.backend.keuanganku.model.Kategori;

public interface KategoriService {
    void tambahKategori(UUID idPengguna, Integer jenis, String nama);
    List<Kategori> getKategoriByJenis(UUID idPengguna, Integer jenis);
    List<Kategori> getAllKategori(UUID idPengguna);
    void updateKategori(UUID idPengguna, UUID idKategori, String namaBaru);
    void hapusKategori(UUID idPengguna, UUID idKategori);
    Page<Kategori> getFilteredKategori(UUID idPengguna, int jenis, String keyword, int page, int size);
}