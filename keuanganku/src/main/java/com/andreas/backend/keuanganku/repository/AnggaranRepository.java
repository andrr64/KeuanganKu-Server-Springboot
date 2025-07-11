package com.andreas.backend.keuanganku.repository;

import com.andreas.backend.keuanganku.model.Anggaran;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnggaranRepository extends JpaRepository<Anggaran, UUID> {
    List<Anggaran> findByPengguna_Id(UUID idPengguna);
    List<Anggaran> findByKategori_Id(UUID idKategori);
}
