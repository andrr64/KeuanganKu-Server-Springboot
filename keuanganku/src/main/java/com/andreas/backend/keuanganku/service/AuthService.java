package com.andreas.backend.keuanganku.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.andreas.backend.keuanganku.model.Pengguna;
import com.andreas.backend.keuanganku.repository.PenggunaRepository;

@Service
public class AuthService {

    private final PenggunaRepository penggunaRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(PenggunaRepository penggunaRepository, PasswordEncoder passwordEncoder) {
        this.penggunaRepository = penggunaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Pengguna login(String email, String rawPassword) {
        Pengguna pengguna = penggunaRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email tidak ditemukan"));

        if (!passwordEncoder.matches(rawPassword, pengguna.getPassword())) {
            throw new RuntimeException("Password salah");
        }

        return pengguna;
    }

    public Pengguna register(String nama, String email, String rawPassword) {
        // Cek kalau email sudah dipakai
        if (penggunaRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email sudah digunakan");
        }

        // Encode password ke bcrypt
        String encodedPassword = passwordEncoder.encode(rawPassword);

        Pengguna pengguna = new Pengguna();
        pengguna.setNama(nama);
        pengguna.setEmail(email);
        pengguna.setPassword(encodedPassword);
        pengguna.setDibuatPada(java.time.LocalDateTime.now());

        return penggunaRepository.save(pengguna);
    }
}
