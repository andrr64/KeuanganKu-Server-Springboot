package com.andreas.backend.keuanganku.model;

import com.andreas.backend.keuanganku.config.TimeConfig;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_pengguna", nullable = false)
    private Pengguna pengguna;

    @Column(nullable = false, length = 255)
    private String nama;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal target;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal terkumpul;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false)
    private OffsetDateTime tanggalTarget;

    @Column(nullable = false)
    private Boolean tercapai;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false)
    private OffsetDateTime dibuatPada;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime diperbaruiPada;

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now(TimeConfig.SERVER_TIME_ZONE_OFFSET);
        dibuatPada = now;
        diperbaruiPada = now;
    }

    @PreUpdate
    protected void onUpdate() {
        diperbaruiPada = OffsetDateTime.now(TimeConfig.SERVER_TIME_ZONE_OFFSET);
    }
}