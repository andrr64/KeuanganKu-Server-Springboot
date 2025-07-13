package com.andreas.backend.keuanganku.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Transaksi {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private BigDecimal jumlah;
    private String catatan;
    private LocalDateTime tanggal;
    private LocalDateTime dibuatPada;

    @ManyToOne
    @JoinColumn(name = "id_pengguna")
    private Pengguna pengguna;

    @ManyToOne
    @JoinColumn(name = "id_akun")
    private Akun akun;

    @ManyToOne
    @JoinColumn(name = "id_kategori")
    private Kategori kategori;

}
