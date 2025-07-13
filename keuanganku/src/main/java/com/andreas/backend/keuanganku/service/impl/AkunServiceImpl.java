package com.andreas.backend.keuanganku.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.andreas.backend.keuanganku.dto.request.AkunRequest;
import com.andreas.backend.keuanganku.model.Akun;
import com.andreas.backend.keuanganku.model.Pengguna;
import com.andreas.backend.keuanganku.repository.AkunRepository;
import com.andreas.backend.keuanganku.repository.PenggunaRepository;
import com.andreas.backend.keuanganku.service.AkunService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AkunServiceImpl implements AkunService {

    private final AkunRepository akunRepository;
    private final PenggunaRepository penggunaRepository;

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
        return akunRepository.findByPenggunaId(idPengguna);
    }

}
