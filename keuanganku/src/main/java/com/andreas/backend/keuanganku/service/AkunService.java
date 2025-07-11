package com.andreas.backend.keuanganku.service;

import com.andreas.backend.keuanganku.model.Akun;
import com.andreas.backend.keuanganku.repository.AkunRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AkunService {
    @Autowired
    private AkunRepository akunRepo;

    public Akun simpan(Akun akun) {
        return akunRepo.save(akun);
    }
}
