package com.andreas.backend.keuanganku.repository;

import com.andreas.backend.keuanganku.model.Transaksi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransaksiRepository extends JpaRepository<Transaksi, UUID> {
    List<Transaksi> findByPengguna_Id(UUID idPengguna);
    List<Transaksi> findByAkun_Id(UUID idAkun);
    List<Transaksi> findByKategori_Id(UUID idKategori);
}

