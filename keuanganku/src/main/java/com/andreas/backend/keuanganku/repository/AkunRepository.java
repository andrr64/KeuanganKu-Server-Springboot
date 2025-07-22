package com.andreas.backend.keuanganku.repository;

import com.andreas.backend.keuanganku.model.Akun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Repository
public interface AkunRepository extends JpaRepository<Akun, UUID> {

    List<Akun> findByPengguna_Id(UUID idPengguna);
    List<Akun> findByPenggunaId(UUID idPengguna);
    Optional<Akun> findByNamaAndPenggunaId(String nama, UUID penggunaId);
    boolean existsByNamaIgnoreCaseAndPenggunaId(String nama, UUID penggunaId);
    boolean existsByNamaAndPenggunaId(String nama, UUID penggunaId);
    Optional<Akun> findByIdAndPengguna_Id(UUID idAkun, UUID idPengguna);
    List<Akun> findByPenggunaIdAndAktifTrue(UUID idPengguna);
}
