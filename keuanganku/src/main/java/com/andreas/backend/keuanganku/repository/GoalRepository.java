package com.andreas.backend.keuanganku.repository;

import com.andreas.backend.keuanganku.model.Goal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

@Repository
public interface GoalRepository extends JpaRepository<Goal, UUID> {
    List<Goal> findByPengguna_Id(UUID penggunaId);
    List<Goal> findByPengguna_IdAndTercapai(UUID penggunaId, Boolean tercapai);
    Optional<Goal> findByIdAndPengguna_Id(UUID id, UUID penggunaId);
    boolean existsByPenggunaIdAndNamaIgnoreCase(UUID penggunaId, String nama);
}

