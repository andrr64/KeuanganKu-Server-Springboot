package com.andreas.backend.keuanganku.controller.secure;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.andreas.backend.keuanganku.annotation.CurrentUserId;
import com.andreas.backend.keuanganku.dto.request.goal.GoalRequest;
import com.andreas.backend.keuanganku.dto.request.goal.PatchGoalRequest;
import com.andreas.backend.keuanganku.dto.request.goal.UpdateGoalRequest;
import com.andreas.backend.keuanganku.dto.response.GeneralResponse;
import com.andreas.backend.keuanganku.dto.response.GoalResponse;
import com.andreas.backend.keuanganku.service.GoalService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/secure/goal")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    public ResponseEntity<?> createGoal(
            @CurrentUserId UUID userId,
            @RequestBody GoalRequest request) {
        GoalResponse response = goalService.tambahGoal(userId, request);
        return ResponseEntity.ok(new GeneralResponse<>("Berhasil menambahkan goal", response, true));
    }

    @GetMapping
    public ResponseEntity<?> getGoals(
            @CurrentUserId UUID userId,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "tercapai", required = false) Boolean tercapai,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        Page<GoalResponse> resultPage = goalService.filterGoals(userId, page, size, keyword, tercapai);

        return ResponseEntity.ok(GeneralResponse.fromPage(resultPage, "OK"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateGoal(
            @CurrentUserId UUID userId,
            @PathVariable UUID id,
            @RequestBody UpdateGoalRequest request) {
        goalService.updateGoal(userId, id, request);
        return ResponseEntity.ok(new GeneralResponse<>("Goal berhasil diupdate", null, true));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<GeneralResponse<Object>> patchGoal(
            @CurrentUserId UUID userId,
            @PathVariable UUID id,
            @RequestParam String action,
            @RequestBody PatchGoalRequest body) {
        try {
            switch (action) {
                case "add_funds" -> {
                    Double uangTambah = body.getUang();
                    if (uangTambah == null || uangTambah <= 0) {
                        return ResponseEntity.badRequest().body(new GeneralResponse<>("Field 'uang' wajib diisi dan harus lebih dari 0", null, false));
                    }
                    goalService.tambahUangKeGoal(userId, id, uangTambah);
                    return ResponseEntity.ok(new GeneralResponse<>("Uang berhasil ditambahkan ke goal", null, true));
                }

                case "subtract_funds" -> {
                    Double uangKurang = body.getUang();
                    if (uangKurang == null || uangKurang <= 0) {
                        return ResponseEntity.badRequest().body(new GeneralResponse<>("Field 'uang' wajib diisi dan harus lebih dari 0", null, false));
                    }
                    goalService.kurangiUangDariGoal(userId, id, uangKurang);
                    return ResponseEntity.ok(new GeneralResponse<>("Uang berhasil dikurangi dari goal", null, true));
                }

                case "update_status" -> {
                    Boolean tercapai = body.getTercapai();
                    if (tercapai == null) {
                        return ResponseEntity.badRequest().body(new GeneralResponse<>("Field 'tercapai' wajib diisi", null, false));
                    }
                    goalService.updateStatusTercapai(userId, id, tercapai);
                    return ResponseEntity.ok(new GeneralResponse<>("Status goal diperbarui", null, true));
                }

                default -> {
                    return ResponseEntity.badRequest().body(new GeneralResponse<>("Aksi '" + action + "' tidak valid.", null, false));
                }
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new GeneralResponse<>(e.getMessage(), null, false));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new GeneralResponse<>("Terjadi kesalahan server", null, false));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGoal(
            @CurrentUserId UUID userId,
            @PathVariable UUID id) {
        goalService.hapusGoal(userId, id);
        return ResponseEntity.ok(new GeneralResponse<>("Goal berhasil dihapus", null, true));
    }

}
