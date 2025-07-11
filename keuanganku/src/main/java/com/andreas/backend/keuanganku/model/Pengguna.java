package com.andreas.backend.keuanganku.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Pengguna {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String nama;
    private String email;
    private String sandiHash;
    private LocalDateTime dibuatPada;

    // Getter
    public UUID getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getEmail() {
        return email;
    }

    public String getSandiHash() {
        return sandiHash;
    }

    public LocalDateTime getDibuatPada() {
        return dibuatPada;
    }

    // Setter
    public void setId(UUID id) {
        this.id = id;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSandiHash(String sandiHash) {
        this.sandiHash = sandiHash;
    }

    public void setDibuatPada(LocalDateTime dibuatPada) {
        this.dibuatPada = dibuatPada;
    }
}
