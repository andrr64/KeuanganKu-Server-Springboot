package com.andreas.backend.keuanganku.controller.secure;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.andreas.backend.keuanganku.annotation.CurrentUserId;
import com.andreas.backend.keuanganku.dto.request.AkunRequest;
import com.andreas.backend.keuanganku.model.Akun;
import com.andreas.backend.keuanganku.service.AkunService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/secure/akun")
@RequiredArgsConstructor
public class AkunController {

    private static final Logger log = LoggerFactory.getLogger(AkunController.class);

    private final AkunService akunService;

    @PostMapping
    public ResponseEntity<?> tambahAkun(
            @CurrentUserId UUID idPengguna,
            @Valid @RequestBody AkunRequest akunRequest
    ) {

        Akun akun = akunService.tambahAkun(idPengguna, akunRequest);

        return ResponseEntity.ok(
                new Object() {
                    public final UUID id = akun.getId();
                    public final String nama = akun.getNama();
                    public final Object saldo = akun.getSaldo();
                }
        );
    }
}
