package com.andreas.backend.keuanganku.repository;

import com.andreas.backend.keuanganku.model.Akun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AkunRepository extends JpaRepository<Akun, UUID> {
    List<Akun> findByPengguna_Id(UUID idPengguna);
}
