package com.andreas.backend.keuanganku.service;

import com.andreas.backend.keuanganku.model.Pengguna;
import com.andreas.backend.keuanganku.repository.PenggunaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PenggunaService {

    @Autowired
    private PenggunaRepository penggunaRepo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public Optional<Pengguna> getById(UUID id) {
        return penggunaRepo.findById(id);
    }

    public Pengguna simpan(Pengguna pengguna) {
        return penggunaRepo.save(pengguna);
    }
    public boolean isEmailDipakai(String email) {
        return penggunaRepo.existsByEmail(email);
    }
    // Cari pengguna berdasarkan email
    public Optional<Pengguna> temukanByEmail(String email) {
        return penggunaRepo.findByEmail(email);
    }

    // Cek password cocok atau tidak
    public boolean passwordCocok(String plainPassword, String hashedPassword) {
        return encoder.matches(plainPassword, hashedPassword);
    }

    public void hapus(UUID id) {
        penggunaRepo.deleteById(id);
    }

    // Hash password plaintext
    public String hashPassword(String plainPassword) {
        return encoder.encode(plainPassword);
    }

}
