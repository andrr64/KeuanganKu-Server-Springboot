package com.andreas.backend.keuanganku.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.andreas.backend.keuanganku.dto.request.AkunRequest;
import com.andreas.backend.keuanganku.model.Akun;
import com.andreas.backend.keuanganku.model.Pengguna;
import com.andreas.backend.keuanganku.repository.AkunRepository;
import com.andreas.backend.keuanganku.repository.PenggunaRepository;
import com.andreas.backend.keuanganku.repository.TransaksiRepository;
import com.andreas.backend.keuanganku.service.AkunService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AkunServiceImpl implements AkunService {

    private final AkunRepository akunRepository;
    private final PenggunaRepository penggunaRepository;
    private final TransaksiRepository transaksiRepository;

    @Override
    public Akun tambahAkun(UUID idPengguna, AkunRequest request) {
        Pengguna pengguna = penggunaRepository.findById(idPengguna)
                .orElseThrow(() -> new EntityNotFoundException("Pengguna tidak ditemukan"));

        if (akunRepository.existsByNamaAndPenggunaId(request.getNamaAkun(), idPengguna)) {
            throw new IllegalArgumentException("Nama akun sudah digunakan");
        }

        Akun akun = new Akun();
        akun.setNama(request.getNamaAkun());
        akun.setSaldo(request.getSaldoAwal());
        akun.setAktif(true);
        akun.setPengguna(pengguna);
        akun.setDibuatPada(LocalDateTime.now());

        return akunRepository.save(akun);
    }

    @Override
    public void updateNamaAkun(UUID idPengguna, UUID idAkun, String namaBaru) {
        Akun akun = akunRepository.findById(idAkun)
                .orElseThrow(() -> new EntityNotFoundException("Akun tidak ditemukan"));

        if (!akun.getPengguna().getId().equals(idPengguna)) {
            throw new SecurityException("Anda tidak memiliki akses ke akun ini");
        }

        akun.setNama(namaBaru);
        akunRepository.save(akun);
    }

    @Override
    public List<Akun> getSemuaAkun(UUID idPengguna) {
        return akunRepository.findByPenggunaIdAndAktifTrue(idPengguna);
    }
    
    @Override
    public void hapusAkunDanTransaksi(UUID idPengguna, UUID idAkun) {
        Akun akun = akunRepository.findById(idAkun)
                .orElseThrow(() -> new EntityNotFoundException("Akun tidak ditemukan"));

        if (!akun.getPengguna().getId().equals(idPengguna)) {
            throw new SecurityException("Anda tidak memiliki akses ke akun ini");
        }

        // Hapus semua transaksi yang terkait dengan akun
        transaksiRepository.deleteByAkunId(idAkun);

        // Hapus akun itu sendiri
        akunRepository.delete(akun);
    }

}
