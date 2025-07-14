package com.andreas.backend.keuanganku.service;

import java.util.List;
import java.util.UUID;

import com.andreas.backend.keuanganku.dto.request.TransferRequest;
import com.andreas.backend.keuanganku.dto.request.UpdateTransferRequest;
import com.andreas.backend.keuanganku.dto.response.DetailTransferResponse;
import com.andreas.backend.keuanganku.dto.response.TransferResponse;

public interface TransferService {

    TransferResponse lakukanTransfer(UUID idPengguna, TransferRequest request);

    List<TransferResponse> getAllTransfer(
            UUID idPengguna,
            UUID idAkun,
            String startDate,
            String endDate,
            String sort
    );

    DetailTransferResponse getById(UUID idPengguna, UUID idTransfer);
    void deleteTransfer(UUID idPengguna, UUID idTransfer);
    TransferResponse updateTransfer(UUID idPengguna, UUID idTransfer, UpdateTransferRequest request);

}