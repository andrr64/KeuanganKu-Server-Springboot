package com.andreas.backend.keuanganku.repository;

import com.andreas.backend.keuanganku.model.Transaksi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransaksiRepository extends JpaRepository<Transaksi, UUID> {

    // Basic finders
    List<Transaksi> findByPengguna_Id(UUID idPengguna);

    Page<Transaksi> findByPengguna_Id(UUID idPengguna, Pageable pageable);

    List<Transaksi> findByAkun_Id(UUID idAkun);

    @Deprecated
    List<Transaksi> findByKategori_Id(UUID idKategori);

    // JPQL with OffsetDateTime
    @Query("SELECT t FROM Transaksi t WHERE t.kategori.pengguna.id = :idPengguna")
    List<Transaksi> findByPengguna(@Param("idPengguna") UUID idPengguna);

    @Query("SELECT t FROM Transaksi t WHERE t.kategori.pengguna.id = :idPengguna AND t.kategori.jenis = :jenis")
    List<Transaksi> findByPenggunaAndJenis(@Param("idPengguna") UUID idPengguna, @Param("jenis") int jenis);

    @Query("SELECT t FROM Transaksi t WHERE t.kategori.pengguna.id = :idPengguna AND t.akun.id = :idAkun")
    List<Transaksi> findByPenggunaAndAkun(@Param("idPengguna") UUID idPengguna, @Param("idAkun") UUID idAkun);

    List<Transaksi> findAllByKategoriId(UUID idKategori);

    // AND t.tanggal >= :startDate AND t.tanggal <= :endDate
    @Query("""
    SELECT t FROM Transaksi t
    WHERE t.pengguna.id = :idPengguna
    AND (t.tanggal >= :startDate AND t.tanggal <= :endDate)
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
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate,
            @Param("jenis") Integer jenis,
            @Param("idAkun") UUID idAkun,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    void deleteByAkunId(UUID akunId);

    /**
     * Native query: Total saldo pengguna
     */
    @Query(value = """
        SELECT COALESCE(SUM(a.saldo), 0)
        FROM akun a
        WHERE a.id_pengguna = :idPengguna
        """, nativeQuery = true)
    BigDecimal getTotalSaldo(@Param("idPengguna") UUID idPengguna);

    /**
     * Total pemasukan bulan ini (native query)
     */
    @Query(value = """
        SELECT COALESCE(SUM(t.jumlah), 0)
        FROM transaksi t
        JOIN kategori k ON k.id = t.id_kategori
        WHERE t.id_pengguna = :idPengguna
        AND k.jenis = 2
        AND EXTRACT(MONTH FROM t.tanggal) = EXTRACT(MONTH FROM CURRENT_DATE AT TIME ZONE 'UTC+7')
        AND EXTRACT(YEAR FROM t.tanggal) = EXTRACT(YEAR FROM CURRENT_DATE AT TIME ZONE 'UTC+7')
        """, nativeQuery = true)
    BigDecimal getTotalPemasukanBulanIni(@Param("idPengguna") UUID idPengguna);

    /**
     * Total pengeluaran bulan ini (native query)
     */
    @Query(value = """
        SELECT COALESCE(SUM(t.jumlah), 0)
        FROM transaksi t
        JOIN kategori k ON k.id = t.id_kategori
        WHERE t.id_pengguna = :idPengguna
        AND k.jenis = 1
        AND EXTRACT(MONTH FROM t.tanggal) = EXTRACT(MONTH FROM CURRENT_DATE AT TIME ZONE 'UTC+7')
        AND EXTRACT(YEAR FROM t.tanggal) = EXTRACT(YEAR FROM CURRENT_DATE AT TIME ZONE 'UTC+7')
        """, nativeQuery = true)
    BigDecimal getTotalPengeluaranBulanIni(@Param("idPengguna") UUID idPengguna);

    /**
     * Get total by tanggal (dengan OffsetDateTime)
     */
    @Query("""
        SELECT COALESCE(SUM(t.jumlah), 0) FROM Transaksi t
        WHERE t.pengguna.id = :idPengguna
        AND t.kategori.jenis = :jenis
        AND t.tanggal >= :startDate AND t.tanggal <= :endDate
        """)
    BigDecimal getTotalByTanggal(
            @Param("idPengguna") UUID idPengguna,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate,
            @Param("jenis") int jenis
    );

    /**
     * Get total by bulan (dengan OffsetDateTime)
     */
    @Query("""
        SELECT COALESCE(SUM(t.jumlah), 0) FROM Transaksi t
        WHERE t.pengguna.id = :idPengguna
        AND t.kategori.jenis = :jenis
        AND EXTRACT(MONTH FROM t.tanggal) = :bulan
        AND EXTRACT(YEAR FROM t.tanggal) = EXTRACT(YEAR FROM :now)
        """)
    BigDecimal getTotalByBulan(
            @Param("idPengguna") UUID idPengguna,
            @Param("bulan") int bulan,
            @Param("jenis") int jenis,
            @Param("now") OffsetDateTime now
    );

    /**
     * Ringkasan kategori: pengeluaran & pemasukan dalam rentang waktu
     */
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
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate
    );

    /**
     * Total pengeluaran per kategori dalam rentang waktu
     */
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
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate
    );
}
