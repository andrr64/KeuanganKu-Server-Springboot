package com.andreas.backend.keuanganku.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    boolean existsByIdAndPenggunaIsNull(UUID id);

    @Query(value = """
    SELECT * FROM kategori
    WHERE (id_pengguna = :idPengguna OR id_pengguna IS NULL)
    AND (:jenis IS NULL OR jenis = :jenis)
    AND (:keyword IS NULL OR LOWER(nama) LIKE LOWER(CONCAT('%', :keyword, '%')))
    """,
            countQuery = """
    SELECT COUNT(*) FROM kategori
    WHERE (id_pengguna = :idPengguna OR id_pengguna IS NULL)
    AND (:jenis IS NULL OR jenis = :jenis)
    AND (:keyword IS NULL OR LOWER(nama) LIKE LOWER(CONCAT('%', :keyword, '%')))
    """,
            nativeQuery = true
    )
    Page<Kategori> findFilteredKategori(
            @Param("idPengguna") UUID idPengguna,
            @Param("jenis") Integer jenis,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
