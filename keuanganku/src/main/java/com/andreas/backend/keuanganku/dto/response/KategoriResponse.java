package com.andreas.backend.keuanganku.dto.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KategoriResponse {
    private UUID id;
    private String nama;
    private Integer jenis;
}
