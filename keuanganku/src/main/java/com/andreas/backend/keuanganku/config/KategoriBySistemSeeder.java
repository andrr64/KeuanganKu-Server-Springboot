package com.andreas.backend.keuanganku.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.andreas.backend.keuanganku.model.Kategori;
import com.andreas.backend.keuanganku.repository.KategoriRepository;

@Component
public class KategoriBySistemSeeder implements CommandLineRunner {

    private final KategoriRepository kategoriRepo;

    public KategoriBySistemSeeder(KategoriRepository kategoriRepo) {
        this.kategoriRepo = kategoriRepo;
    }

    @Override
    public void run(String... args) {
        List<String> kategoriPengeluaran = List.of(
                "Makanan", "Minuman", "Transportasi", "Gaya Hidup", "Hiburan", "Tagihan", "Belanja", "Pendidikan", "Kesehatan"
        );

        List<String> kategoriPemasukan = List.of(
                "Gaji", "Bonus", "Freelance", "Investasi", "Lainnya"
        );

        for (String nama : kategoriPengeluaran) {
            if (!kategoriRepo.existsByNama(nama)) {
                Kategori kategori = new Kategori();
                kategori.setNama(nama);
                kategori.setJenis(1); // Pengeluaran
                kategori.setPengguna(null); // <- opsional
                kategoriRepo.save(kategori);
            }
        }

        for (String nama : kategoriPemasukan) {
            if (!kategoriRepo.existsByNama(nama)) {
                Kategori kategori = new Kategori();
                kategori.setNama(nama);
                kategori.setJenis(2); // Pemasukan
                kategori.setPengguna(null); // <- opsional
                kategoriRepo.save(kategori);
            }
        }
    }

}
