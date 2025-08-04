package com.andreas.backend.keuanganku.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class KategoriResponse {
    private UUID id;
    private String nama;
    private Integer jenis;
}
