package com.andreas.backend.keuanganku.controller.secure;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.andreas.backend.keuanganku.annotation.CurrentUserId;
import com.andreas.backend.keuanganku.dto.request.TransferRequest;
import com.andreas.backend.keuanganku.dto.response.GeneralResponse;
import com.andreas.backend.keuanganku.dto.response.TransferResponse;
import com.andreas.backend.keuanganku.service.TransferService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/secure/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    public ResponseEntity<?> transferFunds(
            @CurrentUserId UUID idPengguna,
            @Valid @RequestBody TransferRequest request
    ) {
        TransferResponse response = transferService.lakukanTransfer(idPengguna, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GeneralResponse<>("Transfer berhasil dilakukan", response));
    }

    @GetMapping
    public ResponseEntity<?> getAllTransfer(
            @CurrentUserId UUID idPengguna,
            @RequestParam(required = false) UUID idAkun,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "desc") String sort
    ) {
        List<TransferResponse> result = transferService.getAllTransfer(
                idPengguna, idAkun, startDate, endDate, sort);
        return ResponseEntity.ok(new GeneralResponse<>("OK", result));
    }

}
