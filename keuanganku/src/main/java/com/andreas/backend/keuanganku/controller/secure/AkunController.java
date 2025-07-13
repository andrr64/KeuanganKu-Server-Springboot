package com.andreas.backend.keuanganku.controller.secure;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.andreas.backend.keuanganku.annotation.CurrentUserId;
import com.andreas.backend.keuanganku.dto.request.AkunRequest;
import com.andreas.backend.keuanganku.dto.request.UpdateNamaAkunRequest;
import com.andreas.backend.keuanganku.dto.response.AkunResponse;
import com.andreas.backend.keuanganku.dto.response.GeneralResponse;
import com.andreas.backend.keuanganku.model.Akun;
import com.andreas.backend.keuanganku.service.AkunService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/secure/akun")
@RequiredArgsConstructor
public class AkunController {

    private final AkunService akunService;

    @PostMapping
    public ResponseEntity<?> tambahAkun(
            @CurrentUserId UUID idPengguna,
            @Valid @RequestBody AkunRequest akunRequest
    ) {
        akunService.tambahAkun(idPengguna, akunRequest);
        return ResponseEntity.ok(new GeneralResponse<>("Akun berhasil ditambahkan"));
    }

    @PutMapping("/update-nama/{id_akun}")
    public ResponseEntity<?> updateNamaAkun(
            @CurrentUserId UUID idPengguna,
            @PathVariable("id_akun") UUID idAkun,
            @Valid @RequestBody UpdateNamaAkunRequest req
    ) {
        akunService.updateNamaAkun(idPengguna, idAkun, req.getNama());
        return ResponseEntity.ok(new GeneralResponse<>("Nama akun berhasil diperbarui"));
    }

    @GetMapping
    public ResponseEntity<?> getSemuaAkun(@CurrentUserId UUID idPengguna) {
        List<Akun> daftarAkun = akunService.getSemuaAkun(idPengguna);

        List<AkunResponse> responseList = daftarAkun.stream()
                .map(akun -> new AkunResponse(
                akun.getId(),
                akun.getNama(),
                akun.getSaldo(),
                akun.getDibuatPada()
        ))
                .toList();

        return ResponseEntity.ok(new GeneralResponse<>("Ok", responseList));
    }

}
