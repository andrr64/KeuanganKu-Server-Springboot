package com.andreas.backend.keuanganku.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.andreas.backend.keuanganku.model.Goal;

@Repository
public interface GoalRepository extends JpaRepository<Goal, UUID> {

    List<Goal> findByPengguna_Id(UUID penggunaId);
    

    List<Goal> findByPengguna_IdAndTercapai(UUID penggunaId, Boolean tercapai);

    Optional<Goal> findByIdAndPengguna_Id(UUID id, UUID penggunaId);

    boolean existsByPenggunaIdAndNamaIgnoreCase(UUID penggunaId, String nama);

    @Query(value = """
    SELECT * FROM goal
    WHERE id_pengguna = :penggunaId
    AND (:tercapai IS NULL OR tercapai = :tercapai)
    AND (:keyword IS NULL OR LOWER(nama) LIKE LOWER('%' || :keyword || '%'))
    ORDER BY tanggal_target ASC, LOWER(nama) ASC
    """,
            countQuery = """
    SELECT COUNT(*) FROM goal
    WHERE id_pengguna = :penggunaId
    AND (:tercapai IS NULL OR tercapai = :tercapai)
    AND (:keyword IS NULL OR LOWER(nama) LIKE LOWER('%' || :keyword || '%'))
    """,
            nativeQuery = true
    )
    Page<Goal> findFilteredGoals(
            @Param("penggunaId") UUID penggunaId,
            @Param("tercapai") Boolean tercapai,
            @Param("keyword") String keyword,
            Pageable pageable
    );

}
