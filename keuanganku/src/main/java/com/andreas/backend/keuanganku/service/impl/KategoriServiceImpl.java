package com.andreas.backend.keuanganku.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.andreas.backend.keuanganku.model.Kategori;
import com.andreas.backend.keuanganku.model.Pengguna;
import com.andreas.backend.keuanganku.repository.KategoriRepository;
import com.andreas.backend.keuanganku.repository.PenggunaRepository;
import com.andreas.backend.keuanganku.service.KategoriService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KategoriServiceImpl implements KategoriService {

    private final KategoriRepository kategoriRepo;
    private final PenggunaRepository penggunaRepo;

    @Override
    public void tambahKategori(UUID idPengguna, Integer jenis, String nama) {
        if (jenis != 1 && jenis != 2) {
            throw new IllegalArgumentException("Jenis hanya boleh 1 (pengeluaran) atau 2 (pemasukan)");
        }

        Pengguna pengguna = penggunaRepo.findById(idPengguna)
                .orElseThrow(() -> new EntityNotFoundException("Pengguna tidak ditemukan"));

        boolean sudahAda = kategoriRepo
                .findByPenggunaIdAndJenis(idPengguna, jenis)
                .stream()
                .anyMatch(k -> k.getNama().equalsIgnoreCase(nama));

        if (sudahAda) {
            throw new IllegalArgumentException("Nama kategori sudah ada");
        }

        Kategori kategori = new Kategori();
        kategori.setNama(nama);
        kategori.setJenis(jenis);
        kategori.setPengguna(pengguna);

        kategoriRepo.save(kategori);
    }

    @Override
    public List<Kategori> getKategoriByJenis(UUID idPengguna, Integer jenis) {
        return kategoriRepo.findByPenggunaIdAndJenis(idPengguna, jenis);
    }
}
