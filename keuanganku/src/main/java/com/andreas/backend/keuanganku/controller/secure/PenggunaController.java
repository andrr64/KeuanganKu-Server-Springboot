package com.andreas.backend.keuanganku.controller.secure;

import com.andreas.backend.keuanganku.annotation.CurrentUserId;
import com.andreas.backend.keuanganku.dto.PrivateUserData;
import com.andreas.backend.keuanganku.dto.request.pengguna.UpdatePenggunaRequest;
import com.andreas.backend.keuanganku.dto.response.GeneralResponse;
import com.andreas.backend.keuanganku.model.Pengguna;
import com.andreas.backend.keuanganku.service.PenggunaService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/secure/pengguna")
@RequiredArgsConstructor
@Tag(name = "Pengguna", description = "API untuk manajemen akun pengguna yang sudah login.")
public class PenggunaController {

    private final PenggunaService penggunaService;

    @GetMapping("/me")
    public ResponseEntity<?> getDetailPengguna(
        @Parameter(description = "ID pengguna saat ini", hidden = true)
        @CurrentUserId UUID userId
    ) {
        Pengguna pengguna = penggunaService.getById(userId);
        PrivateUserData userData = new PrivateUserData(pengguna.getNama(), pengguna.getEmail());
        return ResponseEntity.ok(new GeneralResponse<>("Berhasil ambil data", userData, true));
    }

    @PutMapping
    public ResponseEntity<?> updateData(
        @Parameter(description = "ID pengguna saat ini", hidden = true)
        @CurrentUserId UUID idPengguna,
        @RequestBody UpdatePenggunaRequest request
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
}
