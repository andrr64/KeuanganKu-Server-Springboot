package com.andreas.backend.keuanganku.dto.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class PenggunaResponse {
    private final UUID id;
    private final String nama;
    private final String email;

}
