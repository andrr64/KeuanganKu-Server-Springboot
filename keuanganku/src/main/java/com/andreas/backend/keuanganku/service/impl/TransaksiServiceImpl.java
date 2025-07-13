package com.andreas.backend.keuanganku.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.andreas.backend.keuanganku.SysVar;
import com.andreas.backend.keuanganku.dto.request.TransaksiRequest;
import com.andreas.backend.keuanganku.model.Akun;
import com.andreas.backend.keuanganku.model.Kategori;
import com.andreas.backend.keuanganku.model.Transaksi;
import com.andreas.backend.keuanganku.repository.AkunRepository;
import com.andreas.backend.keuanganku.repository.KategoriRepository;
import com.andreas.backend.keuanganku.repository.TransaksiRepository;
import com.andreas.backend.keuanganku.service.TransaksiService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class TransaksiServiceImpl implements TransaksiService {

    private final AkunRepository akunRepo;
    private final KategoriRepository kategoriRepo;
    private final TransaksiRepository transaksiRepo;

    @Override
    public void tambahTransaksi(UUID idPengguna, TransaksiRequest request) {
        // Ambil akun
        UUID idAkun = UUID.fromString(request.getIdAkun());
        Akun akun = akunRepo.findById(idAkun)
                .filter(a -> a.getPengguna().getId().equals(idPengguna))
                .orElseThrow(() -> new EntityNotFoundException("Akun tidak ditemukan atau bukan milik pengguna"));

        // Ambil kategori
        UUID idKategori = UUID.fromString(request.getIdKategori());
        Kategori kategori = kategoriRepo.findById(idKategori)
                .filter(k -> k.getPengguna().getId().equals(idPengguna))
                .orElseThrow(() -> new EntityNotFoundException("Kategori tidak ditemukan atau bukan milik pengguna"));

        // Parse tanggal
        LocalDateTime tanggal;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            tanggal = LocalDateTime.parse(request.getTanggal(), formatter);
        } catch (Exception e) {
            throw new IllegalArgumentException("Format tanggal tidak valid. Gunakan dd/MM/yyyy HH:mm");
        }

        // Validasi saldo cukup jika pengeluaran
        if (SysVar.isPengeluaran(kategori.getJenis())) { 
            if (akun.getSaldo().compareTo(request.getJumlah()) < 0) {
                throw new IllegalArgumentException("Saldo tidak mencukupi untuk pengeluaran (" + akun.getSaldo() + ")");
            }

            akun.setSaldo(akun.getSaldo().subtract(request.getJumlah()));
        } else if (SysVar.isPemasukan(kategori.getJenis())) {
            // Tambahkan saldo jika pemasukan
            akun.setSaldo(akun.getSaldo().add(request.getJumlah()));
        } else {
            throw new IllegalArgumentException("Jenis kategori tidak valid");
        }

        // Simpan perubahan saldo akun
        akunRepo.save(akun);

        // Buat dan simpan transaksi
        Transaksi transaksi = new Transaksi();
        transaksi.setAkun(akun);
        transaksi.setKategori(kategori);
        transaksi.setJumlah(request.getJumlah());
        transaksi.setTanggal(tanggal);
        transaksi.setCatatan(request.getCatatan());
        transaksi.setDibuatPada(LocalDateTime.now());

        transaksiRepo.save(transaksi);
    }

}
