package com.andreas.backend.keuanganku.service.impl;

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

    @Override
    public void updateKategori(UUID idPengguna, UUID idKategori, String namaBaru) {
        // 1. Find the category and ensure it belongs to the user
        Kategori kategori = kategoriRepo.findById(idKategori)
                .filter(k -> k.getPengguna().getId().equals(idPengguna))
                .orElseThrow(() -> new EntityNotFoundException("Kategori tidak ditemukan atau bukan milik pengguna"));

        // Trim whitespace from the new name for consistent validation
        String trimmedNamaBaru = namaBaru.trim();

        // 2. Logika: Jika namaBaru sama persis dengan nama lama (case-insensitive), maka error
        if (kategori.getNama().equalsIgnoreCase(trimmedNamaBaru)) {
            throw new IllegalArgumentException("Nama kategori tidak berubah. Harap berikan nama baru.");
        }

        // 3. Logika: Jika namaBaru sudah digunakan oleh kategori lain untuk pengguna ini (case-insensitive)
        boolean namaSudahDigunakanOlehLain = kategoriRepo.findByPenggunaId(idPengguna).stream()
                .anyMatch(k -> !k.getId().equals(idKategori) // Exclude the current category being updated
                && k.getNama().equalsIgnoreCase(trimmedNamaBaru));

        if (namaSudahDigunakanOlehLain) {
            throw new IllegalArgumentException("Nama kategori '" + trimmedNamaBaru + "' sudah digunakan oleh kategori lain.");
        }

        // 4. Validasi jenis kategori (pertahankan jika logikanya masih relevan)
        // Asumsi SysVar.isPemasukan dan SysVar.isPengeluaran berfungsi seperti sebelumnya
        if (kategori.getJenis() == null || (!SysVar.isPemasukan(kategori.getJenis()) && !SysVar.isPengeluaran(kategori.getJenis()))) {
            throw new IllegalArgumentException("Jenis kategori tidak valid.");
        }

        // 5. Update nama dan simpan
        kategori.setNama(trimmedNamaBaru);
        kategoriRepo.save(kategori);
    }

    @Override // Assuming this method is part of an interface
    public void hapusKategori(UUID idPengguna, UUID idKategori, boolean ubahTransaksi, UUID targetKategoriId) {
        // 1. Find the category to be deleted (oldKategori) and validate ownership
        Kategori kategoriToDelete = kategoriRepo.findById(idKategori)
                .filter(k -> k.getPengguna().getId().equals(idPengguna))
                .orElseThrow(() -> new EntityNotFoundException("Kategori tidak ditemukan atau bukan milik pengguna."));

        // 2. Get all transactions associated with this category
        List<Transaksi> transaksiList = transaksiRepo.findAllByKategoriId(idKategori);

        // 3. Apply the logic based on 'ubahTransaksi'
        if (ubahTransaksi) {
            // Logic 1: If ubahTransaksi == true, migrate transactions

            // Ensure targetKategoriId is provided
            if (targetKategoriId == null) {
                throw new IllegalArgumentException("Target kategori ID tidak boleh kosong jika ingin mengubah transaksi.");
            }

            // Ensure targetKategori is not the same as the category being deleted
            if (kategoriToDelete.getId().equals(targetKategoriId)) {
                throw new IllegalArgumentException("Target kategori tidak boleh sama dengan kategori yang akan dihapus.");
            }

            // Find and validate ownership of targetKategori
            Kategori targetKategori = kategoriRepo.findById(targetKategoriId)
                    .filter(k -> k.getPengguna().getId().equals(idPengguna))
                    .orElseThrow(() -> new EntityNotFoundException("Target kategori tidak ditemukan atau bukan milik pengguna."));

            // **CRITICAL LOGIC:** Ensure targetKategori.jenis == oldKategori.jenis
            if (!kategoriToDelete.getJenis().equals(targetKategori.getJenis())) {
                throw new IllegalArgumentException("Jenis target kategori harus sama dengan jenis kategori yang dihapus.");
            }

            // Update all associated transactions to the target category
            for (Transaksi t : transaksiList) {
                t.setKategori(targetKategori);
            }
            transaksiRepo.saveAll(transaksiList); // Save updated transactions

        } else {
            // Logic 2: If there are transactions and ubahTransaksi == false, prevent deletion
            if (!transaksiList.isEmpty()) {
                throw new IllegalArgumentException("Kategori tidak dapat dihapus karena masih memiliki transaksi. Gunakan parameter 'ubahTransaksi' untuk memindahkan transaksi.");
            }
            // If ubahTransaksi is false AND no transactions exist, proceed to delete the category
            // No action needed here for transactions, as there are none
        }

        // 4. Finally, delete the category itself
        kategoriRepo.delete(kategoriToDelete);
    }

}
