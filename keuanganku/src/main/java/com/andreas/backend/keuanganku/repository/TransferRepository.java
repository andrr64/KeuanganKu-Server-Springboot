package com.andreas.backend.keuanganku.repository;

import com.andreas.backend.keuanganku.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, UUID> {

    List<Transfer> findByPengguna_Id(UUID idPengguna);

    List<Transfer> findByDariAkun_Id(UUID dariAkunId);

    List<Transfer> findByKeAkun_Id(UUID keAkunId);

    List<Transfer> findAllByPengguna_Id(UUID idPengguna);
}
