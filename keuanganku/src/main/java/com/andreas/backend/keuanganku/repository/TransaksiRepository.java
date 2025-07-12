package com.andreas.backend.keuanganku.repository;

import com.andreas.backend.keuanganku.model.Transaksi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransaksiRepository extends JpaRepository<Transaksi, UUID> {
    List<Transaksi> findByPengguna_Id(UUID idPengguna);
    List<Transaksi> findByAkun_Id(UUID idAkun);
    List<Transaksi> findByKategori_Id(UUID idKategori);

    @Query("""
        SELECT t FROM Transaksi t
        WHERE t.akun.id = :akunId
          AND t.pengguna.id = :penggunaId
        ORDER BY t.tanggal DESC
        """)
    List<Transaksi> findByAkunIdAndPenggunaId(UUID akunId, UUID penggunaId);

    @Query("""
        SELECT t FROM Transaksi t
        WHERE t.akun.id = :akunId
          AND t.pengguna.id = :penggunaId
          AND t.kategori.jenis = :jenis
        ORDER BY t.tanggal DESC
        """)
    List<Transaksi> findByAkunIdAndPenggunaIdAndJenis(UUID akunId, UUID penggunaId, int jenis);
}

