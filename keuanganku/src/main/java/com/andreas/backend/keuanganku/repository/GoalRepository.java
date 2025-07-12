package com.andreas.backend.keuanganku.repository;

import com.andreas.backend.keuanganku.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GoalRepository extends JpaRepository<Goal, UUID> {
    List<Goal> findByPengguna_Id(UUID idPengguna);

}

