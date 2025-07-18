package com.andreas.backend.keuanganku.controller.secure;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.andreas.backend.keuanganku.annotation.CurrentUserId;
import com.andreas.backend.keuanganku.dto.request.UbahPasswordRequest;
import com.andreas.backend.keuanganku.dto.request.UpdatePenggunaRequest;
import com.andreas.backend.keuanganku.dto.response.GeneralResponse;
import com.andreas.backend.keuanganku.model.Pengguna;
import com.andreas.backend.keuanganku.service.PenggunaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/secure/pengguna")
@RequiredArgsConstructor
public class PenggunaController {

    private final PenggunaService penggunaService;

    /**
     * Mengambil detail pengguna yang sedang login.
     *
     * @param userId ID pengguna dari token (injected via @CurrentUserId)
     * @return Detail pengguna berupa ID, nama, dan email
     */
    @GetMapping("/detail-pengguna")
    public ResponseEntity<?> getDetailPengguna(@CurrentUserId UUID userId) {
        Pengguna pengguna = penggunaService.getById(userId);

        return ResponseEntity.ok(Map.of(
                "id", pengguna.getId(),
                "nama", pengguna.getNama(),
                "email", pengguna.getEmail()
        ));
    }

    /**
     * Memperbarui nama dan/atau email pengguna.
     *
     * @param idPengguna ID pengguna saat ini
     * @param request Payload berisi nama atau email baru
     * @return Respon sukses jika berhasil memperbarui
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateNamaEmail(
            @CurrentUserId UUID idPengguna,
            @Valid @RequestBody UpdatePenggunaRequest request
    ) {
        penggunaService.updateNamaAtauEmail(idPengguna, request);
        return ResponseEntity.ok(new GeneralResponse<>("Berhasil update pengguna", null, true));
    }

    /**
     * Mengubah password pengguna.
     *
     * @param idPengguna ID pengguna saat ini
     * @param request Payload berisi password lama dan password baru
     * @return Respon sukses jika password berhasil diubah
     */
    @PutMapping("/ubah-password")
    public ResponseEntity<?> ubahPassword(
            @CurrentUserId UUID idPengguna,
            @Valid @RequestBody UbahPasswordRequest request
    ) {
        penggunaService.ubahPassword(idPengguna, request);
        return ResponseEntity.ok(new GeneralResponse<>("Password berhasil diubah", null, true));
    }
}
