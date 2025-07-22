package com.andreas.backend.keuanganku.service.impl;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.andreas.backend.keuanganku.dto.request.UbahPasswordRequest;
import com.andreas.backend.keuanganku.dto.request.UpdateAkunRequest;
import com.andreas.backend.keuanganku.dto.request.UpdatePenggunaRequest;
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
    public void updateNamaAtauEmail(UUID id, UpdatePenggunaRequest request) {
        if ((request.getNama() == null || request.getNama().isBlank())
                && (request.getEmail() == null || request.getEmail().isBlank())) {
            throw new IllegalArgumentException("Minimal nama atau email harus diisi");
        }

        Pengguna pengguna = getById(id);

        if (request.getEmail() != null && !request.getEmail().equalsIgnoreCase(pengguna.getEmail())) {
            boolean emailSudahDipakai = penggunaRepo.existsByEmailIgnoreCase(request.getEmail());
            if (emailSudahDipakai) {
                throw new IllegalArgumentException("Email sudah digunakan");
            }
            pengguna.setEmail(request.getEmail());
        }

        if (request.getNama() != null) {
            pengguna.setNama(request.getNama());
        }

        penggunaRepo.save(pengguna);
    }

    @Override
    public void ubahPassword(UUID id, UbahPasswordRequest request) {
        Pengguna pengguna = getById(id);

        if (!passwordEncoder.matches(request.getPasswordLama(), pengguna.getPassword())) {
            throw new IllegalArgumentException("Password lama salah");
        }

        if (passwordEncoder.matches(request.getPasswordBaru(), pengguna.getPassword())) {
            throw new IllegalArgumentException("Password baru tidak boleh sama dengan password lama");
        }

        if (!isPasswordLengthOk(request.getPasswordBaru())) {
            throw new IllegalArgumentException("Password baru minimal 10 karakter");
        }

        pengguna.setPassword(passwordEncoder.encode(request.getPasswordBaru()));
        penggunaRepo.save(pengguna);
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
    public void updateAkun(UUID idPengguna, UpdateAkunRequest request) {
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
