// service/impl/GoalServiceImpl.java
package com.andreas.backend.keuanganku.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.andreas.backend.keuanganku.config.TimeConfig;
import com.andreas.backend.keuanganku.dto.request.goal.GoalRequest;
import com.andreas.backend.keuanganku.dto.request.goal.UpdateGoalRequest;
import com.andreas.backend.keuanganku.dto.response.GoalResponse;
import com.andreas.backend.keuanganku.model.Goal;
import com.andreas.backend.keuanganku.model.Pengguna;
import com.andreas.backend.keuanganku.repository.GoalRepository;
import com.andreas.backend.keuanganku.repository.PenggunaRepository;
import com.andreas.backend.keuanganku.service.GoalService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepo;
    private final PenggunaRepository penggunaRepo;

    @Override
    public GoalResponse tambahGoal(UUID userId, GoalRequest request) {
        if (request.getNama() == null || request.getNama().isBlank()) {
            throw new IllegalArgumentException("Nama tidak boleh kosong");
        }

        boolean namaGoalSudahAda = goalRepo.existsByPenggunaIdAndNamaIgnoreCase(userId, request.getNama());
        if (namaGoalSudahAda) {
            throw new IllegalArgumentException("Nama goal sudah digunakan");
        }

        Pengguna pengguna = penggunaRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Pengguna tidak ditemukan"));

        OffsetDateTime tanggalTarget = null;
        if (request.getTanggalTarget() != null) {
            LocalDateTime localDateTime = request.getTanggalTarget().atTime(23, 59, 59);
            tanggalTarget = localDateTime.atOffset(TimeConfig.SERVER_TIME_ZONE_OFFSET);
        }

        Goal goal = new Goal();
        goal.setPengguna(pengguna);
        goal.setNama(request.getNama());
        goal.setTarget(request.getTarget()); // juga bisa null
        goal.setTerkumpul(BigDecimal.ZERO);
        goal.setTanggalTarget(tanggalTarget);
        goal.setTercapai(false);

        goalRepo.save(goal);
        return toResponse(goal);
    }

    @Override
    public void updateGoal(UUID userId, UUID goalId, UpdateGoalRequest request) {
        Goal goal = goalRepo.findByIdAndPengguna_Id(goalId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Goal tidak ditemukan"));

        if (request.getNama() == null && request.getTarget() == null && request.getTanggalTarget() == null) {
            throw new IllegalArgumentException("Minimal satu field harus diisi");
        }

        if (request.getNama() != null) {
            if (!request.getNama().equalsIgnoreCase(goal.getNama())) {
                boolean namaSudahAda = goalRepo.existsByPenggunaIdAndNamaIgnoreCase(userId, request.getNama());
                if (namaSudahAda) {
                    throw new IllegalArgumentException("Nama goal sudah digunakan");
                }
            }
            goal.setNama(request.getNama());
        }

        if (request.getTarget() != null && request.getTarget().compareTo(BigDecimal.ZERO) > 0) {
            if (goal.getTerkumpul().compareTo(request.getTarget()) > 0) {
                throw new IllegalArgumentException("Target baru lebih kecil dari jumlah yang sudah terkumpul");
            }
            goal.setTarget(request.getTarget());
        }

        // Support untuk mengubah tanggal target atau menghapusnya (set null)
        if (request.getTanggalTarget() != null) {
            LocalDateTime localDateTime = request.getTanggalTarget().atTime(23, 59, 59);
            OffsetDateTime tanggalTarget = localDateTime.atOffset(TimeConfig.SERVER_TIME_ZONE_OFFSET);
            goal.setTanggalTarget(tanggalTarget);
        } else { // Asumsi ada method untuk cek explicit null
            goal.setTanggalTarget(null);
        }

        goalRepo.save(goal);
    }

    @Override
    public void updateStatusTercapai(UUID userId, UUID id, boolean status) {
        Goal goal = goalRepo.findByIdAndPengguna_Id(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Goal tidak ditemukan"));

        if (status) {
            goal.setTercapai(true);
            goal.setTerkumpul(goal.getTarget());
        } else {
            goal.setTercapai(false);
            goal.setTerkumpul(BigDecimal.ZERO);
        }

        goalRepo.save(goal);
    }

    @Override
    public void hapusGoal(UUID userId, UUID goalId) {
        Goal goal = goalRepo.findByIdAndPengguna_Id(goalId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Goal tidak ditemukan"));
        goalRepo.delete(goal);
    }

    @Override
    public Page<GoalResponse> filterGoals(UUID userId, int page, int size, String keyword, Boolean tercapai) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Goal> result = goalRepo.findFilteredGoals(userId, tercapai, keyword, pageable);
        return result.map(this::toResponse);
    }

    @Override
    public void tambahUangKeGoal(UUID userId, UUID goalId, double jumlahUang) {
        Goal goal = goalRepo.findByIdAndPengguna_Id(goalId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Goal tidak ditemukan"));

        if (goal.getTercapai() != null && goal.getTercapai()) {
            throw new IllegalStateException("Goal sudah tercapai, tidak bisa menambah uang lagi.");
        }

        BigDecimal tambahan = BigDecimal.valueOf(jumlahUang);
        BigDecimal totalBaru = goal.getTerkumpul().add(tambahan);

        if (totalBaru.compareTo(goal.getTarget()) >= 0) {
            goal.setTerkumpul(goal.getTarget());
            goal.setTercapai(true);
        } else {
            goal.setTerkumpul(totalBaru);
        }

        goalRepo.save(goal);
    }

    @Override
    public void kurangiUangDariGoal(UUID userId, UUID goalId, double jumlahUang) {
        Goal goal = goalRepo.findByIdAndPengguna_Id(goalId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Goal tidak ditemukan"));

        BigDecimal jumlahKurang = BigDecimal.valueOf(jumlahUang);
        BigDecimal totalBaru = goal.getTerkumpul().subtract(jumlahKurang);

        if (totalBaru.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Jumlah yang dikurangi tidak boleh membuat total terkumpul menjadi negatif");
        }

        goal.setTerkumpul(totalBaru);

        if (totalBaru.compareTo(goal.getTarget()) != 0) {
            goal.setTercapai(false);
        }

        goalRepo.save(goal);
    }

    private GoalResponse toResponse(Goal goal) {
        GoalResponse res = new GoalResponse();
        res.setId(goal.getId());
        res.setNama(goal.getNama());
        res.setTarget(goal.getTarget());
        res.setTerkumpul(goal.getTerkumpul());
        res.setTanggalTarget(goal.getTanggalTarget());
        res.setTercapai(goal.getTercapai() != null && goal.getTercapai());
        return res;
    }

}
