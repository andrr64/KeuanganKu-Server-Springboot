package com.andreas.backend.keuanganku.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Pengguna {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String nama;
    private String email;
    private String password;
    private LocalDateTime dibuatPada;
}
