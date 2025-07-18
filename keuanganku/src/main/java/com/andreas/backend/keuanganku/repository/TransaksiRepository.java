package com.andreas.backend.keuanganku.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.andreas.backend.keuanganku.model.Transaksi;

@Repository
public interface TransaksiRepository extends JpaRepository<Transaksi, UUID> {

    List<Transaksi> findByPengguna_Id(UUID idPengguna);

    List<Transaksi> findByAkun_Id(UUID idAkun);

    @Deprecated
    List<Transaksi> findByKategori_Id(UUID idKategori);

    @Query("SELECT t FROM Transaksi t WHERE t.kategori.pengguna.id = :idPengguna")
    List<Transaksi> findByPengguna(@Param("idPengguna") UUID idPengguna);

    @Query("SELECT t FROM Transaksi t WHERE t.kategori.pengguna.id = :idPengguna AND t.kategori.jenis = :jenis")
    List<Transaksi> findByPenggunaAndJenis(@Param("idPengguna") UUID idPengguna, @Param("jenis") int jenis);

    @Query("SELECT t FROM Transaksi t WHERE t.kategori.pengguna.id = :idPengguna AND t.akun.id = :idAkun")
    List<Transaksi> findByPenggunaAndAkun(@Param("idPengguna") UUID idPengguna, @Param("idAkun") UUID idAkun);

    List<Transaksi> findAllByKategoriId(UUID idKategori);

    @Query("""
    SELECT t FROM Transaksi t
    WHERE t.pengguna.id = :idPengguna
    AND t.tanggal >= :startDate AND t.tanggal <= :endDate
    AND (:jenis IS NULL OR t.kategori.jenis = :jenis)
    AND (:idAkun IS NULL OR t.akun.id = :idAkun)
    AND (
        :keyword IS NULL OR
        LOWER(t.catatan) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
        LOWER(t.kategori.nama) LIKE LOWER(CONCAT('%', :keyword, '%'))
    )
    """)

    Page<Transaksi> findFilteredWithSearch(
            @Param("idPengguna") UUID idPengguna,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("jenis") Integer jenis,
            @Param("idAkun") UUID idAkun,
            @Param("keyword") String keyword,
            Pageable pageable
    );

}
