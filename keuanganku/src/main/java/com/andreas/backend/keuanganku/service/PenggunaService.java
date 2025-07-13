package com.andreas.backend.keuanganku.service;

import com.andreas.backend.keuanganku.model.Pengguna;
import com.andreas.backend.keuanganku.repository.PenggunaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PenggunaService {
    @Autowired
    private PenggunaRepository penggunaRepo;

    public Optional<Pengguna> getById(UUID idPengguna){
        return penggunaRepo.findById(idPengguna);
    }
}
