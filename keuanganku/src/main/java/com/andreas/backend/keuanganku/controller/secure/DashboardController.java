package com.andreas.backend.keuanganku.controller.secure;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.andreas.backend.keuanganku.annotation.CurrentUserId;
import com.andreas.backend.keuanganku.dto.response.DashboardResponse;
import com.andreas.backend.keuanganku.dto.response.GeneralResponse;
import com.andreas.backend.keuanganku.service.TransaksiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/secure/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final TransaksiService transaksiService;

    @GetMapping
    public ResponseEntity<?> getDashboardData(@CurrentUserId UUID idPengguna) {
        DashboardResponse response = transaksiService.getDashboardData(idPengguna);
        return ResponseEntity.ok(new GeneralResponse<>("OK", response, true));
    }
}
