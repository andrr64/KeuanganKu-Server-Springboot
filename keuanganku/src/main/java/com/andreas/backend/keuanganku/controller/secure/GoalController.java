package com.andreas.backend.keuanganku.controller.secure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.andreas.backend.keuanganku.annotation.CurrentUserId;
import com.andreas.backend.keuanganku.dto.request.GoalRequest;
import com.andreas.backend.keuanganku.dto.request.TambahDanaGoalRequest;
import com.andreas.backend.keuanganku.dto.request.UpdateGoalRequest;
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
    public ResponseEntity<GeneralResponse<GoalResponse>> tambahGoal(@CurrentUserId UUID userId,
            @RequestBody GoalRequest request) {
        GoalResponse response = goalService.tambahGoal(userId, request);
        return ResponseEntity.ok(new GeneralResponse<>("Berhasil menambahkan goal", response, true));
    }

    @GetMapping
    public ResponseEntity<GeneralResponse<List<GoalResponse>>> getAllGoals(
            @CurrentUserId UUID userId,
            @RequestParam(required = false) Boolean tercapai) {
        List<GoalResponse> data = goalService.getAllGoals(userId, tercapai);
        return ResponseEntity.ok(new GeneralResponse<>("OK", data, true));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse<GoalResponse>> getDetail(@CurrentUserId UUID userId,
            @PathVariable UUID id) {
        GoalResponse response = goalService.getById(userId, id);
        return ResponseEntity.ok(new GeneralResponse<>("OK", response, true));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeneralResponse<Object>> updateGoal(@CurrentUserId UUID userId,
            @PathVariable UUID id,
            @RequestBody UpdateGoalRequest request) {
        goalService.updateGoal(userId, id, request);
        return ResponseEntity.ok(new GeneralResponse<>("Goal berhasil diupdate", null, true));
    }

    @PutMapping("/{id}/tambah-dana")
    public ResponseEntity<GeneralResponse<Object>> tambahDana(@CurrentUserId UUID userId,
            @PathVariable UUID id,
            @RequestBody TambahDanaGoalRequest request) {
        goalService.tambahDana(userId, id, request);
        return ResponseEntity.ok(new GeneralResponse<>("Dana berhasil ditambahkan", null, true));
    }

    @PutMapping("/{id}/set-status")
    public ResponseEntity<GeneralResponse<Object>> updateStatusTercapai(
            @CurrentUserId UUID userId,
            @PathVariable UUID id,
            @RequestBody Map<String, Boolean> body) {

        Boolean tercapai = body.get("tercapai");

        if (tercapai == null) {
            return ResponseEntity.badRequest()
                    .body(new GeneralResponse<>("Field 'tercapai' wajib diisi", null, false));
        }

        goalService.updateStatusTercapai(userId, id, tercapai);
        return ResponseEntity.ok(new GeneralResponse<>("Status goal diperbarui", null, true));
    }

    @PutMapping("/{id}/tambah-uang")
    public ResponseEntity<GeneralResponse<Object>> tambahUangKeGoal(
            @CurrentUserId UUID userId,
            @PathVariable UUID id,
            @RequestBody Map<String, Number> body) {

        Number uang = body.get("uang");

        if (uang == null || uang.doubleValue() <= 0) {
            return ResponseEntity.badRequest()
                    .body(new GeneralResponse<>("Field 'uang' wajib diisi dan harus lebih dari 0", null, false));
        }

        goalService.tambahUangKeGoal(userId, id, uang.doubleValue());

        return ResponseEntity.ok(new GeneralResponse<>("Uang berhasil ditambahkan ke goal", null, true));
    }

    @PutMapping("/{id}/kurangi-uang")
    public ResponseEntity<GeneralResponse<Object>> kurangiUangTerkumpul(
            @CurrentUserId UUID userId,
            @PathVariable UUID id,
            @RequestBody Map<String, Number> body) {

        Number uang = body.get("uang");

        if (uang == null || uang.doubleValue() <= 0) {
            return ResponseEntity.badRequest()
                    .body(new GeneralResponse<>("Field 'uang' wajib diisi dan harus lebih dari 0", null, false));
        }

        try {
            goalService.kurangiUangDariGoal(userId, id, uang.doubleValue());
            return ResponseEntity.ok(new GeneralResponse<>("Uang berhasil dikurangi dari goal", null, true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new GeneralResponse<>(e.getMessage(), null, false));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new GeneralResponse<>("Terjadi kesalahan server", null, false));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse<Object>> delete(@CurrentUserId UUID userId,
            @PathVariable UUID id) {
        goalService.hapusGoal(userId, id);
        return ResponseEntity.ok(new GeneralResponse<>("Goal berhasil dihapus", null, true));
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filterGoals(
            @CurrentUserId UUID userId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "tercapai", required = false) Boolean tercapai
    ) {
        Page<GoalResponse> result = goalService.filterGoals(userId, page, size, keyword, tercapai);

        Map<String, Object> response = new HashMap<>();
        response.put("content", result.getContent());
        response.put("currentPage", result.getNumber());
        response.put("totalItems", result.getTotalElements());
        response.put("totalPages", result.getTotalPages());

        return ResponseEntity.ok(new GeneralResponse<>("OK", response, true));
    }
}
