package com.andreas.backend.keuanganku.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.andreas.backend.keuanganku.SysVar;
import com.andreas.backend.keuanganku.dto.request.TransaksiRequest;
import com.andreas.backend.keuanganku.dto.response.TransaksiResponse;
import com.andreas.backend.keuanganku.model.Akun;
import com.andreas.backend.keuanganku.model.Kategori;
import com.andreas.backend.keuanganku.model.Transaksi;
import com.andreas.backend.keuanganku.repository.AkunRepository;
import com.andreas.backend.keuanganku.repository.KategoriRepository;
import com.andreas.backend.keuanganku.repository.TransaksiRepository;
import com.andreas.backend.keuanganku.service.TransaksiService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TransaksiServiceImpl implements TransaksiService {

    private final AkunRepository akunRepo;
    private final KategoriRepository kategoriRepo;
    private final TransaksiRepository transaksiRepo;

    @Override
    public void tambahTransaksi(UUID idPengguna, TransaksiRequest request) {
        // 🛡️ Validasi awal request
        if (request == null) {
            throw new IllegalArgumentException("Request tidak boleh null");
        }

        // 🧮 Validasi jumlah
        if (request.getJumlah() == null || request.getJumlah().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Jumlah harus lebih dari 0");
        }

        // 🏦 Validasi akun milik pengguna
        UUID idAkun = UUID.fromString(request.getIdAkun());
        Akun akun = akunRepo.findById(idAkun)
                .filter(a -> a.getPengguna().getId().equals(idPengguna))
                .orElseThrow(() -> new EntityNotFoundException("Akun tidak ditemukan atau bukan milik pengguna"));

        // 🗂️ Validasi kategori (kategori sistem atau milik pengguna)
        UUID idKategori = UUID.fromString(request.getIdKategori());
        Kategori kategori = kategoriRepo.findById(idKategori)
                .filter(k -> k.getPengguna() == null || k.getPengguna().getId().equals(idPengguna))
                .orElseThrow(() -> new EntityNotFoundException("Kategori tidak ditemukan atau bukan milik pengguna"));

        // 🕒 Parsing tanggal
        LocalDateTime tanggal;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            tanggal = LocalDateTime.parse(request.getTanggal(), formatter);
        } catch (Exception e) {
            throw new IllegalArgumentException("Format tanggal tidak valid. Gunakan dd/MM/yyyy HH:mm");
        }

        // 💰 Validasi dan update saldo akun
        if (SysVar.isPengeluaran(kategori.getJenis())) {
            if (akun.getSaldo().compareTo(request.getJumlah()) < 0) {
                throw new IllegalArgumentException("Saldo tidak mencukupi untuk pengeluaran (" + akun.getSaldo() + ")");
            }
            akun.setSaldo(akun.getSaldo().subtract(request.getJumlah()));
        } else if (SysVar.isPemasukan(kategori.getJenis())) {
            akun.setSaldo(akun.getSaldo().add(request.getJumlah()));
        } else {
            throw new IllegalArgumentException("Jenis kategori tidak valid");
        }

        // 💾 Simpan saldo akun terbaru
        akunRepo.save(akun);

        // 📝 Buat dan simpan transaksi
        Transaksi transaksi = new Transaksi();
        transaksi.setAkun(akun);
        transaksi.setKategori(kategori);
        transaksi.setJumlah(request.getJumlah());
        transaksi.setTanggal(tanggal);
        transaksi.setCatatan(request.getCatatan());
        transaksi.setDibuatPada(LocalDateTime.now());
        transaksi.setPengguna(akun.getPengguna()); // atau bisa juga ambil dari penggunaRepo.findById(idPengguna)
        transaksiRepo.save(transaksi);
    }

    @Override
    public List<TransaksiResponse> getFilteredTransaksi(UUID idPengguna, Integer jenis, UUID idAkun) {
        List<Transaksi> transaksiList;

        if (idAkun != null) {
            // Jika idAkun ada → filter berdasarkan akun saja
            transaksiList = transaksiRepo.findByPenggunaAndAkun(idPengguna, idAkun);
        } else if (jenis != null) {
            // Jika hanya jenis → filter berdasarkan jenis saja
            transaksiList = transaksiRepo.findByPenggunaAndJenis(idPengguna, jenis);
        } else {
            // Jika tidak ada dua-duanya → ambil semua
            transaksiList = transaksiRepo.findByPengguna(idPengguna);
        }

        return transaksiList.stream().map(t -> new TransaksiResponse(
                t.getId(),
                t.getAkun().getId(),
                t.getKategori().getNama(),
                t.getAkun().getNama(),
                t.getKategori().getJenis(),
                t.getJumlah(),
                t.getCatatan(),
                t.getTanggal(),
                t.getKategori()
        )).toList();
    }

    @Override
    public void updateTransaksi(UUID idPengguna, UUID idTransaksi, TransaksiRequest request) {
        // 1. Validate and get existing transaction
        Transaksi transaksiLama = transaksiRepo.findById(idTransaksi)
                .filter(t -> t.getAkun().getPengguna().getId().equals(idPengguna))
                .orElseThrow(() -> new EntityNotFoundException("Transaksi tidak ditemukan atau bukan milik pengguna"));

        // 2. Get new category and validate
        UUID idKategoriBaru = UUID.fromString(request.getIdKategori());
        Kategori kategoriBaru = kategoriRepo.findById(idKategoriBaru)
                .filter(k -> k.getPengguna() == null || k.getPengguna().getId().equals(idPengguna))
                .orElseThrow(() -> new EntityNotFoundException("Kategori tidak valid atau bukan milik pengguna"));

        if (kategoriBaru.getJenis() != 1 && kategoriBaru.getJenis() != 2) {
            throw new IllegalArgumentException("Jenis kategori tidak valid. Hanya boleh 1 (pengeluaran) atau 2 (pemasukan)");
        }

        // 3. Get new account and validate
        UUID idAkunBaru = UUID.fromString(request.getIdAkun());
        Akun akunBaru = akunRepo.findById(idAkunBaru)
                .filter(a -> a.getPengguna().getId().equals(idPengguna))
                .orElseThrow(() -> new EntityNotFoundException("Akun tidak valid atau bukan milik pengguna"));

        // 4. Parse date
        LocalDateTime tanggal;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            tanggal = LocalDateTime.parse(request.getTanggal(), formatter);
        } catch (Exception e) {
            throw new IllegalArgumentException("Format tanggal tidak valid. Gunakan dd/MM/yyyy HH:mm");
        }

        // 5. Handle account balance changes
        Akun akunLama = transaksiLama.getAkun();
        BigDecimal jumlahLama = transaksiLama.getJumlah();
        BigDecimal jumlahBaru = request.getJumlah();

        // Rollback old transaction effect
        if (transaksiLama.getKategori().getJenis() == 1) {
            // Old transaction was expense - add back the amount
            akunLama.setSaldo(akunLama.getSaldo().add(jumlahLama));
        } else {
            // Old transaction was income - subtract the amount
            akunLama.setSaldo(akunLama.getSaldo().subtract(jumlahLama));
        }

        // Apply new transaction effect
        if (kategoriBaru.getJenis() == 1) {
            // New transaction is expense
            if (akunBaru.getSaldo().compareTo(jumlahBaru) < 0) {
                throw new IllegalArgumentException("Saldo tidak mencukupi untuk pengeluaran");
            }
            akunBaru.setSaldo(akunBaru.getSaldo().subtract(jumlahBaru));
        } else {
            // New transaction is income
            akunBaru.setSaldo(akunBaru.getSaldo().add(jumlahBaru));
        }

        // 6. Save account changes
        akunRepo.save(akunLama);
        if (!akunLama.getId().equals(akunBaru.getId())) {
            akunRepo.save(akunBaru);
        }

        // 7. Update transaction
        transaksiLama.setAkun(akunBaru);
        transaksiLama.setKategori(kategoriBaru);
        transaksiLama.setJumlah(jumlahBaru);
        transaksiLama.setTanggal(tanggal);
        transaksiLama.setCatatan(request.getCatatan());

        transaksiRepo.save(transaksiLama);
    }

    @Override
    public void hapusTransaksi(UUID idPengguna, UUID idTransaksi) {
        // Ambil transaksi
        Transaksi transaksi = transaksiRepo.findById(idTransaksi)
                .filter(t -> t.getAkun().getPengguna().getId().equals(idPengguna))
                .orElseThrow(() -> new EntityNotFoundException("Transaksi tidak ditemukan atau bukan milik pengguna"));

        Akun akun = transaksi.getAkun();
        if (akun == null) {
            throw new EntityNotFoundException("Akun tidak ditemukan");
        }

        Kategori kategori = transaksi.getKategori();
        if (kategori == null) {
            throw new EntityNotFoundException("Kategori tidak ditemukan");
        }

        Integer jenis = kategori.getJenis();
        if (jenis == null) {
            throw new IllegalArgumentException("Jenis kategori tidak boleh null");
        }

        // Validasi jumlah dan saldo
        if (transaksi.getJumlah() == null || akun.getSaldo() == null) {
            throw new IllegalStateException("Jumlah transaksi atau saldo akun tidak boleh null");
        }

        // Logika berdasarkan jenis kategori
        if (jenis == 1) { // Pengeluaran
            akun.setSaldo(akun.getSaldo().add(transaksi.getJumlah())); // rollback saldo
        } else if (jenis == 2) { // Pemasukan
            if (akun.getSaldo().compareTo(transaksi.getJumlah()) < 0) {
                throw new IllegalArgumentException("Saldo tidak mencukupi untuk menghapus transaksi pemasukan ini");
            }
            akun.setSaldo(akun.getSaldo().subtract(transaksi.getJumlah()));
        } else {
            throw new IllegalArgumentException("Jenis kategori tidak valid (harus 1 atau 2)");
        }

        akunRepo.save(akun);             // Simpan update saldo
        transaksiRepo.delete(transaksi); // Hapus transaksi
    }

    @Override
    public Page<TransaksiResponse> getFilteredTransaksi(
            UUID idPengguna,
            String keyword,
            LocalDate startDate,
            LocalDate endDate,
            Integer jenis,
            UUID idAkun,
            int page,
            int size
    ) {
        LocalDateTime start = startDate != null
                ? startDate.atStartOfDay()
                : LocalDate.of(2000, 1, 1).atStartOfDay();

        LocalDateTime end = endDate != null
                ? endDate.atTime(23, 59, 59)
                : LocalDate.now().atTime(23, 59, 59);

        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.max(size, 1),
                Sort.by("tanggal").descending()
        );

        String formattedKeyword = (keyword != null && !keyword.isBlank()) ? keyword.toLowerCase() : null;

        Page<Transaksi> transaksiPage = transaksiRepo.findFilteredWithSearch(
                idPengguna,
                start,
                end,
                jenis,
                idAkun,
                formattedKeyword,
                pageable
        );

        return transaksiPage.map(t -> new TransaksiResponse(
                t.getId(),
                t.getAkun().getId(),
                t.getKategori() != null ? t.getKategori().getNama() : "Tanpa Kategori",
                t.getAkun().getNama(),
                t.getKategori() != null ? t.getKategori().getJenis() : 0,
                t.getJumlah(),
                t.getCatatan(),
                t.getTanggal(),
                t.getKategori()
        ));
    }

}
