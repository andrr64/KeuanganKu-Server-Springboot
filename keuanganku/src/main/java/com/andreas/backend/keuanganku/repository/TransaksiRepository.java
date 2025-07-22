package com.andreas.backend.keuanganku.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    Page<Transaksi> findByPengguna_Id(UUID idPengguna, Pageable pageable);

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
            LOWER(CAST(t.catatan AS text)) LIKE LOWER(CONCAT('%', CAST(:keyword AS text), '%')) OR
            LOWER(CAST(t.kategori.nama AS text)) LIKE LOWER(CONCAT('%', CAST(:keyword AS text), '%'))
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

    void deleteByAkunId(UUID akunId);

    @Query(value = """
    SELECT COALESCE(SUM(a.saldo), 0)
    FROM akun a
    WHERE a.id_pengguna = :idPengguna
""", nativeQuery = true)
    BigDecimal getTotalSaldo(@Param("idPengguna") UUID idPengguna);

    @Query(value = """
        SELECT COALESCE(SUM(t.jumlah), 0)
        FROM transaksi t
        JOIN kategori k ON k.id = t.id_kategori
        WHERE t.id_pengguna = :idPengguna
        AND k.jenis = 2
        AND EXTRACT(MONTH FROM t.tanggal) = EXTRACT(MONTH FROM CURRENT_DATE)
        AND EXTRACT(YEAR FROM t.tanggal) = EXTRACT(YEAR FROM CURRENT_DATE)
    """, nativeQuery = true)
    BigDecimal getTotalPemasukanBulanIni(@Param("idPengguna") UUID idPengguna);

    @Query(value = """
        SELECT COALESCE(SUM(t.jumlah), 0)
        FROM transaksi t
        JOIN kategori k ON k.id = t.id_kategori
        WHERE t.id_pengguna = :idPengguna
        AND k.jenis = 1
        AND EXTRACT(MONTH FROM t.tanggal) = EXTRACT(MONTH FROM CURRENT_DATE)
        AND EXTRACT(YEAR FROM t.tanggal) = EXTRACT(YEAR FROM CURRENT_DATE)
    """, nativeQuery = true)
    BigDecimal getTotalPengeluaranBulanIni(@Param("idPengguna") UUID idPengguna);

    @Query("""
    SELECT COALESCE(SUM(t.jumlah), 0) FROM Transaksi t
    WHERE t.pengguna.id = :idPengguna AND t.kategori.jenis = :jenis
    AND DATE(t.tanggal) = :tanggal
""")
    BigDecimal getTotalByTanggal(UUID idPengguna, LocalDate tanggal, int jenis);

    @Query("""
    SELECT COALESCE(SUM(t.jumlah), 0) FROM Transaksi t
    WHERE t.pengguna.id = :idPengguna AND t.kategori.jenis = :jenis
    AND MONTH(t.tanggal) = :bulan AND YEAR(t.tanggal) = YEAR(CURRENT_DATE)
""")
    BigDecimal getTotalByBulan(UUID idPengguna, int bulan, int jenis);

    @Query("""
    SELECT k.nama, SUM(t.jumlah), k.jenis 
    FROM Transaksi t
    JOIN t.kategori k
    WHERE t.pengguna.id = :idPengguna
    AND t.tanggal >= :startDate AND t.tanggal <= :endDate
    GROUP BY k.nama, k.jenis
    ORDER BY SUM(t.jumlah) DESC
""")
    List<Object[]> getRingkasanKategori(
            @Param("idPengguna") UUID idPengguna,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("""
    SELECT t.kategori.nama, SUM(t.jumlah)
    FROM Transaksi t
    WHERE t.kategori.jenis = 1
    AND t.pengguna.id = :idPengguna
    AND t.tanggal >= :startDate AND t.tanggal <= :endDate
    GROUP BY t.kategori.nama
""")
    List<Object[]> getTotalPengeluaranByKategoriBetween(
            @Param("idPengguna") UUID idPengguna,
            @Param("startDate") LocalDateTime startDate, // Changed to LocalDateTime
            @Param("endDate") LocalDateTime endDate // Changed to LocalDateTime
    );

}
