package com.andreas.backend.keuanganku.service;

import com.andreas.backend.keuanganku.dto.request.goal.GoalRequest;
import com.andreas.backend.keuanganku.dto.request.goal.UpdateGoalRequest;
import com.andreas.backend.keuanganku.dto.response.GoalResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface GoalService {
    GoalResponse tambahGoal(UUID userId, GoalRequest request);

    Page<GoalResponse> filterGoals(UUID userId, int page, int size, String keyword, Boolean tercapai);

    void updateGoal(UUID userId, UUID goalId, UpdateGoalRequest request);
    void tambahUangKeGoal(UUID userId, UUID goalId, double uang);
    void kurangiUangDariGoal(UUID userId, UUID goalId, double jumlahUang);
    void updateStatusTercapai(UUID userId, UUID goalId, boolean status);
    void hapusGoal(UUID userId, UUID goalId);
}
