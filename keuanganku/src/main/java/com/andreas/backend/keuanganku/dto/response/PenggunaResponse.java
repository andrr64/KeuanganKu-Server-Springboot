package com.andreas.backend.keuanganku.dto.response;

import java.util.UUID;

public class PenggunaResponse {
    private UUID id;
    private String nama;
    private String email;

    public PenggunaResponse(UUID id, String nama, String email) {
        this.id = id;
        this.nama = nama;
        this.email = email;
    }

    public UUID getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getEmail() {
        return email;
    }
}
