package com.andreas.backend.keuanganku.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;


@Data
@AllArgsConstructor
public class PenggunaResponse {
    private final UUID id;
    private final String nama;
    private final String email;

}
