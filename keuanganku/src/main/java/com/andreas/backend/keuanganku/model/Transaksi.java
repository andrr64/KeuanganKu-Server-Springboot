package com.andreas.backend.keuanganku.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Transaksi {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private BigDecimal jumlah;
    @Column(name = "catatan", length = 255)
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
    @JoinColumn(name = "id_kategori", nullable= true)
    private Kategori kategori;

}
