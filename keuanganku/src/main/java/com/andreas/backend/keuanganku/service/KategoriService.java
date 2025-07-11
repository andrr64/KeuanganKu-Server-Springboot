package com.andreas.backend.keuanganku.service;

import com.andreas.backend.keuanganku.model.Kategori;
import com.andreas.backend.keuanganku.model.Pengguna;
import com.andreas.backend.keuanganku.repository.KategoriRepository;
import com.andreas.backend.keuanganku.repository.PenggunaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class KategoriService {

    @Autowired
    private KategoriRepository kategoriRepo;

    @Autowired
    private PenggunaRepository penggunaRepo;

    public List<Kategori> getAllByPenggunaId(UUID penggunaId) {
        return kategoriRepo.findByPengguna_Id(penggunaId);
    }

    public Kategori simpanKategori(UUID penggunaId, String nama, Integer jenis) {
        Pengguna pengguna = penggunaRepo.findById(penggunaId)
                .orElseThrow(() -> new RuntimeException("Pengguna tidak ditemukan"));

        Kategori kategori = new Kategori();
        kategori.setNama(nama);
        kategori.setJenis(jenis);
        kategori.setPengguna(pengguna);

        return kategoriRepo.save(kategori); // ✅ UUID akan otomatis dibuat oleh JPA
    }
}
