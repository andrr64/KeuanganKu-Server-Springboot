package com.andreas.backend.keuanganku.controller.secure;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/{id}/tambah")
    public ResponseEntity<GeneralResponse<Object>> tambahDana(@CurrentUserId UUID userId,
                                                              @PathVariable UUID id,
                                                              @RequestBody TambahDanaGoalRequest request) {
        goalService.tambahDana(userId, id, request);
        return ResponseEntity.ok(new GeneralResponse<>("Dana berhasil ditambahkan", null, true));
    }

    @PutMapping("/{id}/tandai-tercapai")
    public ResponseEntity<GeneralResponse<Object>> tandaiTercapai(@CurrentUserId UUID userId,
                                                                  @PathVariable UUID id) {
        goalService.tandaiTercapai(userId, id);
        return ResponseEntity.ok(new GeneralResponse<>("Goal ditandai tercapai", null, true));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse<Object>> delete(@CurrentUserId UUID userId,
                                                          @PathVariable UUID id) {
        goalService.hapusGoal(userId, id);
        return ResponseEntity.ok(new GeneralResponse<>("Goal berhasil dihapus", null, true));
    }
}
