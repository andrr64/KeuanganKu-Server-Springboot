package com.andreas.backend.keuanganku.repository;

import com.andreas.backend.keuanganku.model.Pengguna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PenggunaRepository extends JpaRepository<Pengguna, UUID> {
    Optional<Pengguna> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByEmailIgnoreCase(String email);
}
