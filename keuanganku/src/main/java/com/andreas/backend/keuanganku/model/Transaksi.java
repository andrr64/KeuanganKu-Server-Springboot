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
public class Transaksi {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal jumlah;

    @Column(name = "catatan", length = 255)
    private String catatan;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false)
    private OffsetDateTime tanggal;

    @Column(name = "dibuat_pada", columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false)
    private OffsetDateTime dibuatPada;

    @ManyToOne
    @JoinColumn(name = "id_pengguna", nullable = false)
    private Pengguna pengguna;

    @ManyToOne
    @JoinColumn(name = "id_akun", nullable = false)
    private Akun akun;

    @ManyToOne
    @JoinColumn(name = "id_kategori", nullable = true)
    private Kategori kategori;

    @PrePersist
    protected void onCreate() {
        dibuatPada = OffsetDateTime.now(TimeConfig.SERVER_TIME_ZONE_OFFSET);
    }
}