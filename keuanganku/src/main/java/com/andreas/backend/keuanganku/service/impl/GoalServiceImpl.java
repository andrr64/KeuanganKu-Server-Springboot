package com.andreas.backend.keuanganku.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.andreas.backend.keuanganku.dto.request.GoalRequest;
import com.andreas.backend.keuanganku.dto.request.TambahDanaGoalRequest;
import com.andreas.backend.keuanganku.dto.request.UpdateGoalRequest;
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
        if (request.getTarget() == null || request.getTarget().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Target harus lebih dari 0");
        }

        // Cek jika nama goal sudah ada (ignore case)
        boolean namaGoalSudahAda = goalRepo.existsByPenggunaIdAndNamaIgnoreCase(userId, request.getNama());
        if (namaGoalSudahAda) {
            throw new IllegalArgumentException("Nama goal sudah digunakan");
        }

        // Parsing tanggal dengan format dd/MM/yyyy
        LocalDate tanggalTarget;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            tanggalTarget = LocalDate.parse(request.getTanggalTarget(), formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Tanggal target harus dalam format dd/MM/yyyy");
        }

        Pengguna pengguna = penggunaRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Pengguna tidak ditemukan"));

        Goal goal = new Goal();
        goal.setPengguna(pengguna);
        goal.setNama(request.getNama());
        goal.setTarget(request.getTarget());
        goal.setTerkumpul(BigDecimal.ZERO);
        goal.setTanggalTarget(tanggalTarget);
        goal.setTercapai(false);

        goalRepo.save(goal);
        return toResponse(goal);
    }

    @Override
    public List<GoalResponse> getAllGoals(UUID userId, Boolean tercapai) {
        List<Goal> goals;
        if (tercapai != null) {
            goals = goalRepo.findByPengguna_IdAndTercapai(userId, tercapai);
        } else {
            goals = goalRepo.findByPengguna_Id(userId);
        }
        return goals.stream().map(this::toResponse).toList();
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
    public GoalResponse getById(UUID userId, UUID goalId) {
        Goal goal = goalRepo.findByIdAndPengguna_Id(goalId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Goal tidak ditemukan"));
        return toResponse(goal);
    }

    @Override
    public void updateGoal(UUID userId, UUID goalId, UpdateGoalRequest request) {
        Goal goal = goalRepo.findByIdAndPengguna_Id(goalId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Goal tidak ditemukan"));

        // Validasi: minimal satu field harus dikirim
        if (request.getNama() == null && request.getTarget() == null && request.getTanggalTarget() == null) {
            throw new IllegalArgumentException("Minimal satu field harus diisi");
        }

        // Validasi & set nama baru
        if (request.getNama() != null) {
            if (!request.getNama().equalsIgnoreCase(goal.getNama())) {
                boolean namaSudahAda = goalRepo.existsByPenggunaIdAndNamaIgnoreCase(userId, request.getNama());
                if (namaSudahAda) {
                    throw new IllegalArgumentException("Nama goal sudah digunakan");
                }
            }
            goal.setNama(request.getNama());
        }

        // Validasi & set target baru
        if (request.getTarget() != null && request.getTarget().compareTo(BigDecimal.ZERO) > 0) {
            if (goal.getTerkumpul().compareTo(request.getTarget()) > 0) {
                throw new IllegalArgumentException("Target baru lebih kecil dari jumlah yang sudah terkumpul");
            }
            goal.setTarget(request.getTarget());
        }

        // Validasi & set tanggal target baru (format dd/MM/yyyy)
        if (request.getTanggalTarget() != null) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                goal.setTanggalTarget(LocalDate.parse(request.getTanggalTarget(), formatter));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Tanggal harus dalam format dd/MM/yyyy");
            }
        }
        goalRepo.save(goal);
    }

    @Override
    public void tambahDana(UUID userId, UUID goalId, TambahDanaGoalRequest request) {
        if (request.getJumlah() == null || request.getJumlah().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Jumlah harus positif");
        }

        Goal goal = goalRepo.findByIdAndPengguna_Id(goalId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Goal tidak ditemukan"));

        BigDecimal total = goal.getTerkumpul().add(request.getJumlah());
        if (total.compareTo(goal.getTarget()) > 0) {
            throw new IllegalArgumentException("Jumlah melebihi target");
        }

        goal.setTerkumpul(total);
        if (total.compareTo(goal.getTarget()) == 0) {
            goal.setTercapai(true);
        }

        goalRepo.save(goal);
    }

    @Override
    public void tandaiTercapai(UUID userId, UUID goalId) {
        Goal goal = goalRepo.findByIdAndPengguna_Id(goalId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Goal tidak ditemukan"));

        goal.setTercapai(true);
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

        // Jika setelah dikurangi, uang tidak sama dengan target, maka goal tidak tercapai
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
