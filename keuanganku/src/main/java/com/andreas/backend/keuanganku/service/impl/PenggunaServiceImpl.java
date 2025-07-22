package com.andreas.backend.keuanganku.service.impl;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.andreas.backend.keuanganku.dto.request.pengguna.UpdatePenggunaRequest;
import com.andreas.backend.keuanganku.model.Pengguna;
import com.andreas.backend.keuanganku.repository.PenggunaRepository;
import com.andreas.backend.keuanganku.service.PenggunaService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PenggunaServiceImpl implements PenggunaService {

    private final PenggunaRepository penggunaRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Pengguna getById(UUID id) {
        return penggunaRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pengguna tidak ditemukan"));
    }

    @Override
    public Pengguna login(String email, String rawPassword) {
        Pengguna pengguna = penggunaRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email atau password salah"));

        if (!passwordEncoder.matches(rawPassword, pengguna.getPassword())) {
            throw new IllegalArgumentException("Email atau password salah");
        }

        return pengguna;
    }

    @Override
    public Pengguna register(String nama, String email, String rawPassword) {
        if (penggunaRepo.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email sudah digunakan");
        }

        if (!isPasswordLengthOk(rawPassword)) {
            throw new IllegalArgumentException("Password minimal 10 karakter");
        }

        String encodedPassword = passwordEncoder.encode(rawPassword);

        Pengguna pengguna = new Pengguna();
        pengguna.setNama(nama);
        pengguna.setEmail(email);
        pengguna.setPassword(encodedPassword);
        pengguna.setDibuatPada(java.time.LocalDateTime.now());

        return penggunaRepo.save(pengguna);
    }

    @Override
    public boolean isPasswordLengthOk(String password) {
        return password != null && password.length() >= 10;
    }
    
    @Override
    public void updateAkun(UUID idPengguna, UpdatePenggunaRequest request) {
        // Validasi input
        if (request.getNama() == null || request.getEmail() == null || request.getPasswordKonfirmasi() == null) {
            throw new IllegalArgumentException("Data tidak lengkap.");
        }

        // Ambil pengguna
        Pengguna pengguna = penggunaRepo.findById(idPengguna)
                .orElseThrow(() -> new NoSuchElementException("Pengguna tidak ditemukan."));

        // Cek password lama
        if (!passwordEncoder.matches(request.getPasswordKonfirmasi(), pengguna.getPassword())) {
            throw new SecurityException("Password lama salah.");
        }

        // Update nama & email
        pengguna.setNama(request.getNama());
        pengguna.setEmail(request.getEmail());

        // Jika password baru ada
        if (request.getPasswordBaru() != null && !request.getPasswordBaru().isBlank()) {
            pengguna.setPassword(passwordEncoder.encode(request.getPasswordBaru()));
        }

        penggunaRepo.save(pengguna);
    }

}
