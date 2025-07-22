package com.andreas.backend.keuanganku.controller.secure;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.andreas.backend.keuanganku.annotation.CurrentUserId;
import com.andreas.backend.keuanganku.dto.request.UbahPasswordRequest;
import com.andreas.backend.keuanganku.dto.request.UpdateAkunRequest;
import com.andreas.backend.keuanganku.dto.request.UpdatePenggunaRequest;
import com.andreas.backend.keuanganku.dto.response.GeneralResponse;
import com.andreas.backend.keuanganku.model.Pengguna;
import com.andreas.backend.keuanganku.repository.PenggunaRepository;
import com.andreas.backend.keuanganku.service.PenggunaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/secure/pengguna")
@RequiredArgsConstructor
public class PenggunaController {

    private final PenggunaService penggunaService;
    private final PenggunaRepository penggunaRepo;
    PasswordEncoder passwordEncoder;

    /**
     * Mengambil detail pengguna yang sedang login.
     *
     * @param userId ID pengguna dari token (injected via @CurrentUserId)
     * @return Detail pengguna berupa ID, nama, dan email
     */
    @GetMapping("/detail-pengguna")
    public ResponseEntity<?> getDetailPengguna(@CurrentUserId UUID userId) {
        Pengguna pengguna = penggunaService.getById(userId);

        Map<String, Object> data = Map.of(
                "nama", pengguna.getNama(),
                "email", pengguna.getEmail()
        );

        return ResponseEntity.ok(new GeneralResponse<>("Berhasil ambil data", data, true));
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

    @PutMapping()
    public ResponseEntity<?> updateData(
            @CurrentUserId UUID idPengguna,
            @RequestBody UpdateAkunRequest request
    ) {
        try {
            penggunaService.updateAkun(idPengguna, request);
            return ResponseEntity.ok(new GeneralResponse<>("Akun berhasil diperbarui.", null, true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new GeneralResponse<>(e.getMessage(), null, false));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new GeneralResponse<>(e.getMessage(), null, false));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new GeneralResponse<>(e.getMessage(), null, false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new GeneralResponse<>("Terjadi kesalahan: " + e.getMessage(), null, false)
            );
        }
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
