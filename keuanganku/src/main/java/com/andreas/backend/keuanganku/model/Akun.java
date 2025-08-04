package com.andreas.backend.keuanganku.model;

import com.andreas.backend.keuanganku.config.TimeConfig;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Akun {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private boolean aktif;

    @Column(nullable = false, length = 255)
    private String nama;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal saldo;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime dibuatPada;

    @ManyToOne
    @JoinColumn(name = "id_pengguna", nullable = false)
    private Pengguna pengguna;

    @PrePersist
    public void prePersist() {
        if (dibuatPada == null) {
            dibuatPada = OffsetDateTime.now(TimeConfig.SERVER_TIME_ZONE_OFFSET);
        }
    }
}
