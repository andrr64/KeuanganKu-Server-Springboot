package com.andreas.backend.keuanganku.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTransferRequest {
    private String tanggal; // format: dd/MM/yyyy HH:mm
    private String catatan;
}
