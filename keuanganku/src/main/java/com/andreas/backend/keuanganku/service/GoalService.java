package com.andreas.backend.keuanganku.service;

import java.util.List;
import java.util.UUID;

import com.andreas.backend.keuanganku.dto.request.GoalRequest;
import com.andreas.backend.keuanganku.dto.request.TambahDanaGoalRequest;
import com.andreas.backend.keuanganku.dto.request.UpdateGoalRequest;
import com.andreas.backend.keuanganku.dto.response.GoalResponse;

public interface GoalService {

    GoalResponse tambahGoal(UUID userId, GoalRequest request);

    List<GoalResponse> getAllGoals(UUID userId, Boolean tercapai);

    GoalResponse getById(UUID userId, UUID goalId);

    void updateGoal(UUID userId, UUID goalId, UpdateGoalRequest request);

    void tambahDana(UUID userId, UUID goalId, TambahDanaGoalRequest request);

    void tandaiTercapai(UUID userId, UUID goalId);

    void hapusGoal(UUID userId, UUID goalId);
}
