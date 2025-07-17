package com.andreas.backend.keuanganku.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.andreas.backend.keuanganku.SysVar;
import com.andreas.backend.keuanganku.model.Kategori;
import com.andreas.backend.keuanganku.model.Pengguna;
import com.andreas.backend.keuanganku.model.Transaksi;
import com.andreas.backend.keuanganku.repository.KategoriRepository;
import com.andreas.backend.keuanganku.repository.PenggunaRepository;
import com.andreas.backend.keuanganku.repository.TransaksiRepository;
import com.andreas.backend.keuanganku.service.KategoriService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KategoriServiceImpl implements KategoriService {

    private final TransaksiRepository transaksiRepo;
    private final KategoriRepository kategoriRepo;
    private final PenggunaRepository penggunaRepo;

    @Override
    public void tambahKategori(UUID idPengguna, Integer jenis, String nama) {
        if (jenis != 1 && jenis != 2) {
            throw new IllegalArgumentException("Jenis hanya boleh 1 (pengeluaran) atau 2 (pemasukan)");
        }

        Pengguna pengguna = penggunaRepo.findById(idPengguna)
                .orElseThrow(() -> new EntityNotFoundException("Pengguna tidak ditemukan"));

        boolean sudahAda = kategoriRepo.existsByPenggunaIdAndNamaIgnoreCaseAndJenis(idPengguna, nama, jenis);
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
        List<Kategori> kategoriPribadi = kategoriRepo.findByPenggunaIdAndJenis(idPengguna, jenis);
        List<Kategori> kategoriSistem = kategoriRepo.findByPenggunaIsNullAndJenis(jenis);

        List<Kategori> kategoriGabungan = new ArrayList<>(kategoriPribadi);
        kategoriGabungan.addAll(kategoriSistem);

        return kategoriGabungan;
    }

    @Override
    public List<Kategori> getAllKategori(UUID idPengguna) {
        List<Kategori> kategoriPribadi = kategoriRepo.findByPenggunaId(idPengguna);
        List<Kategori> kategoriSistem = kategoriRepo.findByPenggunaIsNull();

        List<Kategori> kategoriGabungan = new ArrayList<>(kategoriPribadi);
        kategoriGabungan.addAll(kategoriSistem);

        return kategoriGabungan;
    }

    @Override
    public void updateKategori(UUID idPengguna, UUID idKategori, String namaBaru) {
        Kategori kategori = kategoriRepo.findById(idKategori)
                .filter(k -> k.getPengguna() != null && k.getPengguna().getId().equals(idPengguna))
                .orElseThrow(() -> new EntityNotFoundException("Kategori tidak ditemukan atau bukan milik pengguna"));

        String trimmedNamaBaru = namaBaru.trim();

        if (kategori.getNama().equalsIgnoreCase(trimmedNamaBaru)) {
            throw new IllegalArgumentException("Nama kategori tidak berubah. Harap berikan nama baru.");
        }

        boolean namaSudahDigunakanOlehLain = kategoriRepo.findByPenggunaId(idPengguna).stream()
                .anyMatch(k -> !k.getId().equals(idKategori)
                        && k.getNama().equalsIgnoreCase(trimmedNamaBaru));

        if (namaSudahDigunakanOlehLain) {
            throw new IllegalArgumentException("Nama kategori '" + trimmedNamaBaru + "' sudah digunakan.");
        }

        if (kategori.getJenis() == null || (!SysVar.isPemasukan(kategori.getJenis()) && !SysVar.isPengeluaran(kategori.getJenis()))) {
            throw new IllegalArgumentException("Jenis kategori tidak valid.");
        }

        kategori.setNama(trimmedNamaBaru);
        kategoriRepo.save(kategori);
    }

    @Override
    public void hapusKategori(UUID idPengguna, UUID idKategori, boolean ubahTransaksi, UUID targetKategoriId) {
        Kategori kategoriToDelete = kategoriRepo.findById(idKategori)
                .filter(k -> k.getPengguna() != null && k.getPengguna().getId().equals(idPengguna))
                .orElseThrow(() -> new EntityNotFoundException("Kategori tidak ditemukan atau bukan milik pengguna."));

        List<Transaksi> transaksiList = transaksiRepo.findAllByKategoriId(idKategori);

        if (ubahTransaksi) {
            if (targetKategoriId == null) {
                throw new IllegalArgumentException("Target kategori tidak boleh kosong.");
            }

            if (kategoriToDelete.getId().equals(targetKategoriId)) {
                throw new IllegalArgumentException("Target kategori tidak boleh sama dengan kategori yang dihapus.");
            }

            Kategori targetKategori = kategoriRepo.findById(targetKategoriId)
                    .filter(k -> k.getPengguna() == null || k.getPengguna().getId().equals(idPengguna))
                    .orElseThrow(() -> new EntityNotFoundException("Target kategori tidak ditemukan."));

            if (!kategoriToDelete.getJenis().equals(targetKategori.getJenis())) {
                throw new IllegalArgumentException("Jenis kategori harus sama.");
            }

            for (Transaksi t : transaksiList) {
                t.setKategori(targetKategori);
            }

            transaksiRepo.saveAll(transaksiList);
        } else {
            if (!transaksiList.isEmpty()) {
                throw new IllegalArgumentException("Kategori memiliki transaksi. Gunakan opsi ubahTransaksi.");
            }
        }

        kategoriRepo.delete(kategoriToDelete);
    }
}
