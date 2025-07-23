package com.andreas.backend.keuanganku.controller.secure;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.andreas.backend.keuanganku.annotation.CurrentUserId;
import com.andreas.backend.keuanganku.dto.PrivateUserData;
import com.andreas.backend.keuanganku.dto.request.pengguna.UpdatePenggunaRequest;
import com.andreas.backend.keuanganku.dto.response.GeneralResponse;
import com.andreas.backend.keuanganku.model.Pengguna;
import com.andreas.backend.keuanganku.service.PenggunaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/secure/pengguna")
@RequiredArgsConstructor
@Tag(name = "Pengguna", description = "API untuk manajemen akun pengguna yang sudah login.")
public class PenggunaController {

    private final PenggunaService penggunaService;

    @Operation(
        summary = "Ambil detail pengguna saat ini",
        description = "Mengembalikan nama dan email pengguna berdasarkan ID pengguna saat ini."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Berhasil ambil data pengguna", 
            content = @Content(schema = @Schema(implementation = GeneralResponse.class))),
        @ApiResponse(responseCode = "404", description = "Pengguna tidak ditemukan", 
            content = @Content(schema = @Schema())),
        @ApiResponse(responseCode = "500", description = "Kesalahan server", 
            content = @Content(schema = @Schema()))
    })
    @GetMapping("/me")
    public ResponseEntity<?> getDetailPengguna(
        @Parameter(description = "ID pengguna saat ini", hidden = true)
        @CurrentUserId UUID userId
    ) {
        Pengguna pengguna = penggunaService.getById(userId);
        PrivateUserData userData = new PrivateUserData(pengguna.getNama(), pengguna.getEmail());
        return ResponseEntity.ok(new GeneralResponse<>("Berhasil ambil data", userData, true));
    }

    @Operation(
        summary = "Update data pengguna",
        description = "Memperbarui nama/email pengguna berdasarkan ID pengguna saat ini."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Berhasil update data pengguna", 
            content = @Content(schema = @Schema(implementation = GeneralResponse.class))),
        @ApiResponse(responseCode = "400", description = "Request tidak valid", 
            content = @Content(schema = @Schema())),
        @ApiResponse(responseCode = "401", description = "Tidak memiliki izin", 
            content = @Content(schema = @Schema())),
        @ApiResponse(responseCode = "404", description = "Pengguna tidak ditemukan", 
            content = @Content(schema = @Schema())),
        @ApiResponse(responseCode = "500", description = "Kesalahan server", 
            content = @Content(schema = @Schema()))
    })
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
