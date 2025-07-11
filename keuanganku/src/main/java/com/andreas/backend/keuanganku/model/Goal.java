package com.andreas.backend.keuanganku.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String nama;
    private BigDecimal targetJumlah;
    private BigDecimal jumlahSaatIni;
    private LocalDate tenggat;

    @ManyToOne
    @JoinColumn(name = "id_pengguna")
    private Pengguna pengguna;
}