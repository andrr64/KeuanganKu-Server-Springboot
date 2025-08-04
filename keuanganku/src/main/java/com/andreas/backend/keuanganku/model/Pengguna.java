package com.andreas.backend.keuanganku.model;

import com.andreas.backend.keuanganku.config.TimeConfig;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Pengguna {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nama;

    @Column(nullable = false, length = 150, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "dibuat_pada", columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false)
    private OffsetDateTime dibuatPada;

    @PrePersist
    protected void onCreate() {
        dibuatPada = OffsetDateTime.now(TimeConfig.SERVER_TIME_ZONE_OFFSET);
    }
}