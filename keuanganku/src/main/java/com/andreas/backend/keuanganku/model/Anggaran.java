package com.andreas.backend.keuanganku.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
public class Anggaran {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private BigDecimal batasJumlah;
    private LocalDate tanggalMulai;
    private LocalDate tanggalSelesai;

    @ManyToOne
    @JoinColumn(name = "id_pengguna")
    private Pengguna pengguna;

    @ManyToOne
    @JoinColumn(name = "id_kategori")
    private Kategori kategori;
}