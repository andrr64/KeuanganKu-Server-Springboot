package com.andreas.backend.keuanganku.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.andreas.backend.keuanganku.model.Kategori;

@Repository
public interface KategoriRepository extends JpaRepository<Kategori, UUID> {

    List<Kategori> findByPenggunaId(UUID idPengguna);
    List<Kategori> findByJenis(Integer jenis); // 1 = pengeluaran, 2 = pemasukan
    List<Kategori> findByPenggunaIdAndJenis(UUID idPengguna, Integer jenis);
    List<Kategori> findByPenggunaIsNullAndJenis(Integer jenis);
    List<Kategori> findByPenggunaIsNull();
    boolean existsByPenggunaIdAndNamaIgnoreCaseAndJenis(UUID idPengguna, String nama, Integer jenis);
    boolean existsByNama(String nama);

}
