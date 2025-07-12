package com.andreas.backend.keuanganku.repository;

import com.andreas.backend.keuanganku.model.Kategori;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface KategoriRepository extends JpaRepository<Kategori, UUID> {
    List<Kategori> findByPengguna_Id(UUID idPengguna);
    List<Kategori> findByJenis(Integer jenis); // 1: pengeluaran, 2: pemasukan
}

