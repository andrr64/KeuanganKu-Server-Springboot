package com.andreas.backend.keuanganku.repository;

import com.andreas.backend.keuanganku.model.Transaksi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransaksiRepository extends JpaRepository<Transaksi, UUID> {

    List<Transaksi> findByPengguna_Id(UUID idPengguna);

    List<Transaksi> findByAkun_Id(UUID idAkun);

    List<Transaksi> findByKategori_Id(UUID idKategori);

    @Query("SELECT t FROM Transaksi t WHERE t.kategori.pengguna.id = :idPengguna")
    List<Transaksi> findByPengguna(@Param("idPengguna") UUID idPengguna);

    @Query("SELECT t FROM Transaksi t WHERE t.kategori.pengguna.id = :idPengguna AND t.kategori.jenis = :jenis")
    List<Transaksi> findByPenggunaAndJenis(@Param("idPengguna") UUID idPengguna,
            @Param("jenis") int jenis);

    @Query("SELECT t FROM Transaksi t WHERE t.kategori.pengguna.id = :idPengguna AND t.akun.id = :idAkun")
    List<Transaksi> findByPenggunaAndAkun(@Param("idPengguna") UUID idPengguna,
            @Param("idAkun") UUID idAkun);

}
