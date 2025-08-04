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
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_pengguna", nullable = false)
    private Pengguna pengguna;

    @ManyToOne
    @JoinColumn(name = "id_dari_akun", nullable = false)
    private Akun dariAkun;

    @ManyToOne
    @JoinColumn(name = "id_ke_akun", nullable = false)
    private Akun keAkun;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal jumlah;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false)
    private OffsetDateTime tanggal;

    @Column(length = 255)
    private String catatan;

    @Column(name = "dibuat_pada", columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false)
    private OffsetDateTime dibuatPada;

    @PrePersist
    protected void onCreate() {
        tanggal = tanggal != null ? tanggal : OffsetDateTime.now(TimeConfig.SERVER_TIME_ZONE_OFFSET);
        dibuatPada = OffsetDateTime.now(TimeConfig.SERVER_TIME_ZONE_OFFSET);
    }
}